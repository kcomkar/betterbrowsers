package com.sap.dcm.web.ui;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dcm.web.ui.model.UIInitializationResponse;
import com.sap.dcm.web.ui.pages.model.AssignedPage;

/**
 * The Interface of the UI Service. The UI service can be used 
 * - to request localized UI texts for a certain UI view
 * - to get the allowed pages of the logged in user
 * @author D049641
 *
 */
@Path("/ui")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public interface UIService {
	/**
	 * Determines all pages which are assigened to a certain user
	 * @return the list of assigned Pages
	 */
	@GET
	@Path("/pages")
	public List<AssignedPage> getAssignedPages();
	
	/**
	 * Determines all pages which are assigened to a certain user
	 * @return the list of assigned Pages
	 * @throws AuthenticationException if user is not authorized
	 * @throws HDBWrappedException if data cannot be retrieved from database
	 */
	@GET
	@Path("/init")
	public UIInitializationResponse initialize() throws AuthenticationException, HDBWrappedException ;
}
