package com.sap.dcm.web.user.mgmt;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dcm.web.user.mgmt.model.HanaRole;
import com.sap.dcm.web.user.mgmt.model.HanaUser;
import com.sap.dcm.web.user.mgmt.model.RoleAssignment;
import com.sap.dcm.web.user.mgmt.model.UserRoleAssignment;

/**
 * REST service to create new database users and to assign roles
 * 
 * @author D055272
 * 
 */
@Path("/appMgmt")
@Produces({ MediaType.APPLICATION_JSON })
public interface UserMgmtService {

	/**
	 * Method to reset the password of a user in the database
	 * 
	 * @param username
	 *            the user name
	 * @param password
	 *            the password which should be set for the user
	 * @param passwordRepeat
	 *            the password repeated for validation
	 * @param locale
	 *            locale for message handling
	 * @return HTTP response
	 */
	@POST
	@Path("/restPassword")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public Response resetUserPassword(@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("passwordRepeat") String passwordRepeat);

	/**
	 * Method to create a new user in the database
	 * 
	 * @param username
	 *            the user name
	 * @param password
	 *            the password which should be set for the user
	 * @param passwordRepeat
	 *            the password repeated for validation
	 * @param locale
	 *            locale for message handling
	 * @return HTTP response
	 */
	@POST
	@Path("/createUser")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public Response createUser(@FormParam("username") String username,
			@FormParam("password") String password,
			@FormParam("passwordRepeat") String passwordRepeat);

	/**
	 * Method to create a new user in the database
	 * 
	 * @param username
	 *            the name of user which should be deleted
	 * @return HTTP response
	 * @throws AuthenticationException
	 *             if user authentication fails
	 */
	@DELETE
	@Path("/user")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public Response deleteUser(@FormParam("username") String username);

	/**
	 * Fetches all available roles from the database which could be assigned to
	 * a specific users.
	 * 
	 * @return list of roles
	 */
	@GET
	@Path("/roles")
	public List<HanaRole> getRoles() throws HDBWrappedException,
			AuthenticationException;

	/**
	 * Fetches all available users from the database.
	 * 
	 * @return list of users
	 */
	@GET
	@Path("/users")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public List<HanaUser> getUsers() throws HDBWrappedException,
			AuthenticationException;

	/**
	 * Fetches all available users from the database.
	 * 
	 * @return list of users
	 */
	@GET
	@Path("/assignedRoles")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public Response getAssignedRoles(
			@FormParam("username") String username);
	
	/**
	 * Fetches all available roles of a certain user and all available roles from the database.
	 * 
	 * @return list of users
	 */
	@GET
	@Path("/roleAssignments")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public List<RoleAssignment> getRoleAssigments(
			@FormParam("username") String username) throws HDBWrappedException,
			AuthenticationException;

	/**
	 * Assigns the passed roles to the user account.
	 * 
	 * Passing params in xml form: <roleassignment> <user> <name>D055272</name>
	 * </user> <roles> <role> <name>SAP_DCM_CASH_MANAGER</name> </role> <role>
	 * <name>SAP_DCM_ILM_READ</name> </role> </roles> </roleassignment>
	 * 
	 * ... or in JSON form: {"roleassignment": {"user": {"name": "D055272"},
	 * "roles": [{"name": "SAP_DCM_CASH_MANAGER"}, {"name":
	 * "SAP_DCM_ILM_READ"}]}}
	 * 
	 * @return HTTP response
	 */
	@POST
	@Path("/assignRoles")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response assignRoles(UserRoleAssignment userRoleAssignment);
	
	/**
	 * Queries a certain user from the database and returns its object representation
	 * @param username the user name
	 * @return the user object
	 */
	public HanaUser getUser(String username) throws HDBWrappedException, AuthenticationException;
	
	/**
	 * Queries the current user from the database and returns its object representation
	 * @return the user object
	 */
	public HanaUser getCurrentUser() throws HDBWrappedException, AuthenticationException;
}
