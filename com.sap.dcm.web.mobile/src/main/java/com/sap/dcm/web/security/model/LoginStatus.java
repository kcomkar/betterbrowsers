package com.sap.dcm.web.security.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAX RS Model which is used to transfer Login state to the UI
 * @author D049641
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LoginStatus {


	/**
	 * Indicates if user is logged in or not
	 */
	private boolean loggedIn;
	
	/**
	 * the user name
	 */
	private String username;
	
	/**
	 * message returned from login service
	 */
	private String loginMessage;
	
	/**
	 * Redirect URL -> target url after login
	 */
	private String redirectUrl;
	
	/**
	 * Indicator if password should be changed
	 */
	private boolean passwordChangeRequired;

	/**
	 * Must have constructor.
	 */
	public LoginStatus() {
	}

	public LoginStatus(boolean loggedIn, String username, String loginMessage, String redirectUrl, boolean passwordChangeRequired) {
		this.loggedIn = loggedIn;
		this.username = username;
		this.loginMessage = loginMessage;
		this.redirectUrl = redirectUrl;
		this.passwordChangeRequired = passwordChangeRequired;
	}

	/**
	 * 
	 * @return the login state
	 */
	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	/**
	 * 
	 * @return the user name
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * 
	 * @return the login (error) message
	 */
	public String getLoginMessage() {
		return loginMessage;
	}

	/**
	 * 
	 * @return the redirect url
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	/**
	 *
	 * @return the indicator if password change is required or not
	 */
	public boolean isPasswordChangeRequired() {
		return passwordChangeRequired;
	}
}
