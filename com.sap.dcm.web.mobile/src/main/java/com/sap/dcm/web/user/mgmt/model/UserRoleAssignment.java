package com.sap.dcm.web.user.mgmt.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAX RS Model which is used to transfer a database role to the UI
 * 
 * @author D055272
 * 
 */
@XmlRootElement(name = "userRole")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRoleAssignment {

	private RoleAssignment[] rolesAssignments;
	private HanaUser user;

	public UserRoleAssignment() {
		super();
	}

	public UserRoleAssignment(RoleAssignment[] rolesAssignments, HanaUser username) {
		this.rolesAssignments = rolesAssignments;
		this.user = username;
	}

	public RoleAssignment[] getRoleAssignments() {
		return rolesAssignments;
	}

	public HanaUser getUser() {
		return user;
	}

	public void setRoleAssignments(RoleAssignment[] rolesAssignments) {
		this.rolesAssignments = rolesAssignments;
	}

	public void setUser(HanaUser username) {
		this.user = username;
	}

}
