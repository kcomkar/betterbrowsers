package com.sap.dcm.mobile.service.preferences;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces({ MediaType.APPLICATION_JSON })
@Path("mobile/preferences")
public interface IPreferencesService {

	@GET
	public Response getPreferences();
}
