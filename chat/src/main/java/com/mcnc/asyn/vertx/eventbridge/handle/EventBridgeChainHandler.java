package com.mcnc.asyn.vertx.eventbridge.handle;

import io.vertx.ext.web.handler.sockjs.BridgeEvent;

public interface EventBridgeChainHandler {
	boolean handle(BridgeEvent event) throws EventBridgeChainException;
}
