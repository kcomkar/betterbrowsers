package com.sap.dcm.web.ui.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.sap.dcm.web.ui.pages.model.AssignedPage;
import com.sap.dcm.web.user.mgmt.model.HanaUser;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UIInitializationResponse {
	private AssignedPage[] pages;
	private HanaUser user;

	public UIInitializationResponse() {
		super();
	}

	public UIInitializationResponse(AssignedPage[] pages, HanaUser user) {
		super();
		this.pages = pages;
		this.user = user;
	}

	public AssignedPage[] getPages() {
		return pages;
	}

	public void setPages(AssignedPage[] pages) {
		this.pages = pages;
	}

	public HanaUser getUser() {
		return user;
	}

	public void setUser(HanaUser user) {
		this.user = user;
	}
}
