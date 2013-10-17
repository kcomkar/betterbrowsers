package com.sap.dcm.web.exception.ui;

import com.sap.dcm.web.exception.AbstractDCMException;

/**
 * Exception class for missing UI Text ressources. The exception will be thrown if message keys are used which do not exist in messages file.
 * @author D049641
 *
 */
public class UITextRessourceMissingException extends AbstractDCMException {
	private static final long serialVersionUID = -569080811175934554L;

	/**
	 * Creates a new instance without root cause
	 */
	public UITextRessourceMissingException() {
	}

	/**
	 * Creates a new instance with a root cause
	 * @param cause = the root cause of this exception
	 */
	public UITextRessourceMissingException(Throwable cause) {
		super(cause);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageText()
	 */
	protected String getMessageText() {
		return "UI Text not found in Resource Bundle or bundle does not exist";
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageKey()
	 */
	protected String getMessageKey() {
		return "msg.ui.resourceMissing";
	}

}
