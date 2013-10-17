package com.sap.dcm.web.ilm.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.sap.dcm.web.core.model.ServiceResponse;
import com.sap.dcm.web.core.model.ServiceResponseMessage;
import com.sap.dcm.web.exception.AbstractDCMException;
import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.handler.BasicDCMExceptionHandler;
import com.sap.dcm.web.exception.handler.ExceptionHandler;
import com.sap.dcm.web.exception.ui.UITextRessourceMissingException;
import com.sap.dcm.web.exception.ui.ValidationException;
import com.sap.dcm.web.exception.util.ResultSetJSONConvertException;
import com.sap.dcm.web.ilm.CustomerData;
import com.sap.dcm.web.ilm.model.CustomerDataResponse;
import com.sap.dcm.web.security.UserUtils;
import com.sap.dcm.web.ui.impl.UITextProvider;
import com.sap.dcm.web.util.DataInputValidation;
import com.sap.dcm.web.util.DateUtils;
import com.sap.dcm.web.util.ResultSetToJSONConverter;

/**
 * Imkplementation of the ILM customer service
 * @author D049641
 * 
 */
public class CustomerDataImpl implements CustomerData {
	private static Logger log = Logger.getLogger(CustomerDataImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.ilm.CustomerData#getConstomerData(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public Response getConstomerData(String customerId, String companyId,
			String representation) {
		Locale userLocale = UserUtils.getUserLanguage();
		
		ExceptionHandler<AbstractDCMException> eh = new BasicDCMExceptionHandler(userLocale);

		// validate input
		checkCompanyIdAndCustomerId(eh, customerId, companyId);

		if (!eh.hasErrorOccured()) {
			Connection c = null;
			try {
				c = UserUtils.getDBConnection();

				// Retrieve dunnings
				PreparedStatement psDun = c
						.prepareStatement("SELECT * FROM \"COLM\".\"DUNNING_INVOICES\" WHERE CUSTOMER_ID = ? AND COMPANY_CODE = ? ");
				psDun.setString(1, customerId);
				psDun.setString(2, companyId);

				ResultSet rsDun = psDun.executeQuery();
				JSONObject dunnings = ResultSetToJSONConverter.convert(rsDun,representation, userLocale);
				rsDun.close();
				psDun.close();

				// build result
				JSONObject result = new JSONObject();
				try {
					result.put("dunnings", dunnings);
					result.put("deleteAllowed", UserUtils.hasRole("ROLE_SAP_DCM_ILM_DELETE"));
					
					CustomerDataResponse resp = new CustomerDataResponse();
					resp.setDunnings(dunnings.toString());
					resp.setDeleteAllowed(UserUtils.hasRole("ROLE_SAP_DCM_ILM_DELETE"));
					return Response.ok().entity(resp).build();
				} catch (JSONException ex) {
					throw new ResultSetJSONConvertException(ex);
				}
			} catch (SQLException e) {
				eh.add(new HDBWrappedException(e));
			} catch (AbstractDCMException e) {
				eh.add(e);
			} finally {
				if (c != null)
					try {
						c.close();
					} catch (SQLException e) {
						log.info("Close Connection");
						log.debug(e);
					}
			}
		}
		CustomerDataResponse resp = new CustomerDataResponse();
		resp.setMessages(eh.getExceptionsAsResponseMessages());
		
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.ilm.CustomerData#deleteCustomerData(java.lang.String,
	 * java.lang.String, java.util.Date)
	 */
	public Response deleteDunningInvoices(String customerId, String companyId,
			String lastdunningDate) {
		Locale userLocale = UserUtils.getUserLanguage();
		
		ExceptionHandler<AbstractDCMException> eh = new BasicDCMExceptionHandler(userLocale);

		// validate input
		checkCompanyIdAndCustomerId(eh, customerId, companyId);
		Date d = DateUtils.convertISODateString(lastdunningDate);
		if (!DataInputValidation.isValidDate(d, new GregorianCalendar(1972, 1,
				1).getTime(), new Date())) {
			eh.add(new ValidationException("msg.ilm.custData.keyDate.notValid",
					"Key date not valid"));
		}

		if (!eh.hasErrorOccured()) {
			// Get the connection and trigger stored procedure for deletion
			Connection c = null;
			try {
				c = UserUtils.getDBConnection();
				PreparedStatement ps = c
						.prepareStatement("call \"COLM\".\"ILM_DELETE_DUNNING_INVOICES\"(?,?,?) ");
				ps.setString(1, companyId);
				ps.setString(2, customerId);
				ps.setDate(3, new java.sql.Date(d.getTime()));
				ps.executeUpdate();

				String text = "";
				try {
					UITextProvider p = new UITextProvider(userLocale);
					text = p.getUIText("msg.ilm.custDate.del.success");
				} catch (UITextRessourceMissingException e) {
					text = "Delete executed successfully.";
				}
				ServiceResponse resp = new ServiceResponse();
				resp.addMessage(new ServiceResponseMessage(text, false));
				return Response.ok().entity(resp).type(MediaType.APPLICATION_JSON_TYPE).build();
			} catch (SQLException e) {
				log.debug(e);
				eh.add(new HDBWrappedException(e));
			} catch (AbstractDCMException e) {
				eh.add(e);
			} finally {
				if (c != null) {
					try {
						c.close();
					} catch (SQLException e) {
						log.debug("cannont close db connection", e);
					}
				}
			}

			/*
			 * DDL DROP PROCEDURE "COLM"."ILM_DELETE_DUNNING_INVOICES"; CREATE
			 * PROCEDURE "COLM"."ILM_DELETE_DUNNING_INVOICES" (IN company
			 * VARCHAR (4), IN customer VARCHAR (10), IN key_dunning_date DATE )
			 * LANGUAGE SQLSCRIPT AS BEGIN DELETE FROM "COLM"."DUNNING_INVOICES"
			 * WHERE company_code = :company and customer_id = :customer and
			 * dunning_date <= :key_dunning_date ;
			 * 
			 * END ;
			 */
			
			ServiceResponse resp = new ServiceResponse();
			resp.setMessages(eh.getExceptionsAsResponseMessages());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(resp)
					.type(MediaType.TEXT_PLAIN_TYPE).build();
		} else {
			ServiceResponse resp = new ServiceResponse();
			resp.setMessages(eh.getExceptionsAsResponseMessages());
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(resp).type(MediaType.APPLICATION_JSON_TYPE).build();
		}
	}

	/**
	 * Checks if the company id and the customer id is valid
	 * @param eh = the exception handler for message handling
	 * @param customerId = the customer id which should be checked
	 * @param companyId = the company id which should be checked
	 */
	private void checkCompanyIdAndCustomerId(
			ExceptionHandler<AbstractDCMException> eh, String customerId,
			String companyId) {
		//check customer
		if (!DataInputValidation.isValidName(customerId)) {
			eh.add(new ValidationException("msg.customerId.notValid",
					"Customer ID not valid"));
		}

		//check company 
		if (!DataInputValidation.isValidName(companyId)) {
			eh.add(new ValidationException("msg.companyId.notValid",
					"Company ID not valid"));
		}
	}

	
}
