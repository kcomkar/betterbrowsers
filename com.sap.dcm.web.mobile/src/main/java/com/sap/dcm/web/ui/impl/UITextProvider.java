package com.sap.dcm.web.ui.impl;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;

/**
 * This class is responsible for reading message texts from the uiTexts property files.
 * @author D049641
 *
 */
public class UITextProvider {
	private final static String baseName = "ui/messages";
	private ResourceBundle bundle;

	/**
	 * Creates a new default instance with the systems default locale.
	 * @throws UITextRessourceMissingException if the resource bundle cannot be initialized
	 */
	public UITextProvider() throws UITextRessourceMissingException {
		this(Locale.getDefault());
	}

	/**
	 * Creates a new instance for the given locale. 
	 * @param locale = the locale as String representation. If the locale is not valid or does not exists the system default locale will be used.
	 * @throws UITextRessourceMissingException if the resource bundle cannot be initialized 
	 */
	public UITextProvider(String locale) throws UITextRessourceMissingException {
		this(getLocale(locale));
	}

	/**
	 * Creates a new instance for the given locale.
	 * @param locale = the locale which should be used for the message access.
	 * @throws UITextRessourceMissingException if the resource bundle cannot be initialized
	 */
	public UITextProvider(Locale locale) throws UITextRessourceMissingException {
		try {
			if(locale != null){
				bundle = ResourceBundle.getBundle(baseName, locale);
			}else{
				bundle = ResourceBundle.getBundle(baseName);
			}
		} catch (MissingResourceException mre) {
			throw new UITextRessourceMissingException(mre);
		}
	}
	
	/**
	 * Returns the requested message texts and maps the parameters if provided
	 * @param key = the key of the requested message
	 * @param params = the message parameters
	 * @return the localized message
	 * @throws UITextRessourceMissingException if the message does not exist.
	 */
	public String getUIText(String key, String... params) throws UITextRessourceMissingException{
		try {
			String text = bundle.getString(key);
			if(params != null){
				for(int i = 0; i < params.length; i++)
					text.replace("{"+i+"}", params[i]);
			}			
			return text;
		} catch (MissingResourceException mre) {
			throw new UITextRessourceMissingException(mre);
		}
	}

	/**
	 * Derives the locale object from the provided locale String. If the locale is not valid the system locale will be returned
	 * @param locale = the locale as string
	 * @return the object representation of the locale.
	 */
	public static Locale getLocale(String locale) {
		if (locale != null) {
			String[] localeParts = locale.split("_");
			if (localeParts.length == 3 && localeParts[0].length() == 2 && localeParts[0].length() == 2)
				return new Locale(localeParts[0].toLowerCase(),
						localeParts[1].toUpperCase(), localeParts[2]);
			else if (localeParts.length == 2 && localeParts[0].length() == 2 && localeParts[0].length() == 2)
				return new Locale(localeParts[0].toLowerCase(),
						localeParts[1].toUpperCase());
			else if (localeParts.length == 1 && localeParts[0].length() == 2 )
				return new Locale(localeParts[0]);
		}
		return Locale.getDefault();
	}
}
