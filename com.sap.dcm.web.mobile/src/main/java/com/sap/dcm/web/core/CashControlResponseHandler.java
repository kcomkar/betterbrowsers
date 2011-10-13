package com.sap.dcm.web.core;

import java.util.Arrays;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.ResponseHandler;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;

/**
 * This class is used to add cash control information to the response. For get messages no cashing should be done.
 * @author D049641
 *
 */
public class CashControlResponseHandler implements ResponseHandler {
	private static final String METHOD_TYPE = "GET";

	/*
	 * (non-Javadoc)
	 * @see org.apache.cxf.jaxrs.ext.ResponseHandler#handleResponse(org.apache.cxf.message.Message, org.apache.cxf.jaxrs.model.OperationResourceInfo, javax.ws.rs.core.Response)
	 */
	public Response handleResponse(Message message,
			OperationResourceInfo operationResourceInfo, Response response) {
		if (operationResourceInfo.getHttpMethod().equals(METHOD_TYPE)) {
			response.getMetadata().put("Cache-Control",	Arrays.asList(new Object[] { "no-cache" }));
		}
		return response;
	}
}
