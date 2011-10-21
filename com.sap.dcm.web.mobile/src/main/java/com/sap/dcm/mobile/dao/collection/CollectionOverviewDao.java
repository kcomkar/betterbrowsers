package com.sap.dcm.mobile.dao.collection;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.dcm.web.exception.db.HDBWrappedException;
import com.sap.dcm.web.exception.security.AuthenticationException;
import com.sap.dcm.web.security.UserUtils;

public class CollectionOverviewDao {
	private static final Logger LOGGER = Logger
			.getLogger(CollectionOverviewDao.class);
	
	public CollectionOverviewResponse getHitList() throws AuthenticationException, HDBWrappedException{
		CollectionOverviewResponse result = new CollectionOverviewResponse();
		Connection connection = UserUtils.getDBConnection();
		/*try{
			Class.forName("com.sap.db.jdbc.Driver");
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}*/
		//Connection connection = DriverManager.getConnection("jdbc:sap://coe-he-10:30815", "SYSTEM", "manager");
		try{
			CallableStatement cs = connection.prepareCall("{CALL COLM.GET_COLLECTION_HIT_LIST(?,?,?)}");
			/*cs.registerOutParameter(1, Types.REF);
			cs.registerOutParameter(2, Types.REF);
			cs.registerOutParameter(3, Types.REF);*/
			cs.execute();
			
			ResultSet rs = cs.getResultSet();//DSO
			
			List<CollectionOverviewDSO> collectionOverviewDSO = wrapDSO(rs);
			List<CollectionOverviewOpenAmount> collectionOverviewOpenAmount = new ArrayList<CollectionOverviewOpenAmount>();
			List<CollectionOverviewInterestLoss> collectionOverviewInterestLoss = new ArrayList<CollectionOverviewInterestLoss>();
			if(cs.getMoreResults()){
				// OpenAmount
				rs = cs.getResultSet();
				collectionOverviewOpenAmount = wrapOpenAmount(rs);
			}
			
			if(cs.getMoreResults()){
				// Interest Loss
				rs = cs.getResultSet();
				collectionOverviewInterestLoss = wrapInterestLoss(rs);
			}
			
			result.setDsoList(collectionOverviewDSO);
			result.setOpenAmountList(collectionOverviewOpenAmount);
			result.setInterestLossList(collectionOverviewInterestLoss);
			
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
	
	public CustomerKPI getCustomerKPI(String customerId) throws AuthenticationException, HDBWrappedException{
		CustomerKPI result = null;
		Connection connection = UserUtils.getDBConnection();
		/*try{
			Class.forName("com.sap.db.jdbc.Driver");
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}*/
		//Connection connection = DriverManager.getConnection("jdbc:sap://coe-he-10:30815", "SYSTEM", "manager");
		try{
			
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.UPDATE_CUSTOMER(?)}");
			cs.setString(1, customerId);
			cs.executeUpdate();
			
			// get kpi
			cs = connection.prepareCall("{CALL \"_SYS_BIC\".\"cflm/CALC_CUSTOMER_KPIS/proc\"(?)}");
			cs.execute();
			ResultSet rs = cs.getResultSet();
			result = wrapCustomerKPI(rs);
			
			
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
	
	public List<InvoiceHeader> getInvoices(String customerId) throws AuthenticationException, HDBWrappedException{
		List<InvoiceHeader> result = new ArrayList<InvoiceHeader>();
		Connection connection = UserUtils.getDBConnection();
		/*try{
			Class.forName("com.sap.db.jdbc.Driver");
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}*/
		//Connection connection = DriverManager.getConnection("jdbc:sap://coe-he-10:30815", "SYSTEM", "manager");
		try{
			
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.UPDATE_CUSTOMER(?)}");
			cs.setString(1, customerId);
			cs.executeUpdate();
			
			cs = connection.prepareCall("{CALL \"_SYS_BIC\".\"cflm/CALC_INVOICE_LIST/proc\"(?)}");
			cs.execute();
			ResultSet rs = cs.getResultSet();
			result = wrapInvoiceHeader(rs);
			
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
	
	public List<Note> getNotes(String customerId) throws AuthenticationException, HDBWrappedException{
		List<Note> result = new ArrayList<Note>();
		Connection connection = UserUtils.getDBConnection();
		
		try{
			
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.UPDATE_CUSTOMER(?)}");
			cs.setString(1, customerId);
			cs.executeUpdate();
			
	
			cs = connection.prepareCall("{CALL COLM.GET_NOTES(?)}");
			cs.execute();
			ResultSet rs = cs.getResultSet();
			result = wrapNotes(rs);
			
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
	
	public Note createNote(String customerId, String contact, String text) throws HDBWrappedException, AuthenticationException{
		Connection connection = UserUtils.getDBConnection();
		
		try{
			
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.UPDATE_CUSTOMER(?)}");
			cs.setString(1, customerId);
			cs.executeUpdate();
			
	
			cs = connection.prepareCall("{CALL COLM.INSERT_NOTE(?,?)}");
			cs.setString(1, contact);
			cs.setString(2, text);
			
			cs.executeUpdate();
			
			return null;
			
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
	
	
	public InvoiceDetail getInvoice(String customerId, String invoiceId) throws AuthenticationException, HDBWrappedException{
		Connection connection = UserUtils.getDBConnection();
		
		try{
			// set parameter
			CallableStatement cs = connection.prepareCall("{CALL COLM.UPDATE_CUSTOMER(?)}");
			cs.setString(1, customerId);
			cs.executeUpdate();
						
			// set parameter
			cs = connection.prepareCall("{CALL COLM.UPDATE_DOCUMENT(?)}");
			cs.setString(1, invoiceId);
			cs.executeUpdate();
			
	
			cs = connection.prepareCall("{CALL \"_SYS_BIC\".\"cflm/CALC_INVOICE_DETAILS/proc\"(?)}");
			cs.execute();
			ResultSet rs = cs.getResultSet();
			return wrapInvoiceDetail(rs);
			
			
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
	
	protected InvoiceDetail wrapInvoiceDetail(ResultSet rs) throws SQLException{
		InvoiceDetail result = new InvoiceDetail();
		while(rs.next()){
			String invoiceNumber = rs.getString("INVOICE_NUMBER");
			result.setInvoiceNumber(invoiceNumber!=null?invoiceNumber:"");
			
			BigDecimal quantity = rs.getBigDecimal("QUANTITY");
			result.setQuantity(quantity!=null?quantity:new BigDecimal(0));
			
			String product = rs.getString("PRODUCT");
			result.setProduct(product!=null?product:"");
			
			String productDescription = rs.getString("PRODUCT_DESCRIPTION");
			result.setProductDescription(productDescription!=null?productDescription:"");
			
			BigDecimal unitPrice = rs.getBigDecimal("UNIT_PRICE");
			result.setUnitPrice(unitPrice!=null?unitPrice:new BigDecimal(0));
			
			BigDecimal amount = rs.getBigDecimal("AMOUNT");
			result.setAmount(amount!=null?amount:new BigDecimal(0));
			
			Date deliveryDate = rs.getDate("DELIVERY_DATE");
			if(deliveryDate== null){
				result.setDeliveryDate("");
			}
			else{
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				result.setDeliveryDate(df.format(deliveryDate));
			}
			
			Date deliveredOn = rs.getDate("DELIVERED_ON");
			if(deliveredOn== null){
				result.setDeliveredOn("");
			}
			else{
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				result.setDeliveredOn(df.format(deliveredOn));
			}
			
			
			return result;
		}
		
		return null;
	}
	protected List<Note> wrapNotes(ResultSet rs) throws SQLException{
		List<Note> result = new ArrayList<Note>();
		while(rs.next()){
			Note note = new Note();
			String contact = rs.getString("CONTACT");
			note.setContact(contact!=null?contact:"");
			
			Timestamp datetime = rs.getTimestamp("ZDATETIME");
			if(datetime == null){
				note.setDatetime("");
			}
			else{
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm");
				note.setDatetime(df.format(datetime));
			}
			
			String text = rs.getString("TEXT");
			note.setText(text!=null?text:"");
			result.add(note);
		}
		
		return result;
	}
	protected CustomerKPI wrapCustomerKPI(ResultSet rs) throws SQLException{
		CustomerKPI result = new CustomerKPI();
		while(rs.next()){
			String customerName = rs.getString("CUSTOMER_NAME");
			result.setCustomerName(customerName!=null?customerName:"");
			
			String customerId = rs.getString("KUNNR");
			result.setCustomerId(customerId!=null?customerId:"");
			
			String address = rs.getString("ADDRESS");
			result.setAddress(address!= null?address:"");
			
			String companyCode = rs.getString("BUKRS");
			result.setCompanyCode(companyCode!=null?companyCode:"");
			
			String companyName = rs.getString("COMPANY_NAME");
			result.setCompanyName(companyName!=null?companyName:"");
			
			
			result.setPaymentBehavior("");
			
			String creditRating = rs.getString("CREDIT_RATING");
			result.setCreditRating(creditRating!=null?creditRating:"");
			
			BigDecimal overdueAmount = rs.getBigDecimal("RCV_AGING_OVERDUE");
			result.setOverdueAmount(overdueAmount!=null?overdueAmount:new BigDecimal(0));
			
			BigDecimal dso = rs.getBigDecimal("CURRENT_DSO");
			result.setDSO(dso!=null?dso:new BigDecimal(0));
			
			BigDecimal openAmount =  rs.getBigDecimal("RCV_AGING_OPEN");
			result.setOpenAmount(openAmount!=null?openAmount:new BigDecimal(0));
			
			BigDecimal interestLoss = rs.getBigDecimal("INTEREST_COST");
			result.setInterestLoss(interestLoss!=null?interestLoss:new BigDecimal(0));
			
			Integer dunningLevel = (Integer)rs.getObject("AVG_LEVEL");
			result.setDunningLevel(dunningLevel!=null?dunningLevel:0);
			
			Date lastDunning = rs.getDate("LAST_DUNNED_DATE");
			if(lastDunning== null){
				result.setLastDunning("");
			}
			else{
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				result.setLastDunning(df.format(lastDunning));
			}
			
			BigDecimal reveneLastYear =  rs.getBigDecimal("REVENUE_LAST_YEAR");
			result.setRevenueLastYear(reveneLastYear!=null?reveneLastYear:new BigDecimal(0));
			
			BigDecimal promisedAmount =  rs.getBigDecimal("PROMISED_AMOUNT");
			result.setPromisedAmount(promisedAmount!=null?promisedAmount:new BigDecimal(0));
			
			BigDecimal brokenPromiseToPay = rs.getBigDecimal("BROKEN_AMOUNT");
			result.setBrokenPromiseToPay(brokenPromiseToPay!=null?brokenPromiseToPay:new BigDecimal(0));
			
			BigDecimal disputedAmount = rs.getBigDecimal("DISPUTED_AMOUNT");
			result.setDisputedAmount(disputedAmount!=null?disputedAmount:new BigDecimal(0));
			
			BigDecimal manualClearingRatio = rs.getBigDecimal("MANUAL_CLEAR_RATIO");
			result.setManualClearingRatio(manualClearingRatio!=null?manualClearingRatio:new BigDecimal(0));
			
			String contact = rs.getString("CONTACT");
			result.setContact(contact!=null?contact:"");
			
			String phone = rs.getString("TELEPHONE");
			result.setPhone(phone!=null?phone:"");
			
			String email = rs.getString("EMAIL");
			result.setEmail(email!=null?email:"");
			
			return result;
		}
		
		return null;
		
		
	}
	
	protected List<InvoiceHeader> wrapInvoiceHeader(ResultSet rs) throws SQLException{
		List<InvoiceHeader> result = new ArrayList<InvoiceHeader>();
		while(rs.next()){
			InvoiceHeader invoiceHeader = new InvoiceHeader();
			invoiceHeader.setDocNumber(rs.getString("BILLING_DOCUMENT_ID"));
			
			Date dueDate = rs.getDate("DUE_DATE");
			if(dueDate== null){
				invoiceHeader.setDueDate("");
			}
			else{
				DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				invoiceHeader.setDueDate(df.format(dueDate));
			}
			
			BigDecimal amount = rs.getBigDecimal("AMOUNT");
			invoiceHeader.setAmount(amount!=null?amount:new BigDecimal(0));
			
			result.add(invoiceHeader);
		}
		
		return result;
	}
	protected List<CollectionOverviewDSO> wrapDSO(ResultSet rs) throws SQLException{
		List<CollectionOverviewDSO> result = new ArrayList<CollectionOverviewDSO>();
		while(rs.next()){
			CollectionOverviewDSO collectionOverviewDSO = new CollectionOverviewDSO();
			collectionOverviewDSO.setCustomerId(rs.getString("CUSTOMER"));
			collectionOverviewDSO.setCustomerName(rs.getString("CUSTOMER_NAME"));
			collectionOverviewDSO.setDso(rs.getBigDecimal("DSO"));
			
			result.add(collectionOverviewDSO);
		}
		
		return result;
	}
	
	protected List<CollectionOverviewOpenAmount> wrapOpenAmount(ResultSet rs) throws SQLException{
		List<CollectionOverviewOpenAmount> result = new ArrayList<CollectionOverviewOpenAmount>();
		while(rs.next()){
			CollectionOverviewOpenAmount collectionOverviewOpenAmount = new CollectionOverviewOpenAmount();
			collectionOverviewOpenAmount.setCustomerId(rs.getString("CUSTOMER"));
			collectionOverviewOpenAmount.setCustomerName(rs.getString("CUSTOMER_NAME"));
			collectionOverviewOpenAmount.setOpenAmount(rs.getBigDecimal("TOTAL_AMOUNT_REP"));
			
			result.add(collectionOverviewOpenAmount);
		}
		
		return result;
	}
	
	protected List<CollectionOverviewInterestLoss> wrapInterestLoss(ResultSet rs) throws SQLException{
		List<CollectionOverviewInterestLoss> result = new ArrayList<CollectionOverviewInterestLoss>();
		while(rs.next()){
			CollectionOverviewInterestLoss CollectionOverviewInterestLoss = new CollectionOverviewInterestLoss();
			CollectionOverviewInterestLoss.setCustomerId(rs.getString("CUSTOMER"));
			CollectionOverviewInterestLoss.setCustomerName(rs.getString("CUSTOMER_NAME"));
			CollectionOverviewInterestLoss.setInterestLoss(rs.getBigDecimal("INTEREST_LOSS_REP"));
			
			result.add(CollectionOverviewInterestLoss);
		}
		
		return result;
	}
	
	public static void main(String args[]) throws AuthenticationException, HDBWrappedException, SQLException{
		CollectionOverviewDao CollectionOverviewDao = new CollectionOverviewDao();
		CollectionOverviewDao.getHitList();
	}
}
