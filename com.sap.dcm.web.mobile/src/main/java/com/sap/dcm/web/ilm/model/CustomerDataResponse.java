package com.sap.dcm.web.ilm.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.sap.dcm.web.core.model.ServiceResponse;
import com.sap.dcm.web.core.model.ServiceResponseMessage;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerDataResponse extends ServiceResponse{
	private boolean deleteAllowed;
	private String dunnings;

	public CustomerDataResponse() {
		super();
	}

	public CustomerDataResponse(List<ServiceResponseMessage> messages, String dunnings, boolean deleteAllowed) {
		super(messages);
		this.setDunnings(dunnings);
		this.deleteAllowed = deleteAllowed;
	}

	public boolean isDeleteAllowed() {
		return deleteAllowed;
	}

	public void setDeleteAllowed(boolean deleteAllowed) {
		this.deleteAllowed = deleteAllowed;
	}

	public String getDunnings() {
		return dunnings;
	}

	public void setDunnings(String dunnings) {
		this.dunnings = dunnings;
	}
}
