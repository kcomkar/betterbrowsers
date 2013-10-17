package com.sap.dcm.mobile.service.preferences;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces({ MediaType.APPLICATION_JSON })
@Path("mobile/preferences")
public interface IPreferencesService {

	@GET
	public Response getPreferences();
	
	@POST
	@Consumes({ "application/x-www-form-urlencoded" })
	public Response updatePreferences( @FormParam("currency") String currency, @FormParam("companyCodes") List<String> companyCodes);
}
