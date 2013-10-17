package com.sap.dcm.web.exception.ui;

import com.sap.dcm.web.exception.AbstractDCMException;

/**
 * This exception will be thrown if the mapping file of UI View and UI Texts cannot be parsed.
 * @author D049641
 *
 */
public class MappingFileReadException extends AbstractDCMException {
	private static final long serialVersionUID = -2943932152889957751L;
	
	private final static String genericMessageText = "Error during processing of UI Text mapping file. Please repot a bug.";
	private final static String genericMessageKey = "msg.ui.errorMappingFile";
	
	public final static String CYCLES_IN_UI_CONTROL_RESOLUTION = "msg.ui.cyclesInControlerDef";
	public final static String VIEW_NOT_FOUND = "msg.ui.viewNotFound";
	public final static String CONTROL_NOT_FOUND = "msg.ui.controlNotFound";
	
	private String messageKey;

	/**
	 * Creates an instance without root cause
	 */
	public MappingFileReadException() {
		super();
	}

	/**
	 * Creates an instance with a root cause
	 * @param cause = the root cause of this exception.
	 */
	public MappingFileReadException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Creates a new instance by providing a specific error key
	 * @param key the error key
	 */
	public MappingFileReadException(String key){
		super();
		this.messageKey = key;
	}
	
	/**
	 * Creates a new instance by providing a specific error key and a root cause
	 * @param key the error key
	 * @param cause the root cause
	 */
	public MappingFileReadException(String key, Throwable cause){
		super(cause);
		this.messageKey = key;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageText()
	 */
	protected String getMessageText() {
		return genericMessageText;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageKey()
	 */
	protected String getMessageKey() {
		if (messageKey != null)
			return messageKey;
		return genericMessageKey;
	}


	
}
