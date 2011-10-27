// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HanaAuthenticationProvider.java

package com.sap.dcm.mobile.security;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

import com.sap.db.jdbc.exceptions.jdbc40.SQLInvalidAuthorizationSpecException;

// Referenced classes of package com.sap.dna.spring.security.hana:
//			HanaAuthenticationProviderMessageSource, HanaUserDetailsService

public class GJHanaAuthenticationProvider
	implements AuthenticationProvider
{
	private class DefaultPostAuthenticationChecks
		implements UserDetailsChecker
	{

		final GJHanaAuthenticationProvider this$0;

		public void check(UserDetails userDetails)
		{
			if (!userDetails.isAccountNonLocked())
			{
				logger.debug("User account is locked");
				throw new LockedException(messages.getMessage("error.user.account.locked", "User account is locked"), userDetails);
			}
			if (!userDetails.isEnabled())
			{
				logger.debug("User account is disabled");
				throw new DisabledException(messages.getMessage("error.user.account.disabled", "User is disabled"), userDetails);
			}
			if (!userDetails.isAccountNonExpired())
			{
				logger.debug("User account is expired");
				throw new AccountExpiredException(messages.getMessage("error.user.account.expired", "User account has expired"), userDetails);
			}
			if (!userDetails.isCredentialsNonExpired())
			{
				logger.debug("User account credentials have expired");
				throw new CredentialsExpiredException(messages.getMessage("error.user.account.credentials.expired", "User credentials have expired"), userDetails);
			} else
			{
				return;
			}
		}

		private DefaultPostAuthenticationChecks()
		{
			this$0 = GJHanaAuthenticationProvider.this;
			
		}

	}


	protected final Log logger = LogFactory.getLog("spring.security.hana");
	protected MessageSourceAccessor messages;
	private boolean forcePrincipalAsString;
	private UserDetailsChecker postAuthenticationChecks;
	private DataSource dataSource;
	public GJHanaAuthenticationProvider()
	{
		messages = GJHanaAuthenticationProviderMessageSource.getAccessor();
		forcePrincipalAsString = false;
		postAuthenticationChecks = new DefaultPostAuthenticationChecks();
	}


	


	public DataSource getDataSource() {
		return dataSource;
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public Authentication authenticate(Authentication authentication)
		throws AuthenticationException
	{
		//Assert.isInstanceOf(org/springframework/security/authentication/UsernamePasswordAuthenticationToken, authentication, messages.getMessage("error.authentication.token.not.supported", "Only support for UsernamePasswordAuthenticationToken!"));
		String username = authentication.getPrincipal() != null ? authentication.getName() : "NONE_PROVIDED";
		String password = authentication.getCredentials() != null ? (String)authentication.getCredentials() : "NONE_PROVIDED";
		/*dataSource.setUser(username);
		dataSource.setPassword(password);*/
		Connection connection = null;
		try
		{
			connection = dataSource.getConnection(username,password);
			Statement stmt = connection.createStatement();
			stmt.execute("SELECT count(*) FROM USERS");
		}
		catch (SQLException se)
		{
			se.printStackTrace();
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			throw new AuthenticationServiceException(se.getLocalizedMessage(), se);
		}
		UserDetails userDetails = null;
		try
		{
			userDetails = GJHanaUserDetailsService.retrieveUserDetails(connection,username);
		}
		catch (DataAccessException ex)
		{
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (ex.getRootCause() != null && (ex.getRootCause() instanceof SQLInvalidAuthorizationSpecException))
				throw new BadCredentialsException(messages.getMessage("error.authentication.bad.credentials", "Bad Credentials"), ex);
			else
				throw new AuthenticationServiceException(ex.getMessage(), ex);
		}
		Assert.notNull(userDetails, messages.getMessage("error.authentication.userdetails.null", "User details references null!"));
		try{
			postAuthenticationChecks.check(userDetails);
		}
		catch(Exception e){
			try {
				connection.close();
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			postAuthenticationChecks.check(userDetails);
		}
		
		Object principalToReturn = userDetails;
		if (forcePrincipalAsString)
			principalToReturn = userDetails.getUsername();
		return createSuccessAuthentication(principalToReturn, authentication, userDetails,connection);
	}

	public static class GJAuthentication extends UsernamePasswordAuthenticationToken{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5248044974964782108L;
		public Connection connection;
		public GJAuthentication(Object principal, Object credentials,
				Collection<? extends GrantedAuthority> authorities, Connection connection) {
			super(principal, credentials, authorities);
			this.connection = connection;
		}
		
		
	}
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user, Connection connection)
	{
		GJAuthentication result = new GJAuthentication(principal, null, user.getAuthorities(), connection);
		result.setDetails(authentication.getDetails());
		return result;
	}


	public void setForcePrincipalAsString(boolean forcePrincipalAsString)
	{
		this.forcePrincipalAsString = forcePrincipalAsString;
	}

	public void setMessageSource(MessageSource messageSource)
	{
		messages = new MessageSourceAccessor(messageSource);
	}

	public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks)
	{
		this.postAuthenticationChecks = postAuthenticationChecks;
	}

	public boolean supports(Class authentication)
	{
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
