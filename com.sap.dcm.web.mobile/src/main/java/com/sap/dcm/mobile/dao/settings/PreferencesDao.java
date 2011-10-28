package com.sap.dcm.mobile.dao.settings;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sap.dcm.mobile.security.GJUserUtils;
import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.security.AuthenticationException;


public class PreferencesDao {

	/*public Preferences updatePreferences(String currency, String companyCode) throws AuthenticationException, HDBWrappedException{
		Connection connection = UserUtils.getDBConnection();
		Preferences result = new Preferences();
		try{
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.UPDATE_PREFERENCE(?,?)}");
			cs.setString(1, currency);
			cs.setString(2, companyCode);
			cs.executeUpdate();
						
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
	}*/
	public Preferences updatePreferences(String currency, List<String> companyCodes) throws AuthenticationException, HDBWrappedException{
		Connection connection = GJUserUtils.getDBConnection();
		System.out.println("Connection:" + connection);
		/*try{
		Class.forName("com.sap.db.jdbc.Driver");
	}
	catch(Exception e){
		e.printStackTrace();
		throw new RuntimeException(e);
	}*/
	//Connection connection = DriverManager.getConnection("jdbc:sap://coe-he-10:30815", "SYSTEM", "manager");
		
		Preferences result = new Preferences();
		try{
			// create 
			Statement statement = connection.createStatement();
			String sqlLocalTable = "CREATE LOCAL TEMPORARY COLUMN TABLE #TMP_COMPANY_IN ( COMPANY NVARCHAR(4) )";
			try{
				statement.execute(sqlLocalTable);
			}
			catch(SQLException e){
				e.printStackTrace();
			}
			
			// insert data
			String insertToTmpSQL = "INSERT INTO #TMP_COMPANY_IN VALUES(?)";
			PreparedStatement ps = connection.prepareStatement(insertToTmpSQL);
			for(String companyCode : companyCodes){
				ps.setString(1, companyCode);
				ps.addBatch();
			}
			ps.executeBatch();
			
			
			// set parameter
			String schemaName = GJUserUtils.getPrincipal().getUsername().toUpperCase().trim();
			String tmpTableFullName = schemaName +"."+"#TMP_COMPANY_IN";
			CallableStatement cs = connection.prepareCall("{CALL COLM.UPDATE_PREFERENCE(?,"+tmpTableFullName+")}");
			cs.setString(1, currency);
			/*//Struct struct = connection.createStruct("COLM.COMPANY_IN", new String[]{"COMPANY"});
			
			Array array =connection.createArrayOf("COLM.COMPANY_IN", new String[]{"US01","US02"});
		
			cs.setArray(2, array);*/
			
			cs.execute();
			
						
			return result;
			
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new HDBWrappedException(e);
		}
		finally{
			/*try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	public static void main(String args[]) throws AuthenticationException, HDBWrappedException, SQLException{
		PreferencesDao PreferencesDao = new PreferencesDao();
		List<String> companyCodes = new ArrayList<String>();
		companyCodes.add("US01");
		companyCodes.add("US02");
		PreferencesDao.updatePreferences("USD", companyCodes);
	}
	
	/*public Preferences getPreferences() throws AuthenticationException, HDBWrappedException{
		Connection connection = UserUtils.getDBConnection();
		Preferences result = new Preferences();
		try{
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.GET_PREFERENCE(?)}");
			cs.execute();
						
			result = wrapPreferences(cs.getResultSet());
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
	}*/
	
	public List<String> getCompanyRange() throws AuthenticationException, HDBWrappedException{
		Connection connection = GJUserUtils.getDBConnection();
		System.out.println("Connection:" + connection);
		List<String> result = new ArrayList<String>();
		try{
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.GET_COMPANY_RANGE(?)}");
			cs.execute();
						
			result = wrapCompanyCodes(cs.getResultSet());
			return result;
			
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new HDBWrappedException(e);
		}
		finally{
			/*try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	public String getReportingCurrency() throws AuthenticationException, HDBWrappedException{
		Connection connection = GJUserUtils.getDBConnection();
		System.out.println("Connection:" + connection);
		String result = "";
		try{
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.GET_CURRENCY(?)}");
			cs.execute();
			result = cs.getString(1);
			
			//result = wrapReportingCurrency(cs.getResultSet());
			return result!=null?result:"";
			
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new HDBWrappedException(e);
		}
		finally{
			/*try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	/*protected String wrapReportingCurrency(ResultSet rs) throws SQLException{
		String result = "";
		while(rs.next()){
			result = rs.getString(1);
			break;
		}
		return result;
	}*/
	
	public List<String> getCurrencySearchHelp() throws HDBWrappedException, AuthenticationException{
		Connection connection = GJUserUtils.getDBConnection();
		System.out.println("Connection:" + connection);
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
			/*try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	public List<CompanyCodeKV> getCompanyCodeSearchHelp() throws HDBWrappedException, AuthenticationException{
		Connection connection = GJUserUtils.getDBConnection();
		System.out.println("Connection:" + connection);
		List<CompanyCodeKV> result = new ArrayList<CompanyCodeKV>();
		try{
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.GET_COMPANY_LIST()}");
			cs.execute();
						
			result = wrapCompanyCodeSeachHelp(cs.getResultSet());
			return result;
			
		}
		catch(SQLException e){
			e.printStackTrace();
			throw new HDBWrappedException(e);
		}
		finally{
			/*try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}*/
		}
	}
	
	protected List<String> wrapCompanyCodes(ResultSet rs) throws SQLException{
		List<String> result = new ArrayList<String>();
		while(rs.next()){
			String companyCode = rs.getString(1);
			result.add(companyCode);
		}
		return result;
	}
	
	protected List<String> wrapCurrencySeachHelp(ResultSet rs) throws SQLException{
		List<String> result = new ArrayList<String>();
		while(rs.next()){
			String currency = rs.getString(1);
			result.add(currency);
		}
		return result;
	}
	
	protected List<CompanyCodeKV> wrapCompanyCodeSeachHelp(ResultSet rs) throws SQLException{
		List<CompanyCodeKV> result = new ArrayList<CompanyCodeKV>();
		while(rs.next()){
			CompanyCodeKV companyCodeKV = new CompanyCodeKV();
			String companyCode = rs.getString("COMPANY_CODE");
			companyCodeKV.setCompanyCode(companyCode!=null?companyCode:"");
			
			String companyName = rs.getString("COMPANY_TEXT");
			companyCodeKV.setCompanyName(companyName!=null?companyName:"");
			
			result.add(companyCodeKV);
		}
		return result;
	}
	
	/*protected Preferences wrapPreferences(ResultSet rs) throws SQLException{
		Preferences result = new Preferences();
		while(rs.next()){
			String currency = rs.getString("CURR");
			result.setCurrency(currency!=null?currency:"");
			
			String companyCode = rs.getString("COMPANY");
			result.setCompanyCode(companyCode!=null?companyCode:"");
			
			
		}
		return result;
	}*/
	

}
