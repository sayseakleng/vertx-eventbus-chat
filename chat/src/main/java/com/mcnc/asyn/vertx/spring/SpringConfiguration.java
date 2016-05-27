package com.mcnc.asyn.vertx.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.mcnc.asyn.vertx.eventbridge.handle.EventBridgeChain;
import com.mcnc.asyn.vertx.eventbridge.handle.chain.ChatMessageChainHandler;
import com.mcnc.asyn.vertx.eventbridge.handle.chain.LoginChainHandler;
import com.mcnc.asyn.vertx.eventbridge.handle.chain.OfflineChainHandler;

@Configuration
@ComponentScan("com.mcnc.asyn.vertx")
public class SpringConfiguration {
	@Bean
	public EventBridgeChain getEventBridgeChain() {
		EventBridgeChain eventBridgeChain = new EventBridgeChain();
		
		eventBridgeChain.regisger(getLoginChainHandler());
		eventBridgeChain.regisger(getChatMessageChainHandler());
		eventBridgeChain.regisger(getOfflineChainHandler());
		
		return eventBridgeChain;
	}
	
	
	@Bean
	public ChatMessageChainHandler getChatMessageChainHandler() {
		return new ChatMessageChainHandler();
	}
	
	
	@Bean
	public LoginChainHandler getLoginChainHandler() {
		return new LoginChainHandler();
	}
	
	@Bean
	public OfflineChainHandler getOfflineChainHandler() {
		return new OfflineChainHandler();
	}

}
