package com.sap.dcm.web.user.pwd;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST service to change the user password
 * @author D049641
 *
 */
@Path("/user/changePwd")
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/x-www-form-urlencoded" })
public interface PasswordService {
	/**
	 * POST request to change the password of the user
	 * @param user = the user name
	 * @param oldPasssword = the current password of the user
	 * @param newPassword = the new password of the user
	 * @param newPasswordRepeat = the new password repeated for validation
	 * @param locale = locale for message handling
	 * @return HTTP response
	 */
	@POST
	public Response changePassword(@FormParam("username") String user,
			@FormParam("oldPassword") String oldPasssword,
			@FormParam("newPassword") String newPassword,
			@FormParam("newPasswordRepeat") String newPasswordRepeat,
			@FormParam("locale") String locale);
}
