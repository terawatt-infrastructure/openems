package io.openems.edge.meter.accuenergy.acurev2100;

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
import io.openems.edge.bridge.modbus.api.element.SignedWordElement;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.meter.api.MeterType;

@Designate(ocd = Config.class, factory = true)
@Component(//
        name = "Meter.Accuenergy.Acuview_2100", //
        immediate = true, //
        configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class MeterAccuenergyAcuRev2100Impl extends AbstractOpenemsModbusComponent implements MeterAccuenergyAcuRev2100, ElectricityMeter, ModbusComponent, OpenemsComponent {

    @Reference
    private ConfigurationAdmin cm;

    @Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
    protected void setModbus(BridgeModbus modbus) {
        super.setModbus(modbus);
    }

    private Config config = null;

    public MeterAccuenergyAcuRev2100Impl() {
        super(
                OpenemsComponent.ChannelId.values(), //
                ModbusComponent.ChannelId.values(), //
                ElectricityMeter.ChannelId.values(),//
                MeterAccuenergyAcuRev2100.ChannelId.values());
    }

    @Activate
    private void activate(ComponentContext context, Config config) throws OpenemsException {
        if (super.activate(context, config.id(), config.alias(), config.enabled(), config.modbusUnitId(), this.cm, "Modbus",
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
        // TODO implement ModbusProtocol
        return new ModbusProtocol(this, //
                new FC3ReadRegistersTask(0x2104, Priority.HIGH,
                        m(ElectricityMeter.ChannelId.REACTIVE_POWER, new SignedWordElement(0x2104))),
                new FC3ReadRegistersTask(0x2100, Priority.HIGH,
                        m(ElectricityMeter.ChannelId.CURRENT, new SignedWordElement(0x2100))),
                new FC3ReadRegistersTask(0x2010, Priority.HIGH,
                        m(ElectricityMeter.ChannelId.VOLTAGE, new SignedWordElement(0x2010)))


        );
    }

    @Override
    public String debugLog() {
        return "L:" + this.getReactivePower().asString();
    }

    @Override
    public MeterType getMeterType() {
        return this.config.type();
    }
}
