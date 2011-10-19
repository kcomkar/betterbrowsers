package com.sap.dcm.mobile.service.login;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
import com.sap.dcm.web.ui.impl.UITextProvider;
import com.sap.dna.spring.security.hana.HanaUserDetails;

public class MobLoginServiceImpl implements IMobLoginService {
	private Set<String> requiredRoles = new HashSet<String>();
	@Context
	private ServletContext ctx;
	
	public ServletContext getCtx() {
		return ctx;
	}

	public void setCtx(ServletContext ctx) {
		this.ctx = ctx;
	}

	public Set<String> getRequiredRoles() {
		return requiredRoles;
	}

	public void setRequiredRoles(Set<String> requiredRoles) {
		this.requiredRoles = requiredRoles;
	}

	private AuthenticationProvider provider;
	
	public AuthenticationProvider getProvider() {
		return provider;
	}

	public void setProvider(AuthenticationProvider provider) {
		this.provider = provider;
	}

	
	

	protected Set<String> getRoles(Authentication a){
		Set<String> roles = new HashSet<String>();
		if (a.getPrincipal() != null
				&& a.getPrincipal() instanceof HanaUserDetails) {
			HanaUserDetails p = (HanaUserDetails) a.getPrincipal();
			for (GrantedAuthority ga : p.getAuthorities()) {
				roles.add(ga.getAuthority());
			}
		}
		return roles; 
	}
	protected boolean hasRoles(Set<String> roles) {
		for(String role : roles){
			if(this.getRequiredRoles().contains(role)){
				return true;
			}
		}
		return false;
	}
	
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

	public MobLoginStatus login(String username, String password) {
		try {
			UITextProvider p = new UITextProvider(); // TODO localization
			try {
				Authentication authentication = provider
						.authenticate(new UsernamePasswordAuthenticationToken(
								username, password,
								new ArrayList<GrantedAuthority>()));
				if (authentication != null && authentication.isAuthenticated()) {
					// TODO check if user has one of the roles. getRoles
					Set<String> roles = getRoles(authentication);
					if (hasRoles(roles)) {
						SecurityContextHolder.getContext().setAuthentication(
								authentication);
						return new MobLoginStatus(
								authentication.isAuthenticated(), username,
								"login successful", getRedirectUrl(),false,roles);
					} else {
						authentication.setAuthenticated(false);
						return new MobLoginStatus(false, username,
								p.getUIText("msg.logon.roleMis"), "",false,roles);
					}
				} else {
					return new MobLoginStatus(false, username,	p.getUIText("msg.logon.credentialsNotValid"), "",false,new HashSet<String>());
				}
			} catch (AuthenticationException ex) {
				if (ex.getCause() instanceof SQLException) {
					// TODO localization
					HDBWrappedException hdbEx = new HDBWrappedException(
							(SQLException) ex.getCause());
					boolean pwdChange = (((SQLException)ex.getCause()).getErrorCode() == 414);
					return new MobLoginStatus(false, username,
							hdbEx.getLocalizedMessage(), "",pwdChange,new HashSet<String>());
				} else {
					return new MobLoginStatus(false, username,
							p.getUIText("msg.logon.credentialsNotValid"), "",false,new HashSet<String>());
				}
			}
		} catch (UITextRessourceMissingException e) {
			return new MobLoginStatus(false, username, e.getLocalizedMessage(), "",false,new HashSet<String>());
		}
	}
}
