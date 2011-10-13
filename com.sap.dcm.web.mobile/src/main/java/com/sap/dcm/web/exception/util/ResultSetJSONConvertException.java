package com.sap.dcm.web.exception.util;

import com.sap.dcm.web.exception.AbstractDCMException;

/**
 * Exception class which is thrown in case of conversion errors between DB
 * Result Set and JSON.
 * 
 * @author D049641
 * 
 */
public class ResultSetJSONConvertException extends AbstractDCMException {
	private static final long serialVersionUID = -5506519341804983134L;

	/**
	 * create a new default instance without root cause
	 */
	public ResultSetJSONConvertException() {
		super();
	}

	/**
	 * create a new instance for the given root cause
	 * @param cause the root cause of this exception
	 */
	public ResultSetJSONConvertException(Throwable cause) {
		super(cause);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageText()
	 */
	protected String getMessageText() {
		return "Failed to convert result set into JSON.";
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageKey()
	 */
	protected String getMessageKey() {
		return "msg.util.jsonConvertFailed";
	}

}
