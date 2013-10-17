package com.sap.dcm.web.user.pwd.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sap.db.jdbcext.DataSourceSAP;
import com.sap.dcm.web.core.model.ServiceResponse;
import com.sap.dcm.web.exception.AbstractDCMException;
import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.handler.BasicDCMExceptionHandler;
import com.sap.dcm.web.exception.handler.ExceptionHandler;
import com.sap.dcm.web.exception.ui.ValidationException;
import com.sap.dcm.web.user.pwd.PasswordService;
import com.sap.dcm.web.util.DataInputValidation;

/**
 * Implementation of Password Change Rest service
 * 
 * @author D049641
 * 
 */
public class PasswordServiceImpl implements PasswordService {
	
	private static Logger log = Logger.getLogger(PasswordServiceImpl.class);

	private DataSourceSAP dataSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.user.pwd.PasswordService#changePassword(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Response changePassword(String user, String oldPasssword,
			String newPassword, String newPasswordRepeat, String locale) {
		// Initialize Exception Hander
		ExceptionHandler<AbstractDCMException> eh = new BasicDCMExceptionHandler(
				locale);

		// validate the data
		validateOldCredentials(eh, user, oldPasssword);
		validateNewPassword(eh, newPassword, newPasswordRepeat);

		// try to connect to verify old credentials
		Connection c = null;
		if (!eh.hasErrorOccured()) {
			try {
				c = dataSource.getConnection(user, oldPasssword);
			} catch (SQLException e) {
				log.debug("Cannot connect to DB. ", e);
				eh.add(new HDBWrappedException(e));
			}
		}

		// If no error has been occurred -> update user
		if (!eh.hasErrorOccured()) {
			try {
				Statement s = c.createStatement();
				s.execute("ALTER USER " + user + " IDENTIFIED BY "
						+ newPassword);
				s.close();
			} catch (SQLException e) {
				eh.add(new HDBWrappedException(e));
			}finally{
				try {
					c.close();
				} catch (SQLException e) {
					log.info("Close connection");
					log.error("cannot close DB Connection", e);
				}
			}
		}

		// create result
		if (eh.hasErrorOccured()) {
			ServiceResponse resp = new ServiceResponse();
			resp.setMessages(eh.getExceptionsAsResponseMessages());
			
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(resp).type(MediaType.APPLICATION_JSON_TYPE).build();
		} else {
			return Response.ok().build();
		}
	}

	/**
	 * Validates the curretn user credentials
	 * 
	 * @param eh
	 *            = the exception handler
	 * @param user
	 *            = the user name
	 * @param password
	 *            = the current user password
	 */
	private void validateOldCredentials(
			ExceptionHandler<AbstractDCMException> eh, String user,
			String password) {
		// check username
		if (user == null) {
			eh.add(new ValidationException("msg.pwdChg.missUser", "Username"));
		} else {
			if (!DataInputValidation.isValidName(user))
				eh.add(new ValidationException("msg.pwdChg.invalidUser",
						"Username"));
		}

		// check password
		if (password == null) {
			eh.add(new ValidationException("msg.pwdChg.missPwdOld",
					"Old Password"));
		} else {
			if (!DataInputValidation.isValidPassword(password))
				eh.add(new ValidationException("msg.pwdChg.invalidPwdOld",
						"Password Old"));
		}
	}

	/**
	 * Validates the new password
	 * 
	 * @param eh
	 *            = teh exception handler
	 * @param pwdNew
	 *            = the new user password
	 * @param pwdRepeat
	 *            = the repeated new user password
	 */
	private void validateNewPassword(ExceptionHandler<AbstractDCMException> eh,
			String pwdNew, String pwdRepeat) {
		if (pwdNew == null) {
			eh.add(new ValidationException("msg.pwdChg.missPwdNew",
					"New Password"));
		} else {
			if (!DataInputValidation.isValidPassword(pwdNew))
				eh.add(new ValidationException("msg.pwdChg.invalidPwdNew",
						"New Password"));
		}
		if (!pwdNew.equals(pwdRepeat))
			eh.add(new ValidationException("msg.pwdChg.newPwdNotMatch",
					"New Password/New password repeat"));
	}



	/**
	 * Spring DI - inject data source.
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSourceSAP dataSource) {
		this.dataSource = dataSource;
	}
}
