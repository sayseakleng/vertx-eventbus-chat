package com.mcnc.async.vertx.spring.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.mcnc.async.vertx.spring.eventbus.EventBusWebSocketHandler;
import com.mcnc.async.vertx.spring.eventbus.dto.SocketDataModel;
import com.mcnc.async.vertx.spring.eventbus.handler.Handler;

public class SockJSClient {

	@Test
	public void test() {
		List<Transport> transports = new ArrayList<>(2);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		transports.add(new RestTemplateXhrTransport());

		SockJsClient sockJsClient = new SockJsClient(transports);
		sockJsClient.doHandshake(new EventBusWebSocketHandler() {
			@Override
			public void onOpen(WebSocketSession session) throws IOException {
				final EventBusWebSocketHandler eventBusWebSocketHandler = this;
				
				this.login("leng", "password", new Handler() {
					@Override
					public void handle(SocketDataModel data) {
						System.out.println("**** All online users ****");
						System.out.println(data.getBody());
						
						
						// ping to server
						eventBusWebSocketHandler.createPingTimer();
						
						
					}
				});
				
				this.registerHandler("topic/chat/message", new Handler() {
					
					@Override
					public void handle(SocketDataModel data) {
						System.out.println("**** Retrieved chat ****");
						System.out.println(data.getBody());
					}
				});
			}
		}, "ws://localhost:8383/sockjs");
		
		
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
