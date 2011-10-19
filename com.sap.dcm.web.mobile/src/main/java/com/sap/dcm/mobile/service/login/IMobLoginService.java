package com.sap.dcm.mobile.service.login;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ "application/x-www-form-urlencoded" })
@Path("mobile/mobLoginService")
public interface IMobLoginService {
	/**
	 * LOGIN POST service for user authentication 
	 * @param username = the username
	 * @param password = the user password
	 * @return the login status indicating if login was successful or not.
	 */
	@Path("login")
	@POST
	public MobLoginStatus login(@FormParam("username") String username, @FormParam("password") String password);
	
}
