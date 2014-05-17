package com.rtc.communication.business;

public class JSONCommand {
	private String method; 
	private String data; 
	
	public JSONCommand(String method, String data) {
		this.method = method; 
		this.data = data; 
	} 
	
	public JSONCommand() {} 
	
	public String getMethod() {
		return this.method; 
	} 
	
	public String getData() {
		return this.data; 
	} 
}
