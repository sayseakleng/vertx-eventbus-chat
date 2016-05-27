package com.mcnc.async.vertx.spring.eventbus.handler;

import com.mcnc.async.vertx.spring.eventbus.dto.SocketDataModel;

public interface Handler{
	
	void handle(SocketDataModel data); 
}
