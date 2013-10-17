package com.sap.dcm.web.exception.security;

import com.sap.dcm.web.exception.AbstractDCMException;

/**
 * Exception in case of authentication errors
 * 
 * @author D049641
 * 
 */
public class AuthenticationException extends AbstractDCMException {

	private static final long serialVersionUID = 2414125561385485616L;

	public enum TYPE {
		NO_DATA_SOURCE, NO_VALID_PRINCIPAL, NOT_LOGGED_IN
	};

	private final String[] keys = { "msg.sec.noDataSource",
			"msg.sec.noValidPrincipal", "msg.sec.notLoggedIn" };
	private final String[] messages = {
			"No data source attached to principal. Please login again.",
			"User is not a valid DCM principal. Please login again.", "User not logged in to DCM web application. Please login." };

	private final TYPE type;

	/**
	 * Creates a new instance of the given type
	 * 
	 * @param type
	 */
	public AuthenticationException(final TYPE type) {
		super();
		this.type = type;
	}

	/**
	 * Creates a new instance of the given type and the given root cause
	 * 
	 * @param type
	 * @param cause
	 */
	public AuthenticationException(final TYPE type, Throwable cause) {
		super(cause);
		this.type = type;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageText()
	 */
	protected String getMessageText() {
		return keys[type.ordinal()];
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageKey()
	 */
	protected String getMessageKey() {
		return messages[type.ordinal()];
	}

}
