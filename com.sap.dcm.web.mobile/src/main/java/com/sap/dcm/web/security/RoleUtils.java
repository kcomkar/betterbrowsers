package com.sap.dcm.web.security;

/**
 * Utility Class for Role management.
 * 
 * @author D049641
 * 
 */
public class RoleUtils {
	/**
	 * Checks if the role is an application relevant role
	 * 
	 * @param role
	 *            = the role name
	 * @return true if the role is an application role or false if not
	 */
	public static boolean isApplicationRole(String role) {
		if (role.startsWith("SAP_DCM_") || role.startsWith("ROLE_SAP_DCM_"))
			return true;
		return false;
	}
}
