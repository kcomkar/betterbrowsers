package com.sap.dcm.mobile.dao.collection;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerKPI {

	//cUSTOMER iNFO
	private String customerName;
	private String customerId;
	private String address;
	private String companyCode; //BUKRS
	private String companyName;
	private String paymentBehavior;
	private String creditRating;//CREDIT_RATING
	
	//AR
	private BigDecimal overdueAmount; // RCV_AGING_OVERDUE
	private BigDecimal DSO;// CURRENT_DSO
	private BigDecimal openAmount; //RCV_AGING_OPEN
	private BigDecimal InterestLoss;//INTEREST_COST
	private int dunningLevel;//AVG_LEVEL
	private String lastDunning;// LAST_DUNNED_DATE
	
	//FI
	private BigDecimal revenueLastYear; //REVENUE_LAST_YEAR
	private BigDecimal promisedAmount; //PROMISED_AMOUNT
	private BigDecimal brokenPromiseToPay;//BROKEN_AMOUNT
	private BigDecimal disputedAmount;//DISPUTED_AMOUNT
	private BigDecimal manualClearingRatio;//MANUAL_CLEAR_RATIO
	
	//Contact Info
	
	private String contact; //CONTACT
	private String phone;//TELEPHONE
	private String email; //EMAIL
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getPaymentBehavior() {
		return paymentBehavior;
	}
	public void setPaymentBehavior(String paymentBehavior) {
		this.paymentBehavior = paymentBehavior;
	}
	public String getCreditRating() {
		return creditRating;
	}
	public void setCreditRating(String creditRating) {
		this.creditRating = creditRating;
	}
	
	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}
	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}
	public BigDecimal getDSO() {
		return DSO;
	}
	public void setDSO(BigDecimal dSO) {
		DSO = dSO;
	}
	public BigDecimal getOpenAmount() {
		return openAmount;
	}
	public void setOpenAmount(BigDecimal openAmount) {
		this.openAmount = openAmount;
	}
	public BigDecimal getInterestLoss() {
		return InterestLoss;
	}
	public void setInterestLoss(BigDecimal interestLoss) {
		InterestLoss = interestLoss;
	}
	public int getDunningLevel() {
		return dunningLevel;
	}
	public void setDunningLevel(int dunningLevel) {
		this.dunningLevel = dunningLevel;
	}
	
	public String getLastDunning() {
		return lastDunning;
	}
	public void setLastDunning(String lastDunning) {
		this.lastDunning = lastDunning;
	}
	public BigDecimal getRevenueLastYear() {
		return revenueLastYear;
	}
	public void setRevenueLastYear(BigDecimal revenueLastYear) {
		this.revenueLastYear = revenueLastYear;
	}
	public BigDecimal getPromisedAmount() {
		return promisedAmount;
	}
	public void setPromisedAmount(BigDecimal promisedAmount) {
		this.promisedAmount = promisedAmount;
	}
	public BigDecimal getBrokenPromiseToPay() {
		return brokenPromiseToPay;
	}
	public void setBrokenPromiseToPay(BigDecimal brokenPromiseToPay) {
		this.brokenPromiseToPay = brokenPromiseToPay;
	}
	public BigDecimal getDisputedAmount() {
		return disputedAmount;
	}
	public void setDisputedAmount(BigDecimal disputedAmount) {
		this.disputedAmount = disputedAmount;
	}
	public BigDecimal getManualClearingRatio() {
		return manualClearingRatio;
	}
	public void setManualClearingRatio(BigDecimal manualClearingRatio) {
		this.manualClearingRatio = manualClearingRatio;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
}
