<<<<<<<< Updated upstream:io.openems.edge.meter.accuenergy.acuview_2100/src/io/openems/edge/meter/accuenergy/acuview_2100/MeterAccuenergyAcuview2100.java
package io.openems.edge.meter.accuenergy.acuview_2100;

import org.osgi.service.event.EventHandler;
========
package io.openems.edge.meter.accuenergy.acuRev2100;
>>>>>>>> Stashed changes:io.openems.edge.meter.accuenergy.acurev2100/src/io/openems/edge/meter/accuenergy/acuRev2100/MeterAccuenergyAcuRev2100.java

import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;

<<<<<<<< Updated upstream:io.openems.edge.meter.accuenergy.acuview_2100/src/io/openems/edge/meter/accuenergy/acuview_2100/MeterAccuenergyAcuview2100.java
public interface MeterAccuenergyAcuview2100 extends OpenemsComponent {
========
public interface MeterAccuenergyAcuRev2100 extends OpenemsComponent {
>>>>>>>> Stashed changes:io.openems.edge.meter.accuenergy.acurev2100/src/io/openems/edge/meter/accuenergy/acuRev2100/MeterAccuenergyAcuRev2100.java

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		;

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}

}
