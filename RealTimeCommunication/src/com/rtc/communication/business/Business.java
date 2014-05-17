package com.rtc.communication.business;

import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONException;
import org.json.JSONObject;  
import com.rtc.communication.websocket.CommunicationWebsocketBusiness;
import com.rtc.communication.websocket.CommunicationWebsocketServlet;

public class Business { 
	private String queryString = ""; 
	private CommunicationWebsocketBusiness connection; 

	public Business(String queryString, CommunicationWebsocketBusiness communicationWebsocketBusiness){
		this.queryString = queryString; 
		this.connection = communicationWebsocketBusiness; 
	} 
	public void parseJsonMethod(String jsonQuery) {
		// Try to get json string as object 
		JSONObject jsonObject; 
		JSONCommand command=new JSONCommand(); 
		try { 
			// Convert JSON String to Object 
			jsonObject = new JSONObject(jsonQuery); 
			String method = jsonObject.getString("method"); 
			String data = jsonObject.getString("data"); 
			command = new JSONCommand(method, data);  
		} catch (JSONException e) {
			// Invalid JSON 
		}  
		// Parse command 
		if (command.getMethod().equals("login")) { 
			String user = command.getData(); 
			if (!CommunicationWebsocketServlet.connections.containsKey(user)) {
				CopyOnWriteArrayList<CommunicationWebsocketBusiness> userList = new CopyOnWriteArrayList<CommunicationWebsocketBusiness>(); 
				userList.add(this.connection); 
				CommunicationWebsocketServlet.connections.put(user, userList);  
			} else {
				// Get connection list and add 
				
			} 
		} 
	} 
} 
