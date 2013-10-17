package com.sap.dcm.web.exception;

import java.util.Locale;

import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.ui.impl.UITextProvider;

/**
 * Basic abstract implementation of Exception thrown by the DCM Application.
 * Sub-classes of this class have to implement getMessageText and getMessageKey.
 * The abstract implementation will try to get the localized message from the
 * message.properties file so that the sub-classes do not have to take care for
 * internationalization.
 * 
 * @author D049641
 * 
 */
public abstract class AbstractDCMException extends Exception {
	private static final long serialVersionUID = -603745924398204412L;
	private String[] messageParameters;

	/**
	 * Default constructor
	 */
	public AbstractDCMException() {
	}

	/**
	 * Constructor containing root cause of exception
	 * @param cause = the root cause of this exception
	 */
	public AbstractDCMException(Throwable cause) {
		super(cause);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return getMessageText();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	public String getLocalizedMessage() {
		return getLocalizedMessage(Locale.getDefault());
	}

	/**
	 * Returns the localized message for the give locale. If no message for the
	 * provided locale exists the default locale will be returned or the default
	 * message text (getMessageText()) if not message is available in
	 * messages.properties.
	 * 
	 * @param locale
	 *            = the locale of the message text.
	 * @return the localized message.
	 */
	public String getLocalizedMessage(String locale) {
		try {
			UITextProvider prov = new UITextProvider(locale);
			return prov.getUIText(getMessageKey());
		} catch (UITextRessourceMissingException e) {
			return getMessageText();
		}
	}

	/**
	 * Returns the localized message for the give locale. If no message for the
	 * provided locale exists the default locale will be returned or the default
	 * message text (getMessageText()) if not message is available in
	 * messages.properties.
	 * 
	 * @param locale = the locale of the message text.
	 * @return the localized message.
	 */
	public String getLocalizedMessage(Locale locale) {
		try {
			UITextProvider prov = new UITextProvider(locale);
			String msg = prov.getUIText(getMessageKey());
			
			//Check if there are message parameters and replace them
			String[] params = getMessageParameters();
			if(params != null && params.length > 0){
				for(int i = 0;  i < params.length; i++)
					msg = msg.replace("{"+i+"}", params[i]);
			}
			
			return msg;
		} catch (UITextRessourceMissingException e) {
			return getMessageText();
		}
	}

	/**
	 * Returns a hard-coded message text which will be used in case of a missing/undefined message text.
	 * @return the default message text.
	 */
	protected abstract String getMessageText();

	/**
	 * Returns the message key of this exception. The message key has to be equal to the key in messages.properties.
	 * @return = the message key.
	 */
	protected abstract String getMessageKey();

	/**
	 * Returns the array of message parameters
	 * @return the message parameters
	 */
	public String[] getMessageParameters() {
		return messageParameters;
	}

	/**
	 * Sets the array of message parameters
	 * @param messageParameters
	 */
	public void setMessageParameters(String[] messageParameters) {
		this.messageParameters = messageParameters;
	}
}
