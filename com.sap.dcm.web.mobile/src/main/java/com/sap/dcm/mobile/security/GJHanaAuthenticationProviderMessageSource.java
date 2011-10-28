// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   HanaAuthenticationProviderMessageSource.java

package com.sap.dcm.mobile.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class GJHanaAuthenticationProviderMessageSource extends ReloadableResourceBundleMessageSource
{

	protected final Log logger = LogFactory.getLog("spring.security.hana");

	public GJHanaAuthenticationProviderMessageSource()
	{
		setBasename("com/sap/dna/spring/security/messages");
		logger.debug("Reloadable resource message source instantiated.");
	}

	public static MessageSourceAccessor getAccessor()
	{
		return new MessageSourceAccessor(new GJHanaAuthenticationProviderMessageSource());
	}
}
