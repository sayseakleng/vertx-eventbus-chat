package com.mcnc.async.vertx.spring.eventbus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcnc.async.vertx.spring.eventbus.dto.SocketDataModel;
import com.mcnc.async.vertx.spring.eventbus.dto.User;
import com.mcnc.async.vertx.spring.eventbus.handler.Handler;

public abstract class EventBusWebSocketHandler implements WebSocketHandler {
	private Map<String, Handler> handlers = new HashMap<>();
	private WebSocketSession session;
	private ObjectMapper oMapper = new ObjectMapper();
	private List<Timer> timers = new ArrayList<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		this.session = session;
		onOpen(session);
	}
	
	
	public abstract void onOpen(WebSocketSession session) throws IOException;
	

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		try {
			String payload = (String) message.getPayload();
			SocketDataModel socketDataModel = oMapper.readValue(payload, SocketDataModel.class);
			String address = socketDataModel.getAddress();
			
			Handler handler = handlers.get(address);
			
			handler.handle(socketDataModel);
			
			
		} catch (Exception e) {
		} 
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		session.close();
		this.session = null;
		System.out.println("**** Connection close ****");
		this.stopTimers();
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
	
	public void login(String username, String password, Handler handler) throws IOException {
		testConnection();
		
		Map<String, Object> map = new HashMap<>();
		map.put("type", "send");
		map.put("address", "vertx.basicauthmanager.login");
		map.put("body", new User(username, password));
		
		if(handler != null) {
			String key = UUID.randomUUID().toString();
			handlers.put(key, handler);
			map.put("replyAddress", key);
		}
		
		WebSocketMessage<?> sockMesage = new TextMessage(oMapper.writeValueAsString(map));
		session.sendMessage(sockMesage );
	}
	
	/**
	 * Subscribe to Topic
	 * @param addresss
	 * @param handler
	 * @throws IOException 
	 */
	public <T> void registerHandler(String addresss, Handler handler) throws IOException {
		testConnection();
		handlers.put(addresss, handler);
		
		Map<String, Object> map = new HashMap<>();
		map.put("type", "register");
		map.put("address", addresss);
		
		WebSocketMessage<?>  sockMesage = new TextMessage(oMapper.writeValueAsString(map));
		session.sendMessage(sockMesage);
	}
	
	private boolean testConnection() throws IOException {
		if(session.isOpen()) {
			return Boolean.TRUE;
		}
		else {
			throw new IOException("Session is already close");
		}
	}
	
	private void pingToServer() {
		
		try {
			WebSocketMessage<?>  sockMesage = new TextMessage("{\"type\":\"ping\"}");
			session.sendMessage(sockMesage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createPingTimer() {
		EventBusWebSocketHandler.this.pingToServer();
		
		int delay = 5000;// delay for 5 sec.
		int interval = 1000; // iterate every sec.
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				EventBusWebSocketHandler.this.pingToServer();
			}
		}, delay, interval);
		
		timers.add(timer);
	}
	
	public void stopTimers() {
		for (Timer timer : timers) {
			timer.cancel();
		}
		timers.clear();
	}
}
