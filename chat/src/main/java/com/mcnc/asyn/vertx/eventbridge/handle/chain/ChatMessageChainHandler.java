package com.mcnc.asyn.vertx.eventbridge.handle.chain;

import java.util.Map;

import com.mcnc.asyn.vertx.eventbridge.handle.EventBridgeChainException;
import com.mcnc.asyn.vertx.eventbridge.handle.EventBridgeChainHandler;
import com.mcnc.asyn.vertx.websocket.util.VertxHolder;
import com.mcnc.asyn.vertx.websocket.util.WebSocketSessionHolder;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

public class ChatMessageChainHandler implements EventBridgeChainHandler {

	@Override
	public boolean handle(BridgeEvent event) throws EventBridgeChainException {
		boolean isHandle = Boolean.FALSE;
		
		if(BridgeEventType.PUBLISH == event.type()) {
			Vertx vertx = VertxHolder.getVertx();
			SockJSSocket sockJSSocket = event.socket();
			Map<String, Object> rawMessage = event.getRawMessage().getMap();
			
			String senderId = sockJSSocket.headers().get(WebSocketSessionHolder.USER_KEY);
			String address = (String) rawMessage.get("address");
			String msg = (String) rawMessage.get("body");
			
			
			vertx.eventBus().publish(address,
		            new JsonObject()
		                .put("message", msg)
		                .put("sender", senderId));
			
			isHandle = Boolean.TRUE;
			
		}
		
		return isHandle;
	}

}
