package com.rtc.communication.websocket; 

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList; 
import org.eclipse.jetty.websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

import com.rtc.communication.business.Business;

/*
 * That's the entry point, servlet call this method when a message is received 
 * @param connection Jetty websocket connection @see Jetty 8.x API  
 * @param username That store username of the user that send the message 
 * @param isValidConnection This variable stores if the current connection is valid, that means if the user has logged on the system correctly 
 */
public class CommunicationWebsocketBusiness implements WebSocket.OnTextMessage
  {
	public Connection connection;
	private String username = ""; 
	private boolean isValidConnection = false; 
	private String text = "";  
	/* 
	 * Handle websocket open a connection  
	 * (non-Javadoc)
	 * @see org.eclipse.jetty.websocket.WebSocket#onOpen(org.eclipse.jetty.websocket.WebSocket.Connection)
	 */
    @Override
    public void onOpen(Connection connection) {
            this.connection = connection;
            // this.connection.setMaxIdleTime(5*60*1000); 
            this.connection.setMaxIdleTime(0);
            System.out.println("onOpen: " + connection + "|Time: " + new Date());
            try { 
				connection.sendMessage("onOpen: " + connection); 
				CopyOnWriteArrayList<CommunicationWebsocketBusiness> a = new CopyOnWriteArrayList<CommunicationWebsocketBusiness>(); 
				a.add(this); 
				CommunicationWebsocketServlet.connections.put(connection.toString(),a); 
			} catch (IOException e) {
				this.connection.close(); 
			} catch (Exception ex) {
				this.connection.close(); 
			}
    } 

    /* 
     * Handle when a message is sent from client to server through websocket connection. 
     * <p> 
     * </p> 
     * (non-Javadoc)
     * @see org.eclipse.jetty.websocket.WebSocket.OnTextMessage#onMessage(java.lang.String)
     */
    @Override
    public void onMessage(String queryString) { 
    	new Business(queryString, this); 
    	System.out.println("empty username sended: " + queryString);
    	JSONObject query; 
    	String finalString = ""; 
    	try { 
			query = new JSONObject(queryString);
			String method = query.getString("method"); 
			String character = query.getString("char"); 
			int position = query.getInt("position");
			String beforeInsert = ""; 
			String afterInsert=""; 
			if (position != -1) { 
				beforeInsert = text.substring(0,position);
				if (method.equals("r")) { 
					afterInsert = text.substring((position+(character.length())),text.length());
					finalString = beforeInsert + afterInsert;
				} 
				else {
					if (!character.equals("\b")) {
						// When inserting chars need to insert char in position that passed by parameter 
						afterInsert = text.substring(position, text.length());
						finalString = beforeInsert + character + afterInsert;
					} else { 
						// When deleting chars current char it's a.position 
						afterInsert = text.substring(position+1, text.length());
						finalString = beforeInsert + afterInsert;
					}
				}  
				text = finalString; 
			} 
		} catch (JSONException e) {
			e.printStackTrace();
		}  
    	this.sendMessageToAllConnections(finalString); 
    } 

    public void sendMessageToAllConnections(String message) {
    	Enumeration<String> keys = CommunicationWebsocketServlet.connections.keys();
    	while (keys.hasMoreElements()) {
    		String key = keys.nextElement(); 
    		CopyOnWriteArrayList<CommunicationWebsocketBusiness> conn = CommunicationWebsocketServlet.connections.get(key);
    		for (int i=0; i<conn.size(); i++) {
    			CommunicationWebsocketBusiness c = conn.get(i); 
    			try { 
    				if (c.connection != this.connection) {
    					c.connection.sendMessage(message);
    					c.text = message; 
    				} 
				} catch (IOException e) {
					e.printStackTrace(); 
				} 
    		}
    	}
    }
    
    /* 
     * Handle close connection 
     * (non-Javadoc)
     * @see org.eclipse.jetty.websocket.WebSocket#onClose(int, java.lang.String)
     */
    @Override
    public void onClose(int closeCode, String message) {
    		System.out.println("onClose: " + closeCode + " :"+ message + " :" + this.connection + " :" + new Date()); 
    		this.removeConnection(); 
    		this.connection.close(); 
    } 
    
    /* 
     * Remove this class, that is stored in a class variable in CommunicationServlet, called connections 
     */
    private void removeConnection() {
    	Enumeration<String> keys = CommunicationWebsocketServlet.connections.keys();
    	while (keys.hasMoreElements()) {
    		String key = keys.nextElement(); 
	    	CopyOnWriteArrayList<CommunicationWebsocketBusiness> conn = CommunicationWebsocketServlet.connections.get(key);
			for (int i=0; i<conn.size(); i++) {
				CommunicationWebsocketBusiness c = conn.get(i); 
				if (c.connection == this.connection) {
					conn.remove(c); 
					System.out.println("Closed conn"); 
				} 
			} 
    	}  
    } 
    
    /* 
     * Add or replace connection from CommunicationServlet static variable connections
     */
    private void addOrReplaceConnection(String username) {
    	// Check if exists any connection 
		if (CommunicationWebsocketServlet.connections.get(this.username) != null) {
			// Get connection list and add new one 
			CopyOnWriteArrayList<CommunicationWebsocketBusiness> connectionList = CommunicationWebsocketServlet.connections.get(this.username);  
			connectionList.add(this); 
			CommunicationWebsocketServlet.connections.remove(this.username); 
			CommunicationWebsocketServlet.connections.put(this.username, connectionList); 
		} else { 
			// Create new connection 
			CopyOnWriteArrayList<CommunicationWebsocketBusiness> connectionList = new CopyOnWriteArrayList<CommunicationWebsocketBusiness>(); 
			connectionList.add(this); 
			CommunicationWebsocketServlet.connections.put(this.username, connectionList);
		}
    }
     
    /* 
     * Check if a user is connected, loops through connection list connections 
     * It's necessary when want to send message to opponent, ex: p1 makes move then if user is connected update with p1's move 
     */
/* 	public static boolean userIsConnected(Player p) {
		// Check if user is connected 
		if (CommunicationWebsocketServlet.connections.containsKey(p.getId())) {
			return true; 
		} 
		return false; 
	} 
	*/ 
	/* 
	 * Send back result of a command sended by user to server 
	 * Necessary when in multiple session return the result of an action only to the connection that has generated this action and not to all 
	 * sessions that an user has open. ex: p1 has 2 sessions opened, then makes an invalid move from connection 1, then send back Error type 
	 * message to connection 1 and not to connection 2. 
	 * When a user makes an action, result it's created, this result can be a message of type "Info" or "Error", theses types of message 
	 * cannot be sended to all users connections. 
	 */
	public void returnResultToConnection(String message) {
		try {
			if (this.connection.isOpen()) {
				this.connection.sendMessage(message); 
			} 
		} catch (IOException e) {
			this.connection.close(); 
		} 
	}
	
	/* 
	 * Send an arbitrary message to any player to all connections 
	 * Backward that "returnResultToConnection" method, it's called when a user makes an action and it's need to be update opponent and 
	 * same user connections. ex: user makes a move through an arbitrary connection and must be update board from opponent,  and board from other 
	 * users connection 
	 */ 
	public static boolean sendMessageToConnection(String playerId, String message) {
		boolean result = true; 
		if (CommunicationWebsocketServlet.connections.containsKey(playerId)) {
			CopyOnWriteArrayList<CommunicationWebsocketBusiness> connectionList = CommunicationWebsocketServlet.connections.get(playerId);
			for (CommunicationWebsocketBusiness cws : connectionList) {
				try {
					if (cws.connection.isOpen()) {
						cws.connection.sendMessage(message);
					} else {
						cws.connection.close(); 
					}
				} catch (IOException e) { 
					cws.connection.close(); 
				} 
			}
		} 
		return result; 
	} 
} 