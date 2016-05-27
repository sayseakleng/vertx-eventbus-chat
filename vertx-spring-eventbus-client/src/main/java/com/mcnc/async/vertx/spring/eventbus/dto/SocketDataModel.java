package com.mcnc.async.vertx.spring.eventbus.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
public class SocketDataModel {
	private String type;
	private String address;
	private ObjectNode body;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public ObjectNode getBody() {
		return body;
	}
	
	public void setBody(ObjectNode body) {
		this.body = body;
	}
	@Override
	public String toString() {
		return "SocketDataModel [type=" + type + ", address=" + address + ", body=" + body + "]";
	}
}
