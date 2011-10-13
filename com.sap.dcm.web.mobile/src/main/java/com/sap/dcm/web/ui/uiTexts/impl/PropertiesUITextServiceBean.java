package com.sap.dcm.web.ui.uiTexts.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.sap.dcm.web.exception.ui.MappingFileReadException;
import com.sap.dcm.web.exception.ui.UITextIllegalRequestException;
import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.ui.impl.UITextProvider;
import com.sap.dcm.web.ui.uiTexts.UITextMappingProvider;
import com.sap.dcm.web.ui.uiTexts.UITextService;
import com.sap.dcm.web.ui.uiTexts.model.UITextServiceResponse;
import com.sap.dcm.web.ui.uiTexts.model.UiControlDef;
import com.sap.dcm.web.ui.uiTexts.model.UiControlType;
import com.sap.dcm.web.ui.uiTexts.model.UiTextKey;
import com.sap.dcm.web.ui.uiTexts.model.ViewType;

/**
 * UI Text service using property files.
 * @author D049641
 *
 */
public class PropertiesUITextServiceBean implements UITextService {

	private UITextMappingProvider textProvider;

	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.ui.UITextService#getUITexts(java.lang.String, java.lang.String)
	 */
	public List<UITextServiceResponse> getUITexts(String locale, String view)
			throws UITextRessourceMissingException,
			UITextIllegalRequestException, MappingFileReadException {
		if (view != null) {
			UITextProvider provider = new UITextProvider(locale);
			List<UITextServiceResponse> result = new ArrayList<UITextServiceResponse>();
			for (String key : getKeysOfView(view)) {
				String text = provider.getUIText(key);
				result.add(new UITextServiceResponse(key, text));
			}
			return result;
		} else {
			throw new UITextIllegalRequestException();
		}
	}

	/**
	 * Spring DI - Inject Text Mapping Provider which is used to access property file
	 * @param textProvider
	 */
	public void setTextProvider(UITextMappingProvider textProvider) {
		this.textProvider = textProvider;
	}

	/**
	 * Returns all text keys which are used by the given UI View
	 * @param view = view name
	 * @return the list of ui text keys
	 * @throws MappingFileReadException if the mapping file cannot be read
	 */
	private List<String> getKeysOfView(String view) throws MappingFileReadException{
		ViewType t = textProvider.getMappingOfView(view);
		List<String> result = new ArrayList<String>();
		if(t.getUiText() != null){
			for (UiTextKey k : t.getUiText())
				result.add(k.getTextKey());
		}
		//Handle controls assigned to the view
		if(t.getControl() != null){
			for(UiControlType c : t.getControl()){
				result.addAll(resolveControlTexts(c.getRef()));
			}
		}
		return result;
	}
	
	/**
	 * Resolves the UI texts of a control. Cycles are not allowed and will result in an exception
	 * @param control = the control which should be resolved
	 * @return the list of ui texts of the control and its sub controls
	 * @throws MappingFileReadException if the texts cannot be resolved
	 */
	private List<String> resolveControlTexts(String control) throws MappingFileReadException{
		return resolveControlTexts(control, null);
	}
	
	/**
	 * Resolves the UI texts of a control. Cycles are not allowed and will result in an exception
	 * @param control = the control which should be resolved
	 * @param progressStack = the stack of already processed controls (to avoid cycles)
	 * @return the list of ui texts of the control and its sub controls
	 * @throws MappingFileReadException if the texts cannot be resolved
	 */
	private List<String> resolveControlTexts(String control, Stack<String> progressStack) throws MappingFileReadException{
		//get the control object
		if(progressStack == null)
			progressStack = new Stack<String>();
		if(progressStack.contains(control))
			throw new MappingFileReadException(MappingFileReadException.CYCLES_IN_UI_CONTROL_RESOLUTION);
		progressStack.push(control);
		
		UiControlDef c = textProvider.getMappingOfControl(control);
		List<String> result = new ArrayList<String>();
		
		//add ui texts of control
		if(c.getUiText() != null){
			for(UiTextKey k : c.getUiText())
				result.add(k.getTextKey());
		}
		
		//handle sub controls
		if(c.getControl() != null){
			for(UiControlType sub : c.getControl()){
				result.addAll(resolveControlTexts(sub.getRef(), progressStack));
			}
		}
					
		progressStack.pop();
		return result;
	}
}
