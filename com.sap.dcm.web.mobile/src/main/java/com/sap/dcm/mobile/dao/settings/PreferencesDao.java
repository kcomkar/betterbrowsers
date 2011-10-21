package com.sap.dcm.mobile.dao.settings;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dcm.web.security.UserUtils;

public class PreferencesDao {

	public Preferences getPreferences() throws AuthenticationException, HDBWrappedException{
		Connection connection = UserUtils.getDBConnection();
		Preferences result = new Preferences();
		try{
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.GET_CURR_LIST(?)}");
			cs.execute();
						
			//result = wrapPreferences(cs.getResultSet());
			return result;
			
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new HDBWrappedException(e);
		}
		finally{
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public List<String> getCurrencySearchHelp() throws HDBWrappedException, AuthenticationException{
		Connection connection = UserUtils.getDBConnection();
		List<String> result = new ArrayList<String>();
		try{
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.GET_CURR_LIST(?)}");
			cs.execute();
						
			result = wrapCurrencySeachHelp(cs.getResultSet());
			return result;
			
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new HDBWrappedException(e);
		}
		finally{
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected List<String> wrapCurrencySeachHelp(ResultSet rs) throws SQLException{
		List<String> result = new ArrayList<String>();
		while(rs.next()){
			String currency = rs.getString(1);
			result.add(currency);
		}
		return result;
	}

}
