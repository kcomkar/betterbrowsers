package com.sap.dcm.web.user.mgmt.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sap.dcm.web.core.model.ServiceResponse;
import com.sap.dcm.web.exception.AbstractDCMException;
import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.handler.BasicDCMExceptionHandler;
import com.sap.dcm.web.exception.handler.ExceptionHandler;
import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dcm.web.exception.ui.ValidationException;
import com.sap.dcm.web.security.RoleUtils;
import com.sap.dcm.web.security.UserUtils;
import com.sap.dcm.web.user.mgmt.UserMgmtService;
import com.sap.dcm.web.user.mgmt.model.AssignedRoles;
import com.sap.dcm.web.user.mgmt.model.HanaRole;
import com.sap.dcm.web.user.mgmt.model.HanaUser;
import com.sap.dcm.web.user.mgmt.model.RoleAssignment;
import com.sap.dcm.web.user.mgmt.model.UserRoleAssignment;
import com.sap.dcm.web.user.pwd.impl.PasswordServiceImpl;
import com.sap.dcm.web.util.DataInputValidation;

public class UserMgmtServiceImpl implements UserMgmtService {

	private static final Logger LOGGER = Logger
			.getLogger(PasswordServiceImpl.class);

	/**
	 * CREATE PROCEDURE "COLM".GRANT_USER_ROLE(IN USER_NAME NVARCHAR(256), IN
	 * ROLE NVARCHAR(256)) LANGUAGE SQLSCRIPT AS v_sql NVARCHAR(100); BEGIN
	 * v_sql := 'GRANT ' || :ROLE || ' to ' || :USER_NAME; exec ( :v_sql ); END;
	 * 
	 * @throws HDBWrappedException
	 * @throws AuthenticationException
	 */
	public Response assignRoles(UserRoleAssignment userRoleAssignments) {
		ExceptionHandler<AbstractDCMException> eh = new BasicDCMExceptionHandler(
				UserUtils.getUserLanguage());
		// Validate input
		List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
		validationExceptions.addAll(verifyPrincipal(userRoleAssignments
				.getUser()));
		for (RoleAssignment ra : userRoleAssignments.getRoleAssignments())
			validationExceptions.addAll(verifyRole(ra.getRole()));

		if (validationExceptions.isEmpty()) {
			Connection con = null;
			try {
				List<RoleAssignment> before = getRoleAssigments(userRoleAssignments
						.getUser().getName());
				RoleAssignment[] current = userRoleAssignments
						.getRoleAssignments();

				// determine delta
				List<HanaRole> rolesToBeGranted = new ArrayList<HanaRole>();
				List<HanaRole> rolesToBeRevoked = new ArrayList<HanaRole>();

				// determine delta
				for (RoleAssignment c : current) {
					// get the corresponding role assginment from before
					RoleAssignment b = null;
					for (RoleAssignment tmp : before)
						if (tmp.getRole().equals(c.getRole()))
							b = tmp;
					if (b != null) {
						if (c.isAssigned() && !b.isAssigned()) {
							// if the role is assigned but was not assigned
							// before the role has to be granted for the user
							rolesToBeGranted.add(c.getRole());
						} else if (!c.isAssigned() && b.isAssigned())
							// if the role is not assigned but was assigned
							// before the role has to be rovoked for the user
							rolesToBeRevoked.add(c.getRole());
					} else {
						if (c.isAssigned())
							rolesToBeGranted.add(c.getRole());
					}
				}

				con = UserUtils.getDBConnection();
				con.setAutoCommit(false);

				// Grant Roles
				String userName = userRoleAssignments.getUser().getName();
				String sqlGrant = "{call COLM.GRANT_USER_ROLE(?,?)}";
				CallableStatement stmtGrant = con.prepareCall(sqlGrant);
				for (HanaRole r : rolesToBeGranted) {
					stmtGrant.setString(1, userName);
					stmtGrant.setString(2, r.getName());
					stmtGrant.execute();
				}

				// Revoke Roles
				String sqlRevoke = "{call COLM.REVOKE_USER_ROLE(?,?)}";
				CallableStatement stmtRevoke = con.prepareCall(sqlRevoke);
				for (HanaRole r : rolesToBeRevoked) {
					stmtRevoke.setString(1, userName);
					stmtRevoke.setString(2, r.getName());
					stmtRevoke.execute();
				}

				// commit the changes
				con.commit();
				stmtGrant.close();
				stmtRevoke.close();
			} catch (SQLException e) {
				eh.add(new HDBWrappedException(e));
			} catch (AbstractDCMException e) {
				eh.add(e);
			} finally {
				try {
					LOGGER.info("Close DB connection");
					con.close();
				} catch (SQLException e) {
					LOGGER.error("Cannot close DB Connection", e);
				}
			}
		}

		// create result
		if (eh.hasErrorOccured()) {
			ServiceResponse resp = new ServiceResponse();
			resp.setMessages(eh.getExceptionsAsResponseMessages());
			return Response.status(Response.Status.BAD_REQUEST).entity(resp)
					.type(MediaType.APPLICATION_JSON).build();
		} else {
			return Response.ok().build();
		}
	}

	/**
	 * CREATE PROCEDURE "COLM".CREATE_USER(IN USER_NAME NVARCHAR (256), IN
	 * INITIAL_PASSWORD NVARCHAR(256)) LANGUAGE SQLSCRIPT AS v_sql
	 * NVARCHAR(100); BEGIN v_sql := 'CREATE USER ' || :USER_NAME || '
	 * IDENTIFIED BY '|| :INITIAL_PASSWORD; exec( :v_sql ); END;
	 */
	public Response createUser(String username, String password,
			String passwordRepeat) {
		ExceptionHandler<AbstractDCMException> eh = new BasicDCMExceptionHandler(
				UserUtils.getUserLanguage());

		HanaUser user = new HanaUser();
		user.setName(username);

		List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
		validationExceptions.addAll(verifyPrincipal(user));
		validationExceptions
				.addAll(verifyCredentials(password, passwordRepeat));

		if (validationExceptions.isEmpty()) {
			Connection con = null;
			try {
				con = UserUtils.getDBConnection();
				String createUserSql = "{call COLM.create_user(?,?)}";
				CallableStatement stmt = con.prepareCall(createUserSql);
				stmt.setString(1, username);
				stmt.setString(2, password);
				stmt.execute();
				stmt.close();
			} catch (SQLException e) {
				eh.add(new HDBWrappedException(e));
			} catch (AbstractDCMException e) {
				eh.add(e);
			} finally {
				try {
					LOGGER.info("Close DB connection");
					con.close();
				} catch (SQLException e) {
					LOGGER.error("Cannot close DB Connection", e);
				}
			}
		} else {
			for (ValidationException validationException : validationExceptions) {
				eh.add(validationException);
			}
		}

		// create result
		if (eh.hasErrorOccured()) {
			ServiceResponse resp = new ServiceResponse();
			resp.setMessages(eh.getExceptionsAsResponseMessages());
			return Response.status(Response.Status.BAD_REQUEST).entity(resp)
					.type(MediaType.APPLICATION_JSON).build();
		} else {
			return Response.ok().build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.user.mgmt.UserMgmtService#resetUserPassword(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	public Response resetUserPassword(String username, String password,
			String passwordRepeat) {
		ExceptionHandler<AbstractDCMException> eh = new BasicDCMExceptionHandler(
				UserUtils.getUserLanguage());

		HanaUser user = new HanaUser();
		user.setName(username);

		List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
		validationExceptions.addAll(verifyPrincipal(user));
		validationExceptions
				.addAll(verifyCredentials(password, passwordRepeat));

		if (validationExceptions.isEmpty()) {
			Connection con = null;
			try {
				con = UserUtils.getDBConnection();
				String createUserSql = "{call COLM.RESET_USER_PASSWORD(?,?)}";
				CallableStatement stmt = con.prepareCall(createUserSql);
				stmt.setString(1, username);
				stmt.setString(2, password);
				stmt.execute();
				stmt.close();
			} catch (SQLException e) {
				eh.add(new HDBWrappedException(e));
			} catch (AbstractDCMException e) {
				eh.add(e);
			} finally {
				try {
					LOGGER.info("Close DB connection");
					con.close();
				} catch (SQLException e) {
					LOGGER.error("Cannot close DB Connection", e);
				}
			}
		} else {
			for (ValidationException validationException : validationExceptions) {
				eh.add(validationException);
			}
		}

		// create result
		if (eh.hasErrorOccured()) {
			ServiceResponse resp = new ServiceResponse();
			resp.setMessages(eh.getExceptionsAsResponseMessages());
			return Response.status(Response.Status.BAD_REQUEST).entity(resp)
					.type(MediaType.APPLICATION_JSON).build();
		} else {
			return Response.ok().build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.user.mgmt.UserMgmtService#getRoles()
	 */
	public List<HanaRole> getRoles() throws HDBWrappedException,
			AuthenticationException {
		List<HanaRole> roles = new ArrayList<HanaRole>();

		Connection con = null;
		try {
			con = UserUtils.getDBConnection();
			String sql = "{call COLM.get_roles(?)}";
			CallableStatement stmt = con.prepareCall(sql);
			stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			while (rs.next()) {
				String role = rs.getString(1);
				if (RoleUtils.isApplicationRole(role)) {
					roles.add(new HanaRole(role, null));
				}
			}
			stmt.close();
		} catch (SQLException e) {
			throw new HDBWrappedException(e);
		} finally {
			try {
				LOGGER.info("Close DB connection");
				con.close();
			} catch (SQLException e) {
				LOGGER.error("Cannot close DB Connection", e);
			}
		}

		return roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.user.mgmt.UserMgmtService#getUsers()
	 */
	public List<HanaUser> getUsers() throws HDBWrappedException,
			AuthenticationException {
		List<HanaUser> users = new ArrayList<HanaUser>();

		Connection con = null;
		try {
			con = UserUtils.getDBConnection();
			String sql = "SELECT * FROM COLM.USER_VIEW";
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String username = rs.getString(1);
				if (UserUtils.isRelevantUser(username)) {
					users.add(new HanaUser(username, rs.getString(4), rs
							.getDate(5), rs.getDate(6), rs.getDate(7), rs
							.getDate(10), rs.getInt(8), rs.getBoolean(9), rs
							.getBoolean(11), rs.getBoolean(12), rs.getDate(13)));
				}
			}
			stmt.close();
		} catch (SQLException e) {
			throw new HDBWrappedException(e);
		} finally {
			try {
				LOGGER.info("Close DB connection");
				con.close();
			} catch (SQLException e) {
				LOGGER.error("Cannot close DB Connection", e);
			}
		}

		return users;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.user.mgmt.UserMgmtService#deleteUsers(java.util.List)
	 */
	public Response deleteUser(String username) {
		ExceptionHandler<AbstractDCMException> eh = new BasicDCMExceptionHandler(
				UserUtils.getUserLanguage());

		HanaUser user = new HanaUser();
		user.setName(username);

		List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
		validationExceptions.addAll(verifyPrincipal(user));

		if (validationExceptions.isEmpty()) {
			Connection con = null;
			try {
				con = UserUtils.getDBConnection();
				String createUserSql = "{call COLM.delete_user(?)}";
				CallableStatement stmt = con.prepareCall(createUserSql);
				stmt.setString(1, username);
				stmt.execute();
				stmt.close();
			} catch (SQLException e) {
				eh.add(new HDBWrappedException(e));
			} catch (AbstractDCMException e) {
				eh.add(e);
			} finally {
				try {
					LOGGER.info("Close DB connection");
					con.close();
				} catch (SQLException e) {
					LOGGER.error("Cannot close DB Connection", e);
				}
			}
		} else {
			for (ValidationException validationException : validationExceptions) {
				eh.add(validationException);
			}
		}

		// create result
		if (eh.hasErrorOccured()) {
			ServiceResponse resp = new ServiceResponse();
			resp.setMessages(eh.getExceptionsAsResponseMessages());
			return Response.status(Response.Status.BAD_REQUEST).entity(resp)
					.type(MediaType.APPLICATION_JSON).build();
		} else {
			return Response.ok().build();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.user.mgmt.UserMgmtService#getAssignedRoles(java.lang.
	 * String)
	 */
	public Response getAssignedRoles(String username) {
		ExceptionHandler<AbstractDCMException> eh = new BasicDCMExceptionHandler(
				UserUtils.getUserLanguage());

		HanaUser user = new HanaUser();
		user.setName(username);

		List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
		validationExceptions.addAll(verifyPrincipal(user));

		if (validationExceptions.isEmpty()) {
			try {
				List<HanaRole> roles = getAssignedRolesInt(username);
				AssignedRoles assignedRoles = new AssignedRoles(roles);
				return Response.ok().entity(assignedRoles)
						.type(MediaType.APPLICATION_JSON).build();
			} catch (AbstractDCMException e) {
				eh.add(e);
			}
		}
		ServiceResponse resp = new ServiceResponse();
		resp.setMessages(eh.getExceptionsAsResponseMessages());
		return Response.status(Response.Status.BAD_REQUEST).entity(resp)
				.type(MediaType.APPLICATION_JSON).build();
	}

	/**
	 * Retrieves the assigned roles of a user
	 * 
	 * @param username
	 *            = the user name
	 * @return the list of assigned roles
	 * @throws HDBWrappedException
	 *             if an database error occurs
	 * @throws AuthenticationException
	 *             if an authentication error occurs
	 */
	private List<HanaRole> getAssignedRolesInt(String username)
			throws HDBWrappedException, AuthenticationException {
		List<HanaRole> roles = new ArrayList<HanaRole>();
		try {
			Connection c = UserUtils.getDBConnection();
			String sql = "select * from \"COLM\".\"USER_ROLE_VIEW\" WITH PARAMETERS ('placeholder' = ('$$grantee$$', '''"
					+ username + "'''))";
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				String rolename = rs.getString(1);
				if (RoleUtils.isApplicationRole(rolename))
					roles.add(new HanaRole(rolename, null));
			}
			return roles;
		} catch (SQLException e) {
			throw new HDBWrappedException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.user.mgmt.UserMgmtService#getRoleAssigments(java.lang
	 * .String)
	 */
	public List<RoleAssignment> getRoleAssigments(String username)
			throws HDBWrappedException, AuthenticationException {
		List<HanaRole> assigned = getAssignedRolesInt(username);
		List<HanaRole> roles = getRoles();

		List<RoleAssignment> result = new ArrayList<RoleAssignment>();
		for (HanaRole role : roles) {
			if (assigned.contains(role)) {
				result.add(new RoleAssignment(true, role));
			} else {
				result.add(new RoleAssignment(false, role));
			}
		}

		return result;
	}

	/**
	 * Verifies the user credentials
	 * 
	 * @param password
	 *            the user password
	 * @param passwordRepeat
	 *            the repeated user password
	 * @return list of errors if any occurs
	 */
	private Collection<? extends ValidationException> verifyCredentials(
			String password, String passwordRepeat) {
		List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
		if (password == null) {
			validationExceptions.add(new ValidationException("msg.pwdNotValid",
					"password"));
		}
		if (!DataInputValidation.isValidPassword(password)) {
			validationExceptions.add(new ValidationException("msg.pwdNotValid",
					"password"));
		}
		if (!password.equals(passwordRepeat)) {
			validationExceptions.add(new ValidationException("msg.pwdNotMatch",
					"password repeat"));
		}
		return validationExceptions;
	}

	/**
	 * Verifies the principal
	 * 
	 * @param user
	 *            the hana user principal
	 * @return list of errors if any occurs
	 */
	private Collection<? extends ValidationException> verifyPrincipal(
			HanaUser user) {
		List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
		if (user == null) {
			validationExceptions.add(new ValidationException(
					"msg.pwdChg.missUser", "username"));
		} else {
			if (!DataInputValidation.isValidName(user.getName())) {
				validationExceptions.add(new ValidationException(
						"msg.pwdChg.invalidUser", "username"));
			}
			if (!UserUtils.isRelevantUser(user.getName())) {
				validationExceptions.add(new ValidationException(
						"msg.technicalUserNotAllowed", "username"));
			}
		}
		return validationExceptions;
	}

	/**
	 * Verifies the a hana role
	 * 
	 * @param role
	 *            the hana role
	 * @return list of errors if any occurs
	 */
	private Collection<? extends ValidationException> verifyRole(HanaRole role) {
		List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
		if (role == null) {
			validationExceptions.add(new ValidationException(
					"msg.mgmt.role.invalid", "role"));
		} else {
			if (!DataInputValidation.isValidName(role.getName())) {
				validationExceptions.add(new ValidationException(
						"msg.mgmt.role.invalid", "role"));
			}
			if (!RoleUtils.isApplicationRole(role.getName())) {
				validationExceptions.add(new ValidationException(
						"msg.roleNotValidApplicationRole", "role"));
			}
		}
		return validationExceptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.user.mgmt.UserMgmtService#getUser(java.lang.String)
	 */
	public HanaUser getUser(String username) throws HDBWrappedException,
			AuthenticationException {
		Connection con = null;
		try {
			con = UserUtils.getDBConnection();
			String sql = "select * from \"COLM\".\"USER_VIEW\" where USER_NAME = ?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, username.toUpperCase());
			ResultSet rs = stmt.executeQuery();
			HanaUser result = null;
			int count = 0;
			while (rs.next()) {
				if (count < 0)
					break;
				String db_username = rs.getString(1);
				result = new HanaUser(db_username, rs.getString(4),
						rs.getDate(5), rs.getDate(6), rs.getDate(7),
						rs.getDate(10), rs.getInt(8), rs.getBoolean(9),
						rs.getBoolean(11), rs.getBoolean(12), rs.getDate(13));
				count++;
			}
			stmt.close();
			return result;
		} catch (SQLException e) {
			throw new HDBWrappedException(e);
		} finally {
			try {
				LOGGER.info("Close DB connection");
				con.close();
			} catch (SQLException e) {
				LOGGER.error("Cannot close DB Connection", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.user.mgmt.UserMgmtService#getCurrentUser()
	 */
	public HanaUser getCurrentUser() throws HDBWrappedException,
			AuthenticationException {
		Connection con = null;
		try {
			con = UserUtils.getDBConnection();
			String sql = "select * from USERS where USER_NAME = CURRENT_USER";
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			HanaUser result = null;
			int count = 0;
			while (rs.next()) {
				if (count < 0)
					break;
				String db_username = rs.getString(1);
				result = new HanaUser(db_username, rs.getString(4),
						rs.getDate(5), rs.getDate(6), rs.getDate(7),
						rs.getDate(10), rs.getInt(8), rs.getBoolean(9),
						rs.getBoolean(11), rs.getBoolean(12), rs.getDate(13));
				count++;
			}
			stmt.close();
			return result;
		} catch (SQLException e) {
			throw new HDBWrappedException(e);
		} finally {
			try {
				LOGGER.info("Close DB connection");
				con.close();
			} catch (SQLException e) {
				LOGGER.error("Cannot close DB Connection", e);
			}
		}
	}
}
