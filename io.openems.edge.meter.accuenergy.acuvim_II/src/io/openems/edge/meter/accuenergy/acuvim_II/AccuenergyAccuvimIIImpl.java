package io.openems.edge.meter.accuenergy.acuvim_II;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.bridge.modbus.api.ModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.FloatDoublewordElement;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.meter.api.MeterType;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Meter.Accuenergy.Acuvim_II", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class AccuenergyAccuvimIIImpl extends AbstractOpenemsModbusComponent implements AccuenergyAccuvimII,  ElectricityMeter, ModbusComponent, OpenemsComponent {

	@Reference
	private ConfigurationAdmin cm;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	private Config config = null;

	public AccuenergyAccuvimIIImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ElectricityMeter.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				AccuenergyAccuvimII.ChannelId.values() //
		);
	}

	@Activate
	private void activate(ComponentContext context, Config config) throws OpenemsException {
		if(super.activate(context, config.id(), config.alias(), config.enabled(), config.modbusUnitId(), this.cm, "Modbus",
				config.modbus_id())) {
			return;
		}
		this.config = config;
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	protected ModbusProtocol defineModbusProtocol() throws OpenemsException {
		
		return new ModbusProtocol(this, //
				
				new FC3ReadRegistersTask(16386, Priority.HIGH,
						m(ElectricityMeter.ChannelId.VOLTAGE_L1, new FloatDoublewordElement(16386))),
				new FC3ReadRegistersTask(16388, Priority.HIGH,
						m(ElectricityMeter.ChannelId.VOLTAGE_L2, new FloatDoublewordElement(16388))),
				new FC3ReadRegistersTask(16390, Priority.HIGH,
						m(ElectricityMeter.ChannelId.VOLTAGE_L3, new FloatDoublewordElement(16390))),
				new FC3ReadRegistersTask(17922, Priority.HIGH,
						m(ElectricityMeter.ChannelId.CURRENT_L2, new FloatDoublewordElement(17922))),
				new FC3ReadRegistersTask(17920, Priority.HIGH,
						m(ElectricityMeter.ChannelId.CURRENT_L1, new FloatDoublewordElement(17920))),
				new FC3ReadRegistersTask(17924, Priority.HIGH,
						m(ElectricityMeter.ChannelId.CURRENT_L3, new FloatDoublewordElement(17924))),
				new FC3ReadRegistersTask(16452, Priority.HIGH,
						m(ElectricityMeter.ChannelId.REACTIVE_POWER, new FloatDoublewordElement(16452)))
				);
	}

	@Override
	public String debugLog() {
		return "L:" + this.getCurrentL1().asString();
	}

	@Override
	public MeterType getMeterType() {
		return this.config.type();
	}
}
