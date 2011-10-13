package com.sap.dcm.web.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper Class for Date Handling
 * @author D049641
 *
 */
public class DateUtils {
	/**
	 * Converts a ISO Date String (yyyy-MM-dd) into a date object.
	 * @param date = the date string
	 * @return the parsed date object or null if the string is not valid
	 */
	public static Date convertISODateString(String date) {
		if (date != null) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return df.parse(date);
			} catch (ParseException e) {
			}
		}
		return null;
	}
}
