package com.sap.dcm.web.security.impl;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.security.LoginService;
import com.sap.dcm.web.security.model.LoginStatus;
import com.sap.dcm.web.ui.impl.UITextProvider;
import com.sap.dna.spring.security.hana.HanaUserDetails;

/**
 * Impelementation of the login service
 * 
 * @author D049641
 * 
 */
public class LoginServiceImpl implements LoginService {
	private AuthenticationProvider provider;
	
	@Context
	private ServletContext ctx;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.service.LoginService#login(java.lang.String,
	 * java.lang.String)
	 */
	public LoginStatus login(String username, String password) {
		try {
			UITextProvider p = new UITextProvider(); // TODO localization
			try {
				Authentication authentication = provider
						.authenticate(new UsernamePasswordAuthenticationToken(
								username, password,
								new ArrayList<GrantedAuthority>()));
				if (authentication != null && authentication.isAuthenticated()) {
					// TODO check if user has one of the roles. This should
					// be more generic
					if (hasRoles(authentication)) {
						SecurityContextHolder.getContext().setAuthentication(
								authentication);
						return new LoginStatus(
								authentication.isAuthenticated(), username,
								"login successful", getRedirectUrl(),false);
					} else {
						authentication.setAuthenticated(false);
						return new LoginStatus(false, username,
								p.getUIText("msg.logon.roleMis"), "",false);
					}
				} else {
					return new LoginStatus(false, username,	p.getUIText("msg.logon.credentialsNotValid"), "",false);
				}
			} catch (AuthenticationException ex) {
				if (ex.getCause() instanceof SQLException) {
					// TODO localization
					HDBWrappedException hdbEx = new HDBWrappedException(
							(SQLException) ex.getCause());
					boolean pwdChange = (((SQLException)ex.getCause()).getErrorCode() == 414);
					return new LoginStatus(false, username,
							hdbEx.getLocalizedMessage(), "",pwdChange);
				} else {
					return new LoginStatus(false, username,
							p.getUIText("msg.logon.credentialsNotValid"), "",false);
				}
			}
		} catch (UITextRessourceMissingException e) {
			return new LoginStatus(false, username, e.getLocalizedMessage(), "",false);
		}
	}

	/**
	 * Checks if the principal of the given authentication is allowed to access
	 * ILM. Therefore ILM READ or ILM DELETE roles has to be assigned.
	 * 
	 * @param a
	 *            the authentication for which ILM access should be checked
	 * @return true if the user has permission or false if not
	 */
	private boolean hasRoles(Authentication a) {
		if (a.getPrincipal() != null
				&& a.getPrincipal() instanceof HanaUserDetails) {
			HanaUserDetails p = (HanaUserDetails) a.getPrincipal();
			for (GrantedAuthority ga : p.getAuthorities()) {
				if (ga.getAuthority().equals("ROLE_SAP_DCM_ILM_READ") || ga.getAuthority().equals("ROLE_SAP_DCM_ILM_DELETE") || ga.getAuthority().equals("ROLE_SAP_DCM_USER_ADM"))
					return true;
			}
		}
		return false;
	}

	/**
	 * Extracts the redirect URL after successful login. Basically this is the
	 * redirect url of the login.
	 * 
	 * @return the redirect url
	 */
	private String getRedirectUrl() {
		// TODO first release this is fine but with more functionality with need
		// a landing page
		String root = ctx.getContextPath();
		
		if (root.endsWith("/"))
			return root + "index.html";
		else
			return root + "/index.html";
		
//		if (root.endsWith("/"))
//			return root + "ilm/customerData/";
//		else
//			return root + "/ilm/customerData/";
		// String url = "index.html";
		// if (s != null) {
		// SavedRequest savedRequest =
		// (SavedRequest)s.getAttribute(WebAttributes.SAVED_REQUEST);
		// if (savedRequest != null) {
		// url = savedRequest.getRedirectUrl();
		// }
		// }
		// return url;

	}

	/**
	 * Spring DI - inject authentication provider
	 * 
	 * @param provider
	 */
	public void setProvider(AuthenticationProvider provider) {
		this.provider = provider;
	}
}
