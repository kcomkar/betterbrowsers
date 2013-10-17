package com.sap.dcm.web.util;

import java.util.Date;

public class DataInputValidation {
	private final static String NAME_REGEX = "[a-zA-Z0-9-_]+";
	private final static String PASSWORD_REGEX = "[a-zA-Z0-9-_&?=!$�%/+*#\\.:,;]+";
	
	/**
	 * Check if the name is valid (alphanumeric characters only)
	 * 
	 * @param name
	 *            = the user name
	 * @return true if the user name is valid or false if not
	 */
	public static boolean isValidName(String name) {
		if(name == null || name.trim().length() == 0)
			return false;
		return name.matches(NAME_REGEX);
	}

	/**
	 * Checks if the password string is valid (alphanumeric characters plus some
	 * special characters).
	 * 
	 * @param password
	 *            the password string
	 * @return true if the password is valid or false if not
	 */
	public static boolean isValidPassword(String password) {
		return password.matches(PASSWORD_REGEX);
	}
	
	/**
	 * Checks if the date is valid - not null and within the given limits
	 * @param date the date which should be checked
	 * @param lowerLimit the lower limit (min date)
	 * @param upperLimit the upper limit (max date)
	 * @return
	 */
	public static boolean isValidDate(Date date, Date lowerLimit, Date upperLimit){
		if(date == null)
			return false;
		if(date.before(lowerLimit))
			return false;
		if(date.after(upperLimit))
			return false;
		return true;
	}
}
