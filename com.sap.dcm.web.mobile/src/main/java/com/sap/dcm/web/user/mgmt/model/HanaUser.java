package com.sap.dcm.web.user.mgmt.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAX RS Model which is used to transfer a database user to the UI
 * 
 * @author D055272
 * 
 */
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class HanaUser {

	private String name;
	private String creator;
	private Date createTime;
	private Date lastSuccessfulConnect;
	private Date lastInvalidConnectAttempt;
	private Date passwordChangeTime;
	private long invalidConnectAttempts;
	private boolean adminGivenPassword;
	private boolean passwordChangeNeeded;
	private boolean userDeativated;
	private Date deactivationTime;

	public HanaUser() {
		super();
	}

	public HanaUser(String username, String creator, Date createTime,
			Date lastSuccessfulConnect, Date lastInvalidConnectAttempt,
			Date passwordChangeTime, long invalidConnectAttempts,
			boolean adminGivenPassword, boolean passwordChangeNeeded,
			boolean userDeativated, Date deactivationTime) {
		this.name = username;
		this.creator = creator;
		this.createTime = createTime;
		this.lastSuccessfulConnect = lastSuccessfulConnect;
		this.lastInvalidConnectAttempt = lastInvalidConnectAttempt;
		this.passwordChangeTime = passwordChangeTime;
		this.invalidConnectAttempts = invalidConnectAttempts;
		this.adminGivenPassword = adminGivenPassword;
		this.passwordChangeNeeded = passwordChangeNeeded;
		this.userDeativated = userDeativated;
		this.deactivationTime = deactivationTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public String getCreator() {
		return creator;
	}

	public Date getDeactivationTime() {
		return deactivationTime;
	}

	public long getInvalidConnectAttempts() {
		return invalidConnectAttempts;
	}

	public Date getLastInvalidConnectAttempt() {
		return lastInvalidConnectAttempt;
	}

	public Date getLastSuccessfulConnect() {
		return lastSuccessfulConnect;
	}

	public Date getPasswordChangeTime() {
		return passwordChangeTime;
	}

	public String getName() {
		return name;
	}

	public boolean isAdminGivenPassword() {
		return adminGivenPassword;
	}

	public boolean isPasswordChangeNeeded() {
		return passwordChangeNeeded;
	}

	public boolean isUserDeativated() {
		return userDeativated;
	}

	public void setAdminGivenPassword(boolean adminGivenPassword) {
		this.adminGivenPassword = adminGivenPassword;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setDeactivationTime(Date deactivationTime) {
		this.deactivationTime = deactivationTime;
	}

	public void setInvalidConnectAttempts(long invalidConnectAttempts) {
		this.invalidConnectAttempts = invalidConnectAttempts;
	}

	public void setLastInvalidConnectAttempt(Date lastInvalidConnectAttempt) {
		this.lastInvalidConnectAttempt = lastInvalidConnectAttempt;
	}

	public void setLastSuccessfulConnect(Date lastSuccessfulConnect) {
		this.lastSuccessfulConnect = lastSuccessfulConnect;
	}

	public void setPasswordChangeNeeded(boolean passwordChangeNeeded) {
		this.passwordChangeNeeded = passwordChangeNeeded;
	}

	public void setPasswordChangeTime(Date passwordChangeTime) {
		this.passwordChangeTime = passwordChangeTime;
	}

	public void setUserDeativated(boolean userDeativated) {
		this.userDeativated = userDeativated;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof HanaUser){
			if(this.getName().equals(((HanaUser)obj).getName()))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
