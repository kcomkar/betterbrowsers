package com.sap.dcm.web.ui.pages.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dcm.web.exception.ui.UIWorkcenterDefinitionException;
import com.sap.dcm.web.security.UserUtils;
import com.sap.dcm.web.ui.pages.UIPageProvider;
import com.sap.dcm.web.ui.pages.model.AssignedPage;
import com.sap.dcm.web.ui.pages.model.Page;
import com.sap.dcm.web.ui.pages.model.Pages;
import com.sap.dcm.web.ui.pages.model.Role;
import com.sap.dcm.web.ui.pages.model.SubPage;

/**
 * Implementation of the UIPageProvider using the default uiPage.xml in %CLASSPATH%/ui
 * @author D049641
 *
 */
public class UIPageProviderBean implements UIPageProvider,
		ApplicationContextAware {
	private final static String FILE_NAME = "ui/uiPage.xml";

	private Unmarshaller unmarshaller;

	private Pages pages;

	private ApplicationContext context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.ui.workcenter.UIWorkcenterProvider#getWorkcentersOfUser()
	 */
	public List<AssignedPage> getWorkcentersOfUser()
			throws UIWorkcenterDefinitionException, AuthenticationException {
		Pages all = getPages();
		List<AssignedPage> result = new ArrayList<AssignedPage>();
		
		// filter by roles
		if (all.getPage() != null) {
			for (Page p : all.getPage()) {
				// Check access rights on workcenter level
				if (!userHasOneRole(p.getAllowedRole())) {
					continue;
				}
				
				if(p.getPage() != null && p.getPage().size() > 0){
					//Progress sub pages
					List<AssignedPage> subPages = new ArrayList<AssignedPage>();
					
					//get name of default page
					String pageDefault = "";
					if(p.getDefaultPage() != null)
						pageDefault = p.getDefaultPage();
					
					boolean defaultSet = false;
					
					//Check sub page authorization
					for(SubPage sp : p.getPage()){
						if (userHasOneRole(sp.getAllowedRole())) {	
							boolean d = (sp.getId().equals(pageDefault));
							if(d)
								defaultSet=true;
							AssignedPage subPage = new AssignedPage(sp.getId(), sp.getUiText(),d);
							subPages.add(subPage);
						}
					}
					
					//If user is allowed to use at least one view assigne page
					if(subPages.size() > 0){
						//set default page
						if(!defaultSet)
							subPages.get(0).setDefaultPage(true);		
						AssignedPage ap = new AssignedPage(p.getId(), p.getUiText(), false, subPages);
						result.add(ap);
					}
				}else{
					//No sub pages	
					AssignedPage ap = new AssignedPage(p.getId(), p.getUiText(), true);
					result.add(ap);
				}
			}
		}
		return result;
	}

	/**
	 * Checks if the user has at least one of the given roles assigned
	 * 
	 * @param roles
	 *            = list of roles
	 * @return true if user has one of the roles or false if not
	 * @throws AuthenticationException
	 */
	private boolean userHasOneRole(List<Role> roles)
			throws AuthenticationException {
		if (roles != null && roles.size() > 0) {
			for (Role role : roles) {
				if (UserUtils.hasRole(role.getId()))
					return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * Returns all available pages from the pages definition file. If
	 * the file is not read, the data will be read from the xml file
	 * 
	 * @return the pages of the application
	 * @throws UIWorkcenterDefinitionException
	 */
	private Pages getPages() throws UIWorkcenterDefinitionException {
		try {
			if (pages == null)
				loadPages();
		} catch (IOException e) {
			throw new UIWorkcenterDefinitionException(e);
		}
		return pages;
	}

	/**
	 * Initialize mappings. Read the mapping file and buffer the data in the
	 * private mapping variable.
	 * 
	 * @throws IOException
	 */
	private void loadPages() throws IOException {
		FileInputStream is = null;
		try {
			Resource r = context.getResource("classpath:" + FILE_NAME);
			pages = (Pages) this.unmarshaller
					.unmarshal(new StreamSource(r.getInputStream()));
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * Spring DI - set unmarshaller used for XML processing
	 * 
	 * @param unmarshaller
	 *            = the implementation of the xml unmarshaller.
	 */
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	/**
	 * Spring DI - set application context.
	 * 
	 * @param ctx
	 *            = the application context
	 */
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.context = ctx;
	}
}
