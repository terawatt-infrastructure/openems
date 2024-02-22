package io.openems.backend.uiwebsocket.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import io.openems.common.websocket.AbstractWebsocketServer.DebugMode;

@ObjectClassDefinition(//
		name = "Ui.Websocket", //
		description = "Configures the websocket server for OpenEMS UI")
@interface Config {

	@AttributeDefinition(name = "Port", description = "The port of the websocket server.")
	int port() default 8082;

	@AttributeDefinition(name = "Number of Threads", description = "Pool-Size: the number of threads dedicated to handle the tasks")
	int poolSize() default 10;

	@AttributeDefinition(name = "Debug Mode", description = "Activates the debug mode")
	DebugMode debugMode() default DebugMode.OFF;

	String webconsole_configurationFactory_nameHint() default "Ui Websocket";

}
