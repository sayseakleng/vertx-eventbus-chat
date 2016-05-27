package com.mcnc.asyn.vertx.eventbridge.handle.chain;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mcnc.asyn.vertx.eventbridge.handle.EventBridgeChainException;
import com.mcnc.asyn.vertx.eventbridge.handle.EventBridgeChainHandler;
import com.mcnc.asyn.vertx.spring.service.RequestLogService;
import com.mcnc.asyn.vertx.websocket.util.VertxHolder;
import com.mcnc.asyn.vertx.websocket.util.WebSocketSessionHolder;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

public class LoginChainHandler implements EventBridgeChainHandler {
	private static final Logger logger = LoggerFactory.getLogger(LoginChainHandler.class);
	
	@Autowired
	private RequestLogService connectionLogService;

	@Override
	public boolean handle(BridgeEvent event) throws EventBridgeChainException {
		boolean isHandle = Boolean.FALSE;
		
		if(BridgeEventType.SEND == event.type()) {
			Vertx vertx = VertxHolder.getVertx();
			SockJSSocket sockJSSocket = event.socket();
			
			Map<String, Object> rawMessage = event.getRawMessage().getMap();
			
			
			String replyAddress = (String) rawMessage.get("replyAddress");
			String address = (String) rawMessage.get("address");
			
			if("vertx.basicauthmanager.login".equals(address)) {
				@SuppressWarnings("unchecked")
				Map<String, String> credential = (Map<String, String>) rawMessage.get("body");
				String userId = credential.get("username");
				//String password = credential.get("password");
				
				if(userId == null || "".equals(userId)) {
					logger.warn("Connection rejected");
					sockJSSocket.close();
					
					throw new EventBridgeChainException(true, "No user attached");
				}
				else {
					
					boolean exists = WebSocketSessionHolder.exists(userId);
					if(exists) {
						throw new EventBridgeChainException(true, "User already registered");
					}
					
					sockJSSocket.headers().set(WebSocketSessionHolder.USER_KEY, userId);
					connectionLogService.logWebSocketConnection(sockJSSocket);
					
					WebSocketSessionHolder.add(userId, sockJSSocket);
					
					
					// publish there is a new user coming
					vertx.eventBus().publish("topic/chat/user",
				         new JsonObject()
							.put("userId", userId));
					
					// get all online and send back to 
					JsonObject json = new JsonObject()
		                .put("type", "login") // optional
		                .put("address", replyAddress)
		                .put("body", 
	                		new JsonObject()
	                			.put("result", true)
	                			.put("list", WebSocketSessionHolder.getUsers()));
					String data = json.toString();
					
					sockJSSocket.write(Buffer.buffer(data));
					
					isHandle = Boolean.TRUE;
				}
				
			}
			
		}
		return isHandle;
	}

}
