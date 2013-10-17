package com.sap.dcm.web.ui.uiTexts;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;

import com.sap.dcm.web.exception.ui.MappingFileReadException;
import com.sap.dcm.web.exception.ui.UITextIllegalRequestException;
import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.ui.uiTexts.model.UITextServiceResponse;

/**
 * The Interface of the UI text Service. The UI service can be used to request localized UI texts for a certain UI view
 * @author D049641
 *
 */
public interface UITextService {

	/**
	 * Request for the UI texts of a given UI View and locale.
	 * @param locale = the locale which should be used. If no locale is provided the systems default locale will be used.
	 * @param view = the view for which the UI texts are requested
	 * @return the list of UI texts
	 * @throws UITextRessourceMissingException if a key is not defined in the message file
	 * @throws UITextIllegalRequestException if the message file does not exist
	 * @throws MappingFileReadException if the mapping file cannot be processed
	 */
	@GET
	public List<UITextServiceResponse> getUITexts(
			@QueryParam("locale") String locale, @QueryParam("view") String view)
			throws UITextRessourceMissingException,
			UITextIllegalRequestException, MappingFileReadException;
}
