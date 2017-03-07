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
package io.openems.impl.controller.asymmetric.fixvalue;

import java.util.Set;

import io.openems.api.channel.ConfigChannel;
import io.openems.api.controller.Controller;
import io.openems.api.doc.ConfigInfo;
import io.openems.api.doc.ThingInfo;
import io.openems.api.exception.InvalidValueException;
import io.openems.api.exception.WriteChannelException;

@ThingInfo(title = "Fixed active and reactive power (Asymmetric)", description = "Charges or discharges the battery with a predefined, fixed power. For asymmetric Ess.")
public class FixValueController extends Controller {

	/*
	 * Constructors
	 */
	public FixValueController() {
		super();
	}

	public FixValueController(String thingId) {
		super(thingId);
	}

	/*
	 * Config
	 */
	@ConfigInfo(title = "Ess", description = "Sets the Ess devices.", type = Ess.class)
	public final ConfigChannel<Set<Ess>> esss = new ConfigChannel<Set<Ess>>("esss", this);

	@ConfigInfo(title = "ActivePower L1", description = "Fixed active power for phase L1.", type = Long.class)
	public final ConfigChannel<Long> activePowerL1 = new ConfigChannel<>("activePowerL1", this);

	@ConfigInfo(title = "ActivePower L2", description = "Fixed active power for phase L2.", type = Long.class)
	public final ConfigChannel<Long> activePowerL2 = new ConfigChannel<>("activePowerL2", this);

	@ConfigInfo(title = "ActivePower L3", description = "Fixed active power for phase L3.", type = Long.class)
	public final ConfigChannel<Long> activePowerL3 = new ConfigChannel<>("activePowerL3", this);

	@ConfigInfo(title = "ReactivePower L1", description = "Fixed reactive power for phase L1.", type = Long.class)
	public final ConfigChannel<Long> reactivePowerL1 = new ConfigChannel<>("reactivePowerL1", this);

	@ConfigInfo(title = "ReactivePower L2", description = "Fixed reactive power for phase L2.", type = Long.class)
	public final ConfigChannel<Long> reactivePowerL2 = new ConfigChannel<>("reactivePowerL2", this);

	@ConfigInfo(title = "ReactivePower L3", description = "Fixed reactive power for phase L3.", type = Long.class)
	public final ConfigChannel<Long> reactivePowerL3 = new ConfigChannel<>("reactivePowerL3", this);

	/*
	 * Methods
	 */
	@Override
	public void run() {
		try {
			for (Ess ess : esss.value()) {
				try {
					ess.setActivePowerL1.pushWrite(activePowerL1.value());
				} catch (WriteChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ess.setActivePowerL2.pushWrite(activePowerL2.value());
				} catch (WriteChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ess.setActivePowerL3.pushWrite(activePowerL3.value());
				} catch (WriteChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ess.setReactivePowerL1.pushWrite(reactivePowerL1.value());
				} catch (WriteChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ess.setReactivePowerL2.pushWrite(reactivePowerL2.value());
				} catch (WriteChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ess.setReactivePowerL3.pushWrite(reactivePowerL3.value());
				} catch (WriteChannelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (InvalidValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
