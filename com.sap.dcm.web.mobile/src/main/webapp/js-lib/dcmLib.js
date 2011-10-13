/**
 * Parses the url parameters and returns the parameters as array
 * 
 * @returns {Array} array of url parameters
 */
function getUrlVars() {
	var vars = [], hash;
	var hashes = window.location.href.slice(
			window.location.href.indexOf('?') + 1).split('&');
	for ( var i = 0; i < hashes.length; i++) {
		hash = hashes[i].split('=');
		vars.push(hash[0]);
		vars[hash[0]] = hash[1];
	}
	return vars;
}

/**
 * Function to open a busy indicator
 * @param iDuration = the duaration
 * @param iDelay = the delay
 */
function showBusyIndicator(iDuration, iDelay) {
	sap.ui.core.BusyIndicator.show(iDelay);
	if (iDuration && iDuration > 0) {
		window.setTimeout(hideBusyIndicator, iDuration);
	}
}

/**
 * Function to serialize a json into a string representation
 * @param obj the json which should be converted
 * @returns the string representation
 */
function serializeJSON(obj) {
    var t = typeof (obj);
    if (t != "object" || obj === null) {
        // simple data type
        if (t == "string") obj = '"'+obj+'"';
        return String(obj);
    } else {
        // recurse array or object
        var n, v, json = [], arr = (obj && obj.constructor == Array);
        for (n in obj) {
            v = obj[n]; t = typeof(v);
            if (t == "string") v = '"'+v+'"';
            else if (t == "object" && v !== null) v = JSON.stringify(v);
            json.push((arr ? "" : '"' + n + '":') + String(v));
        }
        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
    }
};


/**
 * adds the messages of the service response to the message bar
 * @param response the message response
 * @param messageBar the message bar
 */
function addServiceResponseToMessageBox(response, messageBar){
	var messages = new Array();
	var serviceResponse = null;
	if(response != null && typeof response != 'undefined' && typeof response != 'null')
		serviceResponse = response.serviceResponse;
	
	if ( typeof serviceResponse != "undefined" && typeof serviceResponse != "null" && serviceResponse != null){
		addMessagesToTextBox(serviceResponse.messages, messageBar);
	}else{
		messages.push( new sap.ui.commons.Message({
			type : sap.ui.commons.MessageType.Error,
			text : coreBundle.getText("msg.fatalError")
		}));
	}
	messageBar.addMessages(messages);
}

/**
 * Add a the list of backend messages to the message bar
 * @param messages the list of messages
 * @param messageBar the message bar
 */
function addMessagesToTextBox(messages, messageBar){
	var messageArray = new Array();
	if ( typeof messages != "undefined" && typeof messages != "null" && messages != null){
		if (messages instanceof Array) {
			for ( var i = 0; i < messages.length; i++) {
				var m = messages[i];
				addMessageToArray(messageArray, m);
			}
		} else {
			addMessageToArray(messageArray, messages);
		}
	}else{
		messageArray.push( new sap.ui.commons.Message({
			type : sap.ui.commons.MessageType.Error,
			text : coreBundle.getText("msg.fatalError")
		}));
	}
	messageBar.addMessages(messageArray);
}

/**
 * Adds the message of the backend response to the java script array of
 * messages
 * 
 * @param array =
 *            the java script array
 * @param messageObj =
 *            the backend message
 */
function addMessageToArray(array, messageObj) {
	if (messageObj.errorIndicator == true) {
		var msg = new sap.ui.commons.Message({
			type : sap.ui.commons.MessageType.Error,
			text : messageObj.messageText
		});
		array.push(msg);
	} else {
		var msg = new sap.ui.commons.Message({
			type : sap.ui.commons.MessageType.Success,
			text : messageObj.messageText
		});
		array.push(msg);
	}
}

/**
 * Initializes and returns the given ui text bundle
 * @param resource = the path to the bundle
 * @returns the ui text bundle
 */
function getBundle(resource){
	jQuery.sap.require("jquery.sap.resources");
	var locale = sap.ui.getCore().getConfiguration().getLanguage(); //TODO user specific language
	return jQuery.sap.resources({url : resource, locale: locale});
}

/**
 * Determines the root url of rest services
 * 
 * @param view =
 *            the view name
 * @returns {String} the url string
 */
function getRestRoot(view){
	var url = getRootUrl(view) +"rest";
	return url;
}

/**
 * determines the context root of the application
 * 
 * @param view =
 *            the current ui view
 * @returns context root as string
 */
function getContextRoot(view){
	var index = window.location.pathname.indexOf(view);
	var context = window.location.pathname.substring(1, index);
	return context;
}

/**
 * Determines the root url of the application
 * 
 * @param view =
 *            the current ui view
 * @returns {String} the url string
 */
function getRootUrl(view){
	var url = window.location.protocol + "//" + window.location.host+ "/" + getContextRoot(view);
	var last = url.charAt(url.length-1);
	if(last != "/")
		url = url + "/";
	return url;
}

/**
 * Initializes the ui texts
 */
function initUITexts(view) {
	// build url of rest service
	var url = getRestRoot(view) + "/ui/texts?view=" + view;
	// if the local is not initial add local as request parameter
	url = url + "&locale=" + getLocale();

	// trigger rest service and replace the ui texts
	$.ajax({
		  url: url,
		  dataType: 'json',
		  success: function(data) { mapUITexts(data); }
	});
}

/**
 * Maps the ui texts from backend to the ui controls. 
 * @param data = the ui texts received from java backend
 */
function mapUITexts(data){
	for ( var i = 0; i < data.uiTexts.length; i++) {
		//attribute selector
		var selector = "[uiText=" + data.uiTexts[i].key.replace(/\./g, '\\.')+"]";
		//extract the ui text
		var value = data.uiTexts[i].value;
		
		if (selector.substring(1, 4) == 'btn') {
			//for buttons the value attribute has to be changed
			$(selector).attr('value', value);
		} else {
			$(selector).each( function(){ 
				if($(this).hasClass("dialog")){
					//for dialogs the title attribute has to be set
					$(this).attr('title', value);
					var sel = '#ui-dialog-title-'+$(this).attr('id');
					$(sel).text(value);
				}else{
					//in all other cases the text value will be changed
					$(this).text(value); 
				}
			});
		}
	}
}

/**
 * Formates the date string (yyyymmdd) to an iso date string yyyy-mm-dd
 * 
 * @param date
 *            the date which should be converted
 * @returns {String} the date as string
 */
function formatDateToISO(date){
	var year = date.substring(0,4);
	var month = date.substring(4,6);
	var day = date.substring(6,8);
	return ""+year+"-"+month+"-"+day;
}

/**
 * Determines the locale of the client
 * 
 * @returns the client locale
 */
function getLocale(){
	if ( navigator ) {
	    if ( navigator.language ) {
	        return navigator.language;
	    }
	    else if ( navigator.browserLanguage ) {
	        return navigator.browserLanguage;
	    }
	    else if ( navigator.systemLanguage ) {
	        return navigator.systemLanguage;
	    }
	    else if ( navigator.userLanguage ) {
	        return navigator.userLanguage;
	    }else {
	    	return "en";
	    }
	}
}
