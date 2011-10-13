package com.sap.dcm.web.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceResponseMessage {
	private String messageText;
	private boolean errorIndicator;
	
	public ServiceResponseMessage(){
		super();
	}
	
	public ServiceResponseMessage(String messageText, boolean errorIndicator){
		this.setMessageText(messageText);
		this.setErrorIndicator(errorIndicator);
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public boolean isErrorIndicator() {
		return errorIndicator;
	}

	public void setErrorIndicator(boolean errorIndicator) {
		this.errorIndicator = errorIndicator;
	}
}
