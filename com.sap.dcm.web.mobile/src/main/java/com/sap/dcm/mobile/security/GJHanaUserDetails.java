// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HanaUserDetails.java

package com.sap.dcm.mobile.security;

import java.util.Collection;

import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class GJHanaUserDetails
	implements UserDetails
{

	private static final long serialVersionUID = 1L;
	private SingleConnectionDataSource dataSource;
	private Collection grantedAuthorities;
	private String username;
	private boolean enabled;
	private boolean accountLocked;
	private boolean accountExpired;
	private boolean credentialsExpired;
	private String userLanguage;

	public GJHanaUserDetails(SingleConnectionDataSource dataSource, Collection grantedAuthorities, String username, boolean enabled, boolean accountLocked, boolean accountExpired, boolean credentialsExpired, 
			String userLanguage)
	{
		this.dataSource = dataSource;
		this.grantedAuthorities = grantedAuthorities;
		this.username = username;
		this.enabled = enabled;
		this.accountLocked = accountLocked;
		this.accountExpired = accountExpired;
		this.credentialsExpired = credentialsExpired;
		setUserLanguage(userLanguage);
	}

	public Collection<GrantedAuthority> getAuthorities()
	{
		return grantedAuthorities;
	}

	
	public SingleConnectionDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(SingleConnectionDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getPassword()
	{
		return null;
	}

	public String getUsername()
	{
		return username;
	}

	public boolean isAccountNonExpired()
	{
		return !accountExpired;
	}

	public boolean isAccountNonLocked()
	{
		return !accountLocked;
	}

	public boolean isCredentialsNonExpired()
	{
		return !credentialsExpired;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public String getUserLanguage()
	{
		return userLanguage;
	}

	public void setUserLanguage(String userLanguage)
	{
		this.userLanguage = userLanguage;
	}

	public String toString()
	{
		return (new StringBuilder()).append("HanaUserDetails [dataSource=").append(dataSource).append(", grantedAuthorities=").append(grantedAuthorities).append(", username=").append(username).append(", enabled=").append(enabled).append(", accountLocked=").append(accountLocked).append(", accountExpired=").append(accountExpired).append(", credentialsExpired=").append(credentialsExpired).append("]").toString();
	}
}
