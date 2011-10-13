package com.sap.dcm.web.ui.uiTexts;

import com.sap.dcm.web.exception.ui.MappingFileReadException;
import com.sap.dcm.web.ui.uiTexts.model.UiControlDef;
import com.sap.dcm.web.ui.uiTexts.model.ViewMapping;
import com.sap.dcm.web.ui.uiTexts.model.ViewType;

/**
 * Bean definition of UI Text mapping providers. UI Text Mapping Providers are
 * responsible to derive the UI Text keys which are used by a certain UI view.
 * 
 * @author D049641
 * 
 */
public interface UITextMappingProvider {
	/**
	 * Returns the list of UI View mappings. The view mappings define mapping between UI view and text keys.
	 * @return the list of ui view mappings.
	 * @throws MappingFileReadException if the mapping file cannot be read
	 */
	public ViewMapping getMappings() throws MappingFileReadException;

	/**
	 * Returns the UI texts keys for a given view
	 * @param view = the name of the UI view
	 * @return the view type containing all text keys
	 * @throws MappingFileReadException if the mapping file cannot be read or the view does not exist.
	 */
	public ViewType getMappingOfView(String view)
			throws MappingFileReadException;
	
	/**
	 * Returns the UI texts keys for the given control
	 * @param control = the name of the control
	 * @return the control def containing all text keys
	 * @throws MappingFileReadException if the mapping file cannot be read or the control does not exist.
	 */
	public UiControlDef getMappingOfControl(String control)
			throws MappingFileReadException;
}
