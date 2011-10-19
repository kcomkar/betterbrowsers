package com.sap.dcm.mobile.service.login;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.sap.dcm.web.security.model.LoginStatus;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MobLoginStatus extends LoginStatus {

	public MobLoginStatus(){
		// must has no-arg constructor for serialization
	}
	public MobLoginStatus(boolean loggedIn, String username,
			String loginMessage, String redirectUrl,
			boolean passwordChangeRequired, Set<String> roles) {
		super(loggedIn, username, loginMessage, redirectUrl, passwordChangeRequired);
		this.roles = roles;
	}

	private Set<String> roles = new HashSet<String>();

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
}
