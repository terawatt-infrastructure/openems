<<<<<<<< Updated upstream:io.openems.edge.meter.accuenergy.acuview_2100/test/io/openems/edge/meter/accuenergy/acuview_2100/MyConfig.java
package io.openems.edge.meter.accuenergy.acuview_2100;
========
package io.openems.edge.meter.accuenergy.acuRev2100;
>>>>>>>> Stashed changes:io.openems.edge.meter.accuenergy.acurev2100/test/io/openems/edge/meter/accuenergy/acuRev2100/AccuenergyAccuviewIIConfig.java

import io.openems.common.utils.ConfigUtils;
import io.openems.edge.meter.api.MeterType;
import io.openems.common.test.AbstractComponentConfig;

@SuppressWarnings("all")
public class MyConfig extends AbstractComponentConfig implements Config {

	protected static class Builder {
		private String id;
		private String modbusId = null;
		private int modbusUnitId;

		private Builder() {
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}
		
		private MeterType type;
		public Builder setType(MeterType type) {
			this.type = type;
			return this;
		}

		public Builder setModbusId(String modbusId) {
			this.modbusId = modbusId;
			return this;
		}

		public Builder setModbusUnitId(int modbusUnitId) {
			this.modbusUnitId = modbusUnitId;
			return this;
		}

		public MyConfig build() {
			return new MyConfig(this);
		}
	}

	/**
	 * Create a Config builder.
	 * 
	 * @return a {@link Builder}
	 */
	public static Builder create() {
		return new Builder();
	}

	private final Builder builder;

	private MyConfig(Builder builder) {
		super(Config.class, builder.id);
		this.builder = builder;
	}

	@Override
	public String modbus_id() {
		return this.builder.modbusId;
	}

	@Override
	public String Modbus_target() {
		return ConfigUtils.generateReferenceTargetFilter(this.id(), this.modbus_id());
	}

	@Override
	public int modbusUnitId() {
		return this.builder.modbusUnitId;
	}

	@Override
	public MeterType type() {
		return this.builder.type;
	}

}