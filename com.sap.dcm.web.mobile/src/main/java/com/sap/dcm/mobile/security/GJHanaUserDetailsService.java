// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HanaUserDetailsService.java

package com.sap.dcm.mobile.security;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import com.sap.db.jdbcext.DataSourceSAP;

// Referenced classes of package com.sap.dna.spring.security.hana:
//			HanaUserDetails, HanaAuthenticationProviderMessageSource

public class GJHanaUserDetailsService
{
	private class HanaUserRowMapper
		implements RowMapper
	{

		final GJHanaUserDetailsService this$0;

		public GJHanaUserDetails mapRow(ResultSet rs, int rowNum)
			throws SQLException
		{
			boolean credentialsExpired = rs.getBoolean("PASSWORD_CHANGE_NEEDED");
			boolean enabled = !rs.getBoolean("USER_DEACTIVATED");
			boolean locked = rs.getBoolean("LOCKED");
			boolean accountExpired = false;
			Collection userAuthorities = toGrantedAuthorities(getGrantedAuthorities(dataSource.getUsername(), GRANTEE_TYPE.USER, null));
			GJHanaUserDetails userDetails = new GJHanaUserDetails(dataSource, userAuthorities, dataSource.getUsername(), enabled, locked, accountExpired, credentialsExpired, null);
			logger.debug(userDetails);
			return userDetails;
		}

		/*public volatile Object mapRow(ResultSet x0, int x1)
			throws SQLException
		{
			return mapRow(x0, x1);
		}*/

		private HanaUserRowMapper()
		{
			this$0 = GJHanaUserDetailsService.this;
		}

	}

	public static final class GRANTEE_TYPE
	{

		public static final GRANTEE_TYPE USER;
		public static final GRANTEE_TYPE ROLE;
		private static final GRANTEE_TYPE $VALUES[];

		public static GRANTEE_TYPE[] values()
		{
			return (GRANTEE_TYPE[])$VALUES.clone();
		}
		
		public String typeName;
		public int typeValue;

		static 
		{
			USER = new GRANTEE_TYPE("USER", 0);
			ROLE = new GRANTEE_TYPE("ROLE", 1);
			$VALUES = (new GRANTEE_TYPE[] {
				USER, ROLE
			});
		}

		public GRANTEE_TYPE(String s, int i)
		{
			typeName = s;
			typeValue = i;
		}
	}


	protected final Log logger = LogFactory.getLog("spring.security.hana");
	protected MessageSourceAccessor messages;
	private SingleConnectionDataSource dataSource;
	private static final String ROLE_PREFIX = "ROLE_";

	private GJHanaUserDetailsService(Connection connection, String username)
	{
		messages = GJHanaAuthenticationProviderMessageSource.getAccessor();
		this.dataSource = new SingleConnectionDataSource(connection,false);
		this.dataSource.setUsername(username);
	}

	public static UserDetails retrieveUserDetails(Connection connection, String username)
	{
		GJHanaUserDetailsService inst = new GJHanaUserDetailsService(connection,username);
		return inst.loadUserByUsername();
	}

	private UserDetails loadUserByUsername()
		throws DataAccessException
	{
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String USER_DETAILS = "SELECT U.USER_NAME, U.PASSWORD_CHANGE_NEEDED, U.USER_DEACTIVATED, CASE WHEN ( U.INVALID_CONNECT_ATTEMPTS >= (SELECT \"VALUE\" from M_PASSWORD_POLICY where property = 'maximum_invalid_connect_attempts') ) THEN 'TRUE' ELSE 'FALSE' END AS LOCKED FROM USERS AS U WHERE USER_NAME = ?";
		RowMapper rm = new HanaUserRowMapper();
		GJHanaUserDetails userDetails = (GJHanaUserDetails)template.queryForObject("SELECT U.USER_NAME, U.PASSWORD_CHANGE_NEEDED, U.USER_DEACTIVATED, CASE WHEN ( U.INVALID_CONNECT_ATTEMPTS >= (SELECT \"VALUE\" from M_PASSWORD_POLICY where property = 'maximum_invalid_connect_attempts') ) THEN 'TRUE' ELSE 'FALSE' END AS LOCKED FROM USERS AS U WHERE USER_NAME = ?", new Object[] {
			dataSource.getUsername().toUpperCase()
		}, new int[] {
			12
		}, rm);
		
		return userDetails;
	}

	private Collection getGrantedAuthorities(String name, GRANTEE_TYPE granteeType, List processedRoles)
	{
		if (processedRoles == null)
			processedRoles = new ArrayList();
		if (granteeType == GRANTEE_TYPE.ROLE)
		{
			if (processedRoles.contains(name))
				return new ArrayList();
			processedRoles.add(name);
		}
		JdbcTemplate template = new JdbcTemplate(dataSource);
		List tmpRoles = null;
		String ROLES_SQL = "SELECT ROLE_NAME FROM GRANTED_ROLES WHERE GRANTEE = ? AND GRANTEE_TYPE = ?";
		if (granteeType == GRANTEE_TYPE.USER)
		{
			tmpRoles = template.queryForList("SELECT ROLE_NAME FROM GRANTED_ROLES WHERE GRANTEE = ? AND GRANTEE_TYPE = ?", new String[] {
				name.toUpperCase(), GRANTEE_TYPE.USER.typeName
			}, new int[] {
				12, 12
			}, String.class);
			/*for (Iterator i$ = tmpRoles.iterator(); i$.hasNext();)
			{
				String role = (String)i$.next();
				Collection assignedRoles = getGrantedAuthorities(role, GRANTEE_TYPE.ROLE, processedRoles);
				Iterator j$ = assignedRoles.iterator();
				while (j$.hasNext()) 
				{
					String assignedRole = (String)j$.next();
					if (!tmpRoles.contains(assignedRole))
						tmpRoles.add(assignedRole);
				}
			}*/

		} else
		if (granteeType == GRANTEE_TYPE.ROLE)
		{
			tmpRoles = template.queryForList("SELECT ROLE_NAME FROM GRANTED_ROLES WHERE GRANTEE = ? AND GRANTEE_TYPE = ?", new String[] {
				name.toUpperCase(), GRANTEE_TYPE.ROLE.typeName
			}, new int[] {
				12, 12
			}, String.class);
			for (Iterator i$ = tmpRoles.iterator(); i$.hasNext();)
			{
				String role = (String)i$.next();
				Collection assignedRoles = getGrantedAuthorities(role, GRANTEE_TYPE.ROLE, processedRoles);
				Iterator j$ = assignedRoles.iterator();
				while (j$.hasNext()) 
				{
					String assignedRole = (String)j$.next();
					if (!tmpRoles.contains(assignedRole))
						tmpRoles.add(assignedRole);
				}
			}

		}
		return tmpRoles;
	}

	private Collection toGrantedAuthorities(Collection grantedAuthorities)
	{
		List roles = new ArrayList();
		if (grantedAuthorities != null)
		{
			String grantedAuthority;
			for (Iterator i$ = grantedAuthorities.iterator(); i$.hasNext(); roles.add(new GrantedAuthorityImpl((new StringBuilder()).append("ROLE_").append(grantedAuthority).toString())))
				grantedAuthority = (String)i$.next();

		}
		logger.debug((new StringBuilder()).append("Granted authorities: ").append(roles).toString());
		return roles;
	}



}
