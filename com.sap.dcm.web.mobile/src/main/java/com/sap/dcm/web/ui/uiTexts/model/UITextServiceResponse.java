package com.sap.dcm.web.ui.uiTexts.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the Response of the UI text request. 
 * @author D049641
 *
 */
@XmlRootElement(name = "uiText")
public class UITextServiceResponse {
	private String key;
	private String value;

	/**
	 * Default constructor required by JAXB
	 */
	public UITextServiceResponse() {
		super();
	}

	/**
	 * Creates a new instance for the given key and value
	 * @param key = the UI text key
	 * @param value = the UI text value
	 */
	public UITextServiceResponse(String key, String value) {
		this();
		this.key = key;
		this.value = value;
	}

	/**
	 * Returns the UI text key 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the UI text key
	 * @param key = the key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Returns the value of the UI text
	 * @return the ui text
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the UI text
	 * @param value = the ui text
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
