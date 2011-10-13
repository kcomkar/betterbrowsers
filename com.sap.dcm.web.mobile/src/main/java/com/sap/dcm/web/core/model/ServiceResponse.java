package com.sap.dcm.web.core.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceResponse {
	private List<ServiceResponseMessage> messages;

	public ServiceResponse() {
		super();
	}

	public ServiceResponse(List<ServiceResponseMessage> messages) {
		super();
		this.messages = messages;
	}

	public List<ServiceResponseMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ServiceResponseMessage> messages) {
		this.messages = messages;
	}

	public void addMessage(ServiceResponseMessage message) {
		if (messages == null)
			messages = new ArrayList<ServiceResponseMessage>();
		messages.add(message);
	}
}
