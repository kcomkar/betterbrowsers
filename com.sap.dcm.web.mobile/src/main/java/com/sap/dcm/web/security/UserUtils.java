package com.sap.dcm.web.security;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dna.spring.security.hana.HanaUserDetails;

/**
 * Utility Class for handling of User/Principal
 * @author D049641
 *
 */
public class UserUtils {
	private static Logger log = Logger.getLogger(UserUtils.class);

	/**
	 * Returns a data base connection by using the data source which is attached to the Hana Login User
	 * @return the database connection
	 * @throws AuthenticationException if authentication is not valid
	 * @throws HDBWrappedException if connection cannot be established
	 */
	public static Connection getDBConnection() throws AuthenticationException,
			HDBWrappedException {
		try {
			HanaUserDetails principal = getPrincipal();
			if (principal.getDataSource() != null) {
				return principal.getDataSource().getConnection();
			} else {
				throw new AuthenticationException(
						AuthenticationException.TYPE.NO_DATA_SOURCE);
			}
		} catch (SQLException e) {
			throw new HDBWrappedException(e);
		}
	}

	/**
	 * Returns the current user / principal
	 * @return the user as HanaUserDetails object
	 * @throws AuthenticationException if the authentication is not valid
	 */ 
	public static HanaUserDetails getPrincipal() throws AuthenticationException {
		
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		// musst be called in a protected url
		//other wise, you can not get logged in authroization
		System.out.println("SecurityContextHolder.getContext().getAuthentication()" + a);
		
		if(a != null){
			Object o = a.getPrincipal();
			System.out.println(o.getClass());
			System.out.println(o);
		if (o instanceof HanaUserDetails) {
			return ((HanaUserDetails) o);
		} else {
			throw new AuthenticationException(
					AuthenticationException.TYPE.NO_VALID_PRINCIPAL);
		}
		}else{
			throw new AuthenticationException(
					AuthenticationException.TYPE.NOT_LOGGED_IN);
		}
	}

	/**
	 * checks if the given role is assigned to the current user
	 * @param role = the role which should be checked
	 * @return true if user has the role assigned or false if not
	 * @throws AuthenticationException if the authentication of the user is not valid
	 */
	public static boolean hasRole(String role) throws AuthenticationException {
		String r = role;
		if(!r.startsWith("ROLE_")){
			r = "ROLE_"+r;
		}
		
		HanaUserDetails p = getPrincipal();
		for (GrantedAuthority g : p.getAuthorities())
			if (g.getAuthority().equals(r))
				return true;
		return false;
	}

	/**
	 * Returns the user language.
	 * @return the locale of the user language
	 */
	public static Locale getUserLanguage() {
		try {
			HanaUserDetails p = getPrincipal();
			if (p.getUserLanguage() != null
					&& p.getUserLanguage().length() == 2)
				return new Locale(p.getUserLanguage().toLowerCase());
		} catch (AuthenticationException e) {
			log.debug(e);
		}
		return Locale.getDefault();
	}
	
	/**
	 * Checks if a user is a valid application user or not
	 * @param username the username
	 * @return true if the user is valid or false if not
	 */
	public static boolean isRelevantUser(String username){
		for(String filter : getUserFilters()){
			if(filter.endsWith("*")){
				String f = filter.substring(0, filter.length()-1);
				if(username.startsWith(f))
					return false;
			}else{
				if(username.equalsIgnoreCase(filter))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the list of User filtes.
	 * @return th list of user filers
	 */
	public static String[] getUserFilters(){
		return new String[] { "SYSTEM", "SYS", "_SYS*"};
	}
}
