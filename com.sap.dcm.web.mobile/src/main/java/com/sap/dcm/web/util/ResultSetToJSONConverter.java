package com.sap.dcm.web.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.exception.util.ResultSetJSONConvertException;
import com.sap.dcm.web.ui.impl.UITextProvider;

/**
 * Utility class to convert a result set into a flat JSON representation.
 * 
 * @author D049641
 * 
 */
public class ResultSetToJSONConverter {
	private static Logger log = Logger
			.getLogger(ResultSetToJSONConverter.class);

	public static String REPRESENTATION_TYPE_ROW_OBJ = "ROW_OBJ";
	public static String REPRESENTATION_TYPE_ROW_ARRAY = "ROW_ARRAY";
	public static String REPRESENTATION_TYPE_COLUMN = "COLUMN";

	/**
	 * Converts a Result set into JSON representation
	 * 
	 * @param data
	 *            the result set wich should be converted
	 * @param representation
	 *            the requested representation type
	 * @return the json object with result count, result representation type,
	 *         header array, data as requested.
	 * @throws ResultSetToJSONConverter
	 *             if JSON transformation failed.
	 */
	public static JSONObject convert(ResultSet data, String representation,
			Locale locale) throws ResultSetJSONConvertException {
		ResultSetToJSONConverter inst = new ResultSetToJSONConverter(data,
				locale);
		try {
			if (representation.equalsIgnoreCase(REPRESENTATION_TYPE_COLUMN)) {
				return inst.convertColumnRepresentation();
			} else if (representation
					.equalsIgnoreCase(REPRESENTATION_TYPE_ROW_ARRAY)) {
				return inst.convertRowArrayRepresentation();
			} else {
				// default is row object representation
				return inst.convertRowObjectRepresentation();
			}
		} catch (JSONException e) {
			throw new ResultSetJSONConvertException(e);
		}
	}

	private final ResultSet data;
	private UITextProvider provider;

	/**
	 * Creates a new instance for the given data and locale;
	 * 
	 * @param data
	 *            the result set which should be converted
	 * @param locale
	 *            the locale for translation of column headers
	 */
	private ResultSetToJSONConverter(final ResultSet data, final Locale locale) throws ResultSetJSONConvertException {
		super();
		this.data = data;
		try {
			provider = new UITextProvider(locale);
		} catch (UITextRessourceMissingException e) {
			throw new ResultSetJSONConvertException(e);
		}
	}

	/**
	 * Converts a Result set into JSON representation by converting the columns
	 * into JSON arrays.
	 * 
	 * @return the JSON OBject (object with count, type, header array and data
	 *         as json object column representation)
	 * @throws JSONException
	 *             if JSON transformation failed.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONObject convertColumnRepresentation() throws JSONException,
			ResultSetJSONConvertException {
		if (data != null) {
			try {
				int columnCount = data.getMetaData().getColumnCount();
				int rowCount = 0;

				List[] columns = new List[columnCount];
				// Initialize result map
				for (int i = 0; i < columnCount; i++) {
					columns[i] = new ArrayList();
				}

				// convert result set data (rows) into column representation
				JSONArray headers = new JSONArray();
				boolean firstRun = true;
				while (data.next()) {
					for (int i = 0; i < columnCount; i++) {
						Object value = data.getObject(i + 1);
						columns[i].add(value);
						if (firstRun) {
							// Build header array during build of first row
							headers.put(data.getMetaData().getColumnLabel(i));
						}

					}
					firstRun = false;
				}

				// create JSON objects from column representation
				JSONObject dataObject = new JSONObject();
				for (int i = 0; i < columnCount; i++) {
					dataObject.put(data.getMetaData().getColumnLabel(i + 1),
							columns[i]);
				}

				// create the result.
				JSONObject result = new JSONObject();
				result.put("count", rowCount);
				result.put("type", REPRESENTATION_TYPE_COLUMN);
				result.put("headers", headers);
				result.put("data", dataObject);

				return result;
			} catch (SQLException e) {
				log.debug(e);
				throw new ResultSetJSONConvertException(e);
			}
		}
		return null;
	}

	/**
	 * Converts a result set into a JSON object string.
	 * 
	 * @return the JSON representation (object with count, type, header array
	 *         and data json object)
	 * @throws JSONException
	 *             if conversion failed.
	 */
	private JSONObject convertRowObjectRepresentation() throws JSONException,
			ResultSetJSONConvertException {
		if (data != null) {
			try {
				int columnCount = data.getMetaData().getColumnCount();
				int rowCount = 0;

				JSONArray headers = new JSONArray();
				JSONArray array = new JSONArray();
				while (data.next()) {
					JSONObject tmp = new JSONObject();
					for (int i = 1; i <= columnCount; i++) {
						Object value = data.getObject(i);
						tmp.put(data.getMetaData().getColumnLabel(i), value);
						if (array.length() == 0) {
							// Build header array during build of first row
							headers.put(data.getMetaData().getColumnLabel(i));
						}
					}
					array.put(tmp);
					rowCount++;
				}

				JSONObject result = new JSONObject();
				result.put("count", rowCount);
				result.put("type", REPRESENTATION_TYPE_ROW_OBJ);
				result.put("headers", headers);
				result.put("data", array);

				return result;
			} catch (SQLException e) {
				log.debug(e);
				throw new ResultSetJSONConvertException(e);
			}
		}
		return null;
	}

	/**
	 * Converts a result set into a JSON array.
	 * 
	 * @param data
	 *            the result set which should be converted
	 * @param locale
	 *            the locale for translation of column headers
	 * @return the JSON representation (object with count, type, header array
	 *         and data array)
	 * @throws JSONException
	 *             if conversion failed.
	 */
	private JSONObject convertRowArrayRepresentation() throws JSONException,
			ResultSetJSONConvertException {
		if (data != null) {
			try {
				int columnCount = data.getMetaData().getColumnCount();
				int rowCount = 0;

				JSONArray array = new JSONArray();
				JSONArray headers = new JSONArray();
				while (data.next()) {
					JSONArray tmp = new JSONArray();
					for (int i = 1; i <= columnCount; i++) {
						tmp.put(data.getObject(i));
						if (array.length() == 0) {
							// Build header array during build of first row
							headers.put(getColumnHeader(data.getMetaData()
									.getColumnLabel(i), data.getMetaData()
									.getTableName(i)));
						}
					}
					array.put(tmp);
					rowCount++;
				}

				JSONObject result = new JSONObject();
				result.put("count", rowCount);
				result.put("type", REPRESENTATION_TYPE_ROW_ARRAY);
				result.put("headers", headers);
				result.put("data", array);

				return result;
			} catch (SQLException e) {
				log.debug(e);
				throw new ResultSetJSONConvertException(e);
			}
		}
		return null;
	}

	private JSONObject getColumnHeader(String columnName, String tableName)
			throws JSONException {
		JSONObject result = new JSONObject();
		result.put("name", columnName);
		String localizedName = "";
		if (provider != null) {
			String textKey = "tbl." + tableName.toLowerCase() + "."
					+ columnName.toLowerCase();
			try {
				localizedName = provider.getUIText(textKey);

			} catch (UITextRessourceMissingException e) {
				log.debug("No text found for key " + textKey, e);
			}
		}
		if (localizedName.length() == 0)
			localizedName = columnName;
		result.put("localizedName", localizedName);
		return result;
	}

}
