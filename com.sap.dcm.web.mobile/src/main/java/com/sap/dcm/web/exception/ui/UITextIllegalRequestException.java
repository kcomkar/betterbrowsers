package com.sap.dcm.web.exception.ui;

import com.sap.dcm.web.exception.AbstractDCMException;

/**
 * This exception will be thrown in case of illegal UI texts requests from the UI
 * @author D049641
 *
 */
public class UITextIllegalRequestException extends AbstractDCMException {
	private static final long serialVersionUID = -2943932152889957751L;
	
	private final static String messageText = "Illegal UI Text request. View missing or not valid";
	private final static String messageKey = "msg.ui.illegalRequest";

	/**
	 * Creates a new instance without root cause
	 */
	public UITextIllegalRequestException() {
	}

	/**
	 * Create a new instance with a root cause.
	 * @param cause = the root cause of this instance
	 */
	public UITextIllegalRequestException(Throwable cause) {
		super(cause);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageText()
	 */
	protected String getMessageText() {
		return messageText;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageKey()
	 */
	protected String getMessageKey() {
		return messageKey;
	}


	
}
