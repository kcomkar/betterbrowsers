package com.sap.dcm.web.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.springframework.security.access.AccessDeniedException;

import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.security.UserUtils;
import com.sap.dcm.web.ui.impl.UITextProvider;

/**
 * Mapping of Access Denied Exceptions for JAX RS Services
 * @author D049641
 *
 */
public class SecurityExceptionMapper implements
		ExceptionMapper<AccessDeniedException> {

	/*
	 * (non-Javadoc)
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	public Response toResponse(AccessDeniedException exception) {
		try {
			UITextProvider p = new UITextProvider(UserUtils.getUserLanguage());
			String text = p.getUIText("msg.sec.accessNotAllowed");
			return Response.status(Response.Status.FORBIDDEN).entity(text).build();
		} catch (UITextRessourceMissingException e) {
			return Response.status(Response.Status.FORBIDDEN).entity("No Permission").build();
		}	
	}

}
