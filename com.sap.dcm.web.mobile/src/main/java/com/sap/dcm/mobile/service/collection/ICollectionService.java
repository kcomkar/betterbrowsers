package com.sap.dcm.mobile.service.collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces({ MediaType.APPLICATION_JSON })
@Path("mobile/collectionOverview")
public interface ICollectionService {

	@Path("getOverview")
	@GET
	public Response getOverview();
	
	@Path("getCustomer/{companyCode}/{customerId}")
	@GET
	public Response getCustomerKPI(@PathParam("companyCode") String companyCode, @PathParam("customerId") String customerId);
	
	@Path("getCustomer/{companyCode}/{customerId}/notes")
	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response createNote(@PathParam("companyCode") String companyCode,@PathParam("customerId") String customerId, @FormParam("contact") String contact, @FormParam("text") String text);
	
	@Path("getCustomer/{companyCode}/{customerId}/notes")
	@GET
	public Response getNotes(@PathParam("companyCode") String companyCode,@PathParam("customerId") String customerId);
	
	@Path("getCustomer/{companyCode}/{customerId}/invoices/{invoiceId}")
	@GET
	public Response getInvoice(@PathParam("companyCode") String companyCode,@PathParam("customerId") String customerId,@PathParam("invoiceId") String invoiceId);
	
	
	
}
