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
package io.openems.impl.device.simulator;

import java.util.HashSet;
import java.util.Set;

import io.openems.api.channel.ConfigChannel;
import io.openems.api.device.nature.DeviceNature;
import io.openems.api.doc.ConfigInfo;
import io.openems.api.doc.ThingInfo;
import io.openems.api.exception.OpenemsException;
import io.openems.impl.protocol.simulator.SimulatorDevice;

@ThingInfo(title = "Simulator")
public class Simulator extends SimulatorDevice {

	/*
	 * Constructors
	 */
	public Simulator() throws OpenemsException {
		super();
	}

	/*
	 * Config
	 */
	@ConfigInfo(title = "Ess", description = "Sets the Ess nature.", type = SimulatorEss.class)
	public final ConfigChannel<SimulatorEss> ess = new ConfigChannel<>("ess", this);

	@ConfigInfo(title = "Grid-Meter", description = "Sets the grid meter nature.", type = SimulatorGridMeter.class)
	public final ConfigChannel<SimulatorGridMeter> gridMeter = new ConfigChannel<>("gridMeter", this);

	@ConfigInfo(title = "Production-Meter", description = "Sets the production meter nature.", type = SimulatorProductionMeter.class)
	public final ConfigChannel<SimulatorProductionMeter> productionMeter = new ConfigChannel<>("productionMeter", this);

	/*
	 * Methods
	 */
	@Override
	protected Set<DeviceNature> getDeviceNatures() {
		Set<DeviceNature> natures = new HashSet<>();
		if (ess.valueOptional().isPresent()) {
			natures.add(ess.valueOptional().get());
		}
		if (gridMeter.valueOptional().isPresent()) {
			natures.add(gridMeter.valueOptional().get());
		}
		if (productionMeter.valueOptional().isPresent()) {
			natures.add(productionMeter.valueOptional().get());
		}
		return natures;
	}

}
