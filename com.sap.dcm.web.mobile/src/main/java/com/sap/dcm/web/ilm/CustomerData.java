package com.sap.dcm.web.ilm;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Rest Service to get and delete personal data of a customer.
 * @author D049641
 *
 */
@Path("/ilm/customerData")
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/x-www-form-urlencoded" })
public interface CustomerData {
	/**
	 * Retrieves the customer data from the HANA system and returns result as a json string 
	 * @param customerID = the customer id for which the personal data should be shown
	 * @param companyId = the company of the customer data which should be deleted
	 * @param representation = the result set representation type 
 	 * @return the json object
	 */
	@GET
	public Response getConstomerData(
			@QueryParam("customerId") String customerId,
			@QueryParam("companyId") String companyId,
			@QueryParam("representation") @DefaultValue("ROW_OBJ") String representation);
	
	/**
	 * Deletes all dunning invoices of a certain customer and company up to and including the given dunning date.
	 * @param customerId = the customer id for which the date should be deleted
	 * @param companyId = the company of the customer data which should be deleted
	 * @param lastDunningDate = the dunning date for deletion. all dunnings up and including this date will be deleted
	 * @return
	 */
	@DELETE
	@Path("/dunnings")
	@PreAuthorize("hasRole('ROLE_SAP_DCM_ILM_DELETE')")
	public Response deleteDunningInvoices( @FormParam("customerId") String customerId,
			@FormParam("companyId") String companyId, @FormParam("lastDunningDate") String
			lastDunningDate);
}
