package com.sap.dcm.web.user.mgmt.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "roleassignment")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleAssignment {
	private boolean assigned;
	private HanaRole role;
	
	public RoleAssignment(){
		super();
	}
	
	public RoleAssignment(boolean assigned, HanaRole role) {
		super();
		this.assigned = assigned;
		this.role = role;
	}
	public boolean isAssigned() {
		return assigned;
	}
	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}
	public HanaRole getRole() {
		return role;
	}
	public void setRole(HanaRole role) {
		this.role = role;
	}
	
	
}
