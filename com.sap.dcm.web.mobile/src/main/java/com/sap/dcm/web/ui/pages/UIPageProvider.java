package com.sap.dcm.web.ui.pages;

import java.util.List;

import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dcm.web.exception.ui.UIWorkcenterDefinitionException;
import com.sap.dcm.web.ui.pages.model.AssignedPage;

/**
 * This bean is responsible to the read UI workcenter definition file and to
 * return all workcenters of a certain user.
 * 
 * @author D049641
 * 
 */
public interface UIPageProvider {
	/**
	 * Determines all workcenters of the current logged in user
	 * @return the workcenters including the workcenter views
	 * @throws UIWorkcenterDefinitionException if the file cannot be progressed
	 * @throws AuthenticationException if the role check fails
	 */
	public abstract List<AssignedPage> getWorkcentersOfUser() throws UIWorkcenterDefinitionException, AuthenticationException;
}
