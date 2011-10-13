package com.sap.dcm.web.user.mgmt.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "assignedRoles")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssignedRoles {
	private List<HanaRole> roles;

	public AssignedRoles(){
		super();
	}
	
	public AssignedRoles(List<HanaRole> roles){
		super();
		this.roles = roles;
	}
	
	public List<HanaRole> getRoles() {
		return roles;
	}

	public void setRoles(List<HanaRole> roles) {
		this.roles = roles;
	}
	
	public void addRole(HanaRole role){
		if(this.roles == null)
			roles = new ArrayList<HanaRole>();
		this.roles.add(role);
	}
}
