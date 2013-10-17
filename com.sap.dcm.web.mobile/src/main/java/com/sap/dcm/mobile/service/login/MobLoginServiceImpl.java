package com.sap.dcm.mobile.service.login;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sap.db.jdbcext.DataSourceSAP;
import com.sap.dcm.mobile.security.GJHanaAuthenticationProvider;
import com.sap.dcm.mobile.security.GJHanaUserDetails;
import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.ui.impl.UITextProvider;

public class MobLoginServiceImpl implements IMobLoginService, ApplicationContextAware {
	private Set<String> requiredRoles = new HashSet<String>();
	
	
	
	@Context
	private ServletContext ctx;
	
	public ServletContext getCtx() {
		return ctx;
	}

	public void setCtx(ServletContext ctx) {
		this.ctx = ctx;
	}

	@Context
	private HttpServletRequest request;
	
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
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
				&& a.getPrincipal() instanceof GJHanaUserDetails) {
			GJHanaUserDetails p = (GJHanaUserDetails) a.getPrincipal();
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
				long begin = System.currentTimeMillis();
				
				Authentication authentication = provider
						.authenticate(new UsernamePasswordAuthenticationToken(
								username, password,
								new ArrayList<GrantedAuthority>()));
				System.out.println(System.currentTimeMillis() - begin);
				if (authentication != null && authentication.isAuthenticated()) {
					// TODO check if user has one of the roles. getRoles
					Set<String> roles = getRoles(authentication);
					if (hasRoles(roles)) {
						SecurityContextHolder.getContext().setAuthentication(
								authentication);
						
						// pass
						
						System.out.println(this.getRequest().getSession().getAttributeNames());
						
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
	
	public static void main(String [] args){
		GJHanaAuthenticationProvider provider = new GJHanaAuthenticationProvider();
		DataSourceSAP dataSource = new DataSourceSAP();
		dataSource.setServerName("coe-he-10");
		dataSource.setPort(30815);
		provider.setDataSource(dataSource);
		Authentication authentication = provider
				.authenticate(new UsernamePasswordAuthenticationToken(
						"GUJING", "Welcome1Gujing",
						new ArrayList<GrantedAuthority>()));
		System.out.println(authentication);
		
	}

	public String changePassword(String username, String oldPassword,
			String newPassword) {
		DataSourceSAP DataSourceSAP = (DataSourceSAP)this.getApplicationContext().getBean("dataSource");
		Connection connection = null;
		try{
			connection = DataSourceSAP.getConnection(username, oldPassword);
			Statement s = connection.createStatement();
			s.execute("ALTER USER " + username + " IDENTIFIED BY "
					+ newPassword);
			
			return "";
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
		finally{
			if(connection != null){
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	private ApplicationContext applicationContext;
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}


