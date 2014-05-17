/* 
 *  These lines will hold several functions that will be called by UI. 
 *  Most importants are: 
 *  - server: Defines the main methods to access websockets 
 *  - loadPage: Loads a template into index.html 
 *  - changeHash: This project was implemented using window.onHashChange() from Javascript, it allows a "template page" to be load 
 *  without refreshing all the UI. That's boosts performance making the render more fast. 
 *  - Show different info/error messages 
 *  - Is defined a loading div, this div will show while a loadPage() method is working, if the browser is fast enough, it wouldn't showing 
 *  - Several methods to send data to server. 
 *  - There's a method inside server, that it's very important and interesting, it will parse and proceed to update and do several action to UI.  
 */ 

var server_ip = "localhost"; 
var server = {
		connect : function() { 
			var location = "ws://"+server_ip+":8080/rtc/connection";     
			if (this._ws != null) { // Check if a socket is already open  
				this._ws.close(); 
			} 
			this._ws = new WebSocket(location);
			this._ws.onopen = this._onopen; 
			this._ws.onmessage = this._onmessage; 
			this._ws.onclose = this._onclose;
			this._ws.onerror = this._onerror; 
		}, 

		_onopen : function() {
			$("#connect").val("Disconnect");
			$("#connectlbl").text("Connectat"); 
		},

		_send : function(message) {
			if (this._ws)
				this._ws.send(message);
		},

		send : function(text) { 
			if (this.status() == WebSocket.OPEN) {
				server._send(text); 
			} else {
				alert("Connection error"); 
			} 
		},
		status: function() { 
			if (this._ws != null) {
				switch (this._ws.readyState) {
				  case WebSocket.CONNECTING:
					   	// alert("CONNECTING");
					   	return WebSocket.CONNECTING; 
					    break;
					  case WebSocket.OPEN:
						  // alert("OPEN");
						  return WebSocket.OPEN; 
					    break;
					  case WebSocket.CLOSING:
						  // alert("CLOSING");
						  WebSocket.CLOSING; 
					    break;
					  case WebSocket.CLOSED:
						  // alert("CLOSED");
						  this._ws = null;
						  return WebSocket.CLOSED; 
					    break;
					  default:
						  alert("error");
					  	  return "unknown"; 
					    break;
					}
			} else { 
				return WebSocket.CLOSED; 
			}
		},
		_onmessage : function(m) { 
			// console.log(m); 
			if (m.data.indexOf("onOpen") == -1) {  
				$("#toSend").val(m.data); 
				/* try { 
					var a = JSON.parse(m.data); 
					if (a.method === "r") { 
						$("#toSend").selectRange(a.position);  
						var text = $("#toSend").val(); 
						var beforeInsert = text.substring(0,a.position);
						var afterInsert = text.substring((a.position+(a.char.length)),text.length);
						// console.log("BEFORE:" + beforeInsert); 
						// console.log("AFTER:" + afterInsert);
						var finalString = beforeInsert + afterInsert; 
						// console.log("FINAL: " + finalString);  
						$("#toSend").val(finalString);  
					} else { 
						$("#toSend").selectRange(a.position);  
						var text = $("#toSend").val();  
						var beforeInsert = text.substring(0,a.position);
						var afterInsert; 
						if (a.char != "\b") {
							// When inserting chars need to insert char in position that passed by parameter 
							afterInsert = text.substring(a.position, text.length); 
						} else { 
							// When deleting chars current char it's a.position 
							afterInsert = text.substring(a.position+1, text.length); 
						} 
						console.log("BEFORE:" + beforeInsert); 
						console.log("AFTER:" + afterInsert);  
						var finalString = beforeInsert + a.char + afterInsert;
						console.log("FINAL: " + finalString);
						$("#toSend").val(finalString); 
					} 
				} catch (e) { 
					console.log(e); 
				} */ 
				$('#messageBox').append(m.data);
				$('#messageBox').append("<br/>");  
			}
		},

		_onclose : function(m) {
			$("#connect").val("Connect");
			$("#connectlbl").text("Desconnectat"); 
			if (this._ws)
				this._ws.close();
			this._ws = null; 
		}, 
		_onerror : function(m) {  
			if (this._ws)
				this._ws.close();
		} 
};  