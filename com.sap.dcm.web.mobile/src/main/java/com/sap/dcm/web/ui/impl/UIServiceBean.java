package com.sap.dcm.web.ui.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.dcm.web.exception.AbstractDCMException;
import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dcm.web.ui.UIService;
import com.sap.dcm.web.ui.model.UIInitializationResponse;
import com.sap.dcm.web.ui.pages.UIPageProvider;
import com.sap.dcm.web.ui.pages.model.AssignedPage;
import com.sap.dcm.web.user.mgmt.UserMgmtService;
import com.sap.dcm.web.user.mgmt.model.HanaUser;

/**
 * Default implementation of the UIServiceBean
 * @author D049641
 *
 */
public class UIServiceBean implements UIService {
	private static Logger log = Logger.getLogger(UIServiceBean.class);
	private UIPageProvider pageProvider;
	private UserMgmtService userService;

	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.ui.UIService#getAssignedPages()
	 */
	public List<AssignedPage> getAssignedPages() {
		try {
			return pageProvider.getWorkcentersOfUser();
		} catch (AbstractDCMException e) {
			log.debug(e);
			return new ArrayList<AssignedPage>();
		} 
	}
	
	/**
	 * Spring DI inject page provider
	 * @param pageProvider
	 */
	public void setPageProvider(UIPageProvider pageProvider) {
		this.pageProvider = pageProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.ui.UIService#initialize()
	 */
	public UIInitializationResponse initialize() throws AuthenticationException, HDBWrappedException {
		HanaUser user = userService.getCurrentUser();
		List<AssignedPage> pages = getAssignedPages();
		return new UIInitializationResponse(pages.toArray(new AssignedPage[pages.size()]), user);
	}

	/**
	 * Spring DI - inject user service
	 * @param userService
	 */
	public void setUserService(UserMgmtService userService){
		this.userService = userService;
	}
}
