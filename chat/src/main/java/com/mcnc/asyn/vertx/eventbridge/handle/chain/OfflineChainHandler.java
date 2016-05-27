package com.mcnc.asyn.vertx.eventbridge.handle.chain;

import com.mcnc.asyn.vertx.eventbridge.handle.EventBridgeChainException;
import com.mcnc.asyn.vertx.eventbridge.handle.EventBridgeChainHandler;
import com.mcnc.asyn.vertx.websocket.util.VertxHolder;
import com.mcnc.asyn.vertx.websocket.util.WebSocketSessionHolder;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

public class OfflineChainHandler implements EventBridgeChainHandler {

	@Override
	public boolean handle(BridgeEvent event) throws EventBridgeChainException {
		boolean isHandle = Boolean.FALSE;
		
		if(BridgeEventType.SOCKET_CLOSED == event.type()) {
			Vertx vertx = VertxHolder.getVertx();
			
			isHandle = Boolean.TRUE;
			
			SockJSSocket sockJSSocket = event.socket();
			
			// notify user offline
			String address = "topic/chat/offline";
			String userId = sockJSSocket.headers().get(WebSocketSessionHolder.USER_KEY);
			
			WebSocketSessionHolder.remove(userId);
			
			vertx.eventBus().publish(address,
		            new JsonObject()
		                .put("userId", userId));
			
		}
		
		return isHandle;
	}

}
