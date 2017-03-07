/*******************************************************************************
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016, 2017 FENECON GmbH and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   FENECON GmbH - initial API and implementation and initial documentation
 *******************************************************************************/
package io.openems.core.utilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;

import io.openems.api.channel.Channel;
import io.openems.api.channel.ConfigChannel;
import io.openems.api.controller.IsThingMap;
import io.openems.api.controller.ThingMap;
import io.openems.api.device.nature.DeviceNature;
import io.openems.api.exception.ConfigException;
import io.openems.api.exception.OpenemsException;
import io.openems.api.exception.ReflectionException;
import io.openems.api.thing.Thing;
import io.openems.core.ClassRepository;
import io.openems.core.ThingRepository;

public class InjectionUtils {
	private final static Logger log = LoggerFactory.getLogger(InjectionUtils.class);

	/**
	 * Creates an instance of the given {@link Class}. {@link Object} arguments are optional.
	 *
	 * Restriction: this implementation tries only the first constructor of the Class.
	 *
	 * @param clazz
	 * @param args
	 * @return
	 * @throws ConfigException
	 */
	public static Object getInstance(Class<?> clazz, Object... args) throws ReflectionException {
		try {
			if (args.length == 0) {
				return clazz.newInstance();
			} else {
				Constructor<?>[] constructors = clazz.getConstructors();
				Constructor<?> constructor = null;
				for (Constructor<?> ct : constructors) {
					List<Class<?>> types = new ArrayList<>(Arrays.asList(ct.getParameterTypes()));
					if (types.size() == args.length) {
						boolean isType = true;
						for (Object arg : args) {
							boolean isAssignable = false;
							for (Class<?> type : types) {
								if (type.isAssignableFrom(arg.getClass())) {
									isAssignable = true;
								}
							}
							if (!isAssignable) {
								isType = false;
							}
						}
						if (isType) {
							constructor = ct;
						}
					}
				}
				if (constructor != null) {
					return constructor.newInstance(args);
				} else {
					throw new ReflectionException(
							"Unable to instantiate class [" + clazz.getName() + "] no matching constructor found.");
				}
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException
				| NullPointerException e) {
			e.printStackTrace();
			throw new ReflectionException("Unable to instantiate class [" + clazz.getName() + "]: " + e.getMessage());
		}
	}

	/**
	 * Creates a Thing instance of the given {@link Class}. {@link Object} arguments are optional.
	 *
	 * @param clazz
	 * @param args
	 * @return
	 * @throws CastException
	 * @throws ConfigException
	 * @throws ReflectionException
	 */
	public static Thing getThingInstance(Class<? extends Thing> clazz, Object... args) throws ReflectionException {
		Thing thing;
		try {
			thing = (Thing) InjectionUtils.getInstance(clazz, args);
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ReflectionException("Class [" + clazz.getName() + "] is not a Thing");
		}
		ClassRepository classRepository = ClassRepository.getInstance();
		classRepository.getThingConfigChannels(clazz).forEach((member, config) -> {
			try {
				Channel channel = getChannel(thing, member);
				((ConfigChannel<?>) channel).applyAnnotation(config);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException
					| OpenemsException e) {
				log.warn(e.getMessage());
			}
		});
		return thing;

	}

	private static Channel getChannel(Thing thing, Member member)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (member instanceof Field) {
			Field f = (Field) member;
			return (Channel) f.get(thing);
		} else {
			Method m = (Method) member;
			return (Channel) m.invoke(thing, null);
		}
	}

	/**
	 * Creates an instance of the given {@link Class}name. Uses {@link getThingInstance()} internally. {@link Object}
	 * arguments are optional.
	 *
	 * @param className
	 * @return
	 * @throws CastException
	 * @throws ConfigException
	 */
	@SuppressWarnings("unchecked")
	public static Thing getThingInstance(String className, Object... args) throws ReflectionException {
		Class<? extends Thing> clazz;
		try {
			clazz = (Class<? extends Thing>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ReflectionException("Class not found: [" + className + "]");
		}
		return getThingInstance(clazz, args);
	}

	public static Object getThingMapsFromConfig(ConfigChannel<?> channel, JsonElement j) throws ReflectionException {
		/*
		 * Get "Field" in Channels parent class
		 */
		Field field;
		try {
			field = channel.parent().getClass().getField(channel.id());
		} catch (NoSuchFieldException | SecurityException e) {
			throw new ReflectionException("Field for ConfigChannel [" + channel.address() + "] is not named ["
					+ channel.id() + "] in [" + channel.getClass().getSimpleName() + "]");
		}

		/*
		 * Get expected Object Type (List, Set, simple Object)
		 */
		Type expectedObjectType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
		if (expectedObjectType instanceof ParameterizedType) {
			expectedObjectType = ((ParameterizedType) expectedObjectType).getRawType();
		}
		Class<?> expectedObjectClass = (Class<?>) expectedObjectType;

		/*
		 * Get the ThingMap class
		 */
		Class<?> thingMapClass = channel.type();

		/*
		 * Get the referenced Thing class
		 */
		IsThingMap isThingMapAnnotation = thingMapClass.getAnnotation(IsThingMap.class);
		Class<? extends Thing> thingClass = isThingMapAnnotation.type();

		/*
		 * Prepare filter for matching Things
		 * - Empty filter: accept everything
		 * - Otherwise: accept only exact string matches on the thing id
		 */
		Set<String> filter = new HashSet<>();
		if (j.isJsonPrimitive()) {
			String id = j.getAsJsonPrimitive().getAsString();
			if (!id.equals("*")) {
				filter.add(id);
			}
		} else if (j.isJsonArray()) {
			j.getAsJsonArray().forEach(id -> filter.add(id.getAsString()));
		}

		/*
		 * Create ThingMap instance(s) for each matching Thing
		 */
		ThingRepository thingRepository = ThingRepository.getInstance();
		Set<Thing> matchingThings = thingRepository.getThingsAssignableByClass(thingClass);
		Set<ThingMap> thingMaps = new HashSet<>();
		for (Thing thing : matchingThings) {
			if (filter.isEmpty() || filter.contains(thing.id())) {
				ThingMap thingMap = (ThingMap) InjectionUtils.getInstance(thingMapClass, thing);
				thingMaps.add(thingMap);
			}
		}

		/*
		 * Prepare return
		 */
		if (thingMaps.isEmpty()) {
			throw new ReflectionException("No matching ThingMap found for ConfigChannel [" + channel.address() + "]");
		}

		if (Collection.class.isAssignableFrom(expectedObjectClass)) {
			if (Set.class.isAssignableFrom(expectedObjectClass)) {
				return thingMaps;
			} else if (List.class.isAssignableFrom(expectedObjectClass)) {
				return new ArrayList<>(thingMaps);
			} else {
				throw new ReflectionException("Only List and Set ConfigChannels are currently implemented, not ["
						+ expectedObjectClass + "]. ConfigChannel [" + channel.address() + "]");
			}
		} else {
			// No collection
			if (thingMaps.size() > 1) {
				throw new ReflectionException("Field for ConfigChannel [" + channel.address()
						+ "] is no collection, but more than one ThingMaps [" + thingMaps + "] is fitting for ["
						+ channel.id() + "] in [" + channel.getClass().getSimpleName() + "]");
			} else {
				return thingMaps.iterator().next();
			}
		}
	}

	/**
	 * Gets all important nature super interfaces and classes. This data is used by web client to visualize the data
	 * appropriately
	 *
	 * @param clazz
	 * @return
	 */
	public static Set<Class<?>> getImportantNatureInterfaces(Class<?> clazz) {
		Set<Class<?>> ifaces = new HashSet<>();
		if (clazz == null // at the top
				|| clazz.equals(DeviceNature.class) // we are at the DeviceNature interface
				|| !DeviceNature.class.isAssignableFrom(clazz) // clazz is not derived from DeviceNature
		) {
			return ifaces;
		}
		// myself
		ifaces.add(clazz);
		// super interfaces
		for (Class<?> iface : clazz.getInterfaces()) {
			if (DeviceNature.class.isAssignableFrom(iface)) {
				ifaces.addAll(getImportantNatureInterfaces(iface));
			}
		}
		// super classes
		ifaces.addAll(getImportantNatureInterfaces(clazz.getSuperclass()));
		return ifaces;
	}
}
