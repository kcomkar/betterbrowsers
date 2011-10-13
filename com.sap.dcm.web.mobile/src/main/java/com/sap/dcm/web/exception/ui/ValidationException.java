package com.sap.dcm.web.exception.ui;

import com.sap.dcm.web.exception.AbstractDCMException;

/**
 * Exception class for data validation. The exception text will be generated
 * from the attribute name and the localized message is derived from the message
 * key
 * 
 * @author D049641
 * 
 */
public class ValidationException extends AbstractDCMException {
	private static final long serialVersionUID = 4490709700491725918L;
	/**
	 * The message key of the validation exception
	 */
	private String messageKey;
	/**
	 * The Ui attribute name of the validation exception
	 */
	private String attribute;

	/**
	 * Initializes a new Instance of Validation Exception for the given
	 * attribute and message key.
	 * 
	 * @param messageKey
	 *            = the localized message key
	 * @param attribute
	 *            = the effected attribute
	 */
	public ValidationException(String messageKey, String attribute) {
		this.messageKey = messageKey;
		this.attribute = attribute;
	}

	/**
	 * Initializes a new Instance of Validation Exception for the given
	 * attribute and message key by providing a root cause of this exception.
	 * 
	 * @param messageKey
	 *            = the localized message key
	 * @param attribute
	 *            = the effected attribute
	 * @param e
	 *            = the root cause
	 */
	public ValidationException(String messageKey, String attribute, Throwable e) {
		super(e);
		this.messageKey = messageKey;
		this.attribute = attribute;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageText()
	 */
	protected String getMessageText() {
		String a = attribute.substring(0, 1).toUpperCase()
				+ attribute.substring(1);
		return a + " not valid.";
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageKey()
	 */
	protected String getMessageKey() {
		return messageKey;
	}

}
