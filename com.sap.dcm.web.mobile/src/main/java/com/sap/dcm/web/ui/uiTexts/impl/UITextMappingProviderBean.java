package com.sap.dcm.web.ui.uiTexts.impl;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import com.sap.dcm.web.exception.ui.MappingFileReadException;
import com.sap.dcm.web.ui.uiTexts.UITextMappingProvider;
import com.sap.dcm.web.ui.uiTexts.model.UiControlDef;
import com.sap.dcm.web.ui.uiTexts.model.ViewMapping;
import com.sap.dcm.web.ui.uiTexts.model.ViewType;

/**
 * Implementation of the UITextMappingProvider. The mapping definition have to
 * be stored in viewTextKeyMapping.xml in the ui folder within the classpath
 * root folder.
 * 
 * @author D049641
 * 
 */
public class UITextMappingProviderBean implements UITextMappingProvider,
		ApplicationContextAware {
	private final static String FILE_NAME = "ui/uiTextKeyMapping.xml";

	private Unmarshaller unmarshaller;

	private ViewMapping mappings;

	private ApplicationContext context;

	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.ui.mapping.UITextMappingProvider#getMappings()
	 */
	public ViewMapping getMappings() throws MappingFileReadException {
		if (this.mappings == null) {
			try {
				loadMappings();
			} catch (IOException e) {
				throw new MappingFileReadException(e);
			}
		}
		return mappings;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.ui.mapping.UITextMappingProvider#getMappingOfView(java.lang.String)
	 */
	public ViewType getMappingOfView(String view)
			throws MappingFileReadException {
		ViewMapping mappings = getMappings();
		if (mappings != null && mappings.getView() != null) {
			for (ViewType t : mappings.getView()) {
				if (t.getId().equals(view))
					return t;
			}
		}
		throw new MappingFileReadException(MappingFileReadException.VIEW_NOT_FOUND);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.ui.mapping.UITextMappingProvider#getMappingOfControl(java.lang.String)
	 */
	public UiControlDef getMappingOfControl(String control) throws MappingFileReadException{
		ViewMapping mappings = getMappings();
		if(mappings != null && mappings.getControl() != null){
			for ( UiControlDef c : mappings.getControl())
				if(c.getId().equals(control))
					return c;
		}
		throw new MappingFileReadException(MappingFileReadException.CONTROL_NOT_FOUND);
	}

	/**
	 * Initialize mappings. Read the mapping file and buffer the data in the private mapping variable.
	 * @throws IOException
	 */
	private void loadMappings() throws IOException {
		FileInputStream is = null;
		try {
			Resource r = context.getResource("classpath:" + FILE_NAME);
			mappings = (ViewMapping) this.unmarshaller
					.unmarshal(new StreamSource(r.getInputStream()));
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * Spring DI - set unmarshaller used for XML processing
	 * @param unmarshaller = the implementation of the xml unmarshaller.
	 */
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	/**
	 * Spring DI - set application context.
	 * @param ctx = the application context
	 */
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.context = ctx;
	}
}
