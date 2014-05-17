package com.rtc.communication.websocket; 
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList; 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;


public class CommunicationWebsocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = -7932990989224752168L;  
	// TODO replace and use DB solution 
	// What's next? create a group based connections 
	// Use this class as a black box 
	// Send instance on business object (thread-safe) 
	
	public final static ConcurrentHashMap<String, CopyOnWriteArrayList<CommunicationWebsocketBusiness>> connections = new ConcurrentHashMap<String, CopyOnWriteArrayList<CommunicationWebsocketBusiness>>(); 
	// GET method is used to establish a stream connection 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException { 
     //Implementation 
    } 
    // POST method is used to communicate with the server
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
    //Implementation
    } 
    /* 
     * This method allows websocket connection, see Jetty 8.X API for more information 
     * (non-Javadoc)
     * @see org.eclipse.jetty.websocket.WebSocketFactory.Acceptor#doWebSocketConnect(javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    @Override
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
            return new CommunicationWebsocketBusiness();
    }
} 
