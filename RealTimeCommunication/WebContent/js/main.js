$(document).ready(function() { 
	$("#connect").click(function() { 
		server.connect();   
	}); 
	$("#send").click(function(){ 
		// Create JSON object  
		// var jsonStringConnection = {'method':'login','data':'username'}; 
		// Send it to server
		// server.send(JSON.stringify(jsonStringConnection));
		var text = $("#toSend").val(); 
		var jsonString = {'method':'message','data':text}; 
		server.send(JSON.stringify(jsonString)); 
	}); 
		$("#toSend").bind({
	/* keypress:function(keyEvt){
			// console.log(keyEvt); 
			console.log(1); 
			var cursorPosition = $(this).getCursorPosition(); 
			var keyCharCode = keyEvt.which; 
			if (keyCharCode>=32 && keyCharCode<=125 || keyCharCode == 13) { 
				// console.log("KEY: " + cursorPosition + "|CHAR:" + keyCharCode);
				var jsonString = {'method':'i','char':String.fromCharCode(keyCharCode),'position':cursorPosition};
				var parsedString = JSON.stringify(jsonString); 
				server.send(parsedString);  
			} 
		}, */ 
		textInput: function(keyEvt) {
			// console.log(keyEvt); 
			// console.log(2); 
			var cursorPosition = $(this).getCursorPosition(); 
			var jsonString = {'method':'i','char':String(keyEvt.originalEvent.data),'position':cursorPosition};
			var parsedString = JSON.stringify(jsonString); 
			server.send(parsedString);  
		}, 
		keydown: function(keyEvt) {
			// console.log(keyEvt); 
			// console.log(3); 
			var cursorPosition = $(this).getCursorPosition(); 
			var keyCharCode = keyEvt.which; 
			if (keyCharCode == 8) { 
				if (window.getSelection() != "") {
					var text = String(window.getSelection());  
					var jsonString = {'method':'r','char':text,'position':cursorPosition};
					var parsedString = JSON.stringify(jsonString); 
					server.send(parsedString); 
				} else {
					var jsonString = {'method':'i','char':String.fromCharCode(keyCharCode),'position':cursorPosition-1};
					var parsedString = JSON.stringify(jsonString); 
					server.send(parsedString);  					
				}
			} 
		} 
		});  
	});  
		/* 
		 $("#toSend").keypress(function(keyEvt){   
		
		}  
		 var maxCols = keyEvt.target.cols; 
		var keyCharCode = keyEvt.keyCode; 
		if (keyCharCode == 13) {
			colPosition = 0; 
			rowPosition += 1;
			console.log("COLUMN:" + colPosition+ "|ROW:" + rowPosition);  	
		} else if (keyCharCode == 8) {
			if (colPosition==0) {
				rowPosition -= 1; 
				var currentRowSize = $("#toSend").val().length; 
				colPosition = currentRowSize; 
			} else { 
				colPosition -= 1;
			} 
			console.log("COLUMN:" + colPosition+ "|ROW:" + rowPosition);
		} else { 
			colPosition += 1;
			var timeStamp = keyEvt.timestamp; 
			console.log("COLUMN:" + colPosition+ "|ROW:" + rowPosition);
			if (colPosition >= maxCols) {
				rowPosition += 1; 
				colPosition = 0; 
			} 
		} 
		
		}); */ 	


(function($, undefined) {  
    $.fn.getCursorPosition = function() {  
        var el = $(this).get(0);  
        var pos = 0;  
        if ('selectionStart' in el) {  
            pos = el.selectionStart;  
        } else if ('selection' in document) {  
            el.focus();  
            var Sel = document.selection.createRange();  
            var SelLength = document.selection.createRange().text.length;  
            Sel.moveStart('character', -el.value.length);  
            pos = Sel.text.length - SelLength;  
        }  
        return pos;  
    };   
})(jQuery);  

$.fn.selectRange = function(start, end) {
    if(!end) end = start; 
    return this.each(function() {
        if (this.setSelectionRange) {
            this.focus();
            this.setSelectionRange(start, end);
        } else if (this.createTextRange) {
            var range = this.createTextRange();
            range.collapse(true);
            range.moveEnd('character', end);
            range.moveStart('character', start);
            range.select();
        }
    });
}; 