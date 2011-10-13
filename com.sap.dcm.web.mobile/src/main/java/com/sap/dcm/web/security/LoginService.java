package com.sap.dcm.web.security;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.sap.dcm.web.security.model.LoginStatus;

/**
 * REST Service to authenticate user with username and password
 * @author D049641
 *
 */
@Produces({ "application/json" })
@Consumes({ "application/x-www-form-urlencoded" })
public interface LoginService {
	
	/**
	 * LOGIN POST service for user authentication 
	 * @param username = the username
	 * @param password = the user password
	 * @return the login status indicating if login was successful or not.
	 */
	@Path("login")
	@POST
	public LoginStatus login(@FormParam("username") String username, @FormParam("password") String password);
}
