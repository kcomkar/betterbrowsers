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
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.FIELD)
public class HanaRole {
	
	private String name;
	private String description;

	public HanaRole() {

	}

	public HanaRole(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof HanaRole){
			if(this.getName().equals(((HanaRole)obj).getName()))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

}
