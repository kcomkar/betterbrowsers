package com.sap.dcm.web.exception.db;

import java.sql.SQLException;

import com.sap.dcm.web.exception.AbstractDCMException;

/**
 * Wrapper class for SQL Exceptions thrown by HANA DB.
 * @author D049641
 *
 */
public class HDBWrappedException extends AbstractDCMException {
	private static final long serialVersionUID = 1930618643894483718L;
	private String key;
	private String message;
	
	/**
	 * Initializes a new HDBWrapperException from the given SQLException
	 * @param e = the source exception
	 */
	public HDBWrappedException(SQLException e){
		key = "msg.hdb."+e.getErrorCode();
		if(e.getErrorCode() == 413){
			//Parse the number of old passwords which cannot be reused.
			int start = e.getMessage().lastIndexOf("[") +1;
			int end = e.getMessage().lastIndexOf("]");
			if(start != -1 && end != -1){
				String value = e.getMessage().substring(start,end);
				super.setMessageParameters(new String[]{value});
			}
		}
		message = e.getLocalizedMessage();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageText()
	 */
	protected String getMessageText() {
		return message;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.AbstractDCMException#getMessageKey()
	 */
	protected String getMessageKey() {
		return key;
	}
}
