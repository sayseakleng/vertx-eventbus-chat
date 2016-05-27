package com.mcnc.asyn.vertx.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.mcnc.asyn.vertx.HttpServer;
import com.mcnc.asyn.vertx.PubSubSockJsServer;
import com.mcnc.asyn.vertx.spring.SpringConfiguration;
import com.mcnc.asyn.vertx.websocket.util.VertxHolder;

import io.vertx.core.Vertx;


public class Application {
	
	public static void main(String[] arg) {
		Vertx vertx = Vertx.vertx();
		VertxHolder.setVertx(vertx);
		@SuppressWarnings("resource")
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		
		HttpServer httpServer = context.getBean(HttpServer.class);
		vertx.deployVerticle(httpServer);
		
		PubSubSockJsServer pubSubSockJsServer = context.getBean(PubSubSockJsServer.class);
		vertx.deployVerticle(pubSubSockJsServer);
	}
}
