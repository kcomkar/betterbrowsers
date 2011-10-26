package com.sap.dcm.mobile.dao.collection;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionOverviewInterestLoss {
	private String customerId;
	private String customerName;
	private BigDecimal interestLoss;
	private String companyCode;
	private String companyName;
	
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
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public BigDecimal getInterestLoss() {
		return interestLoss;
	}
	public void setInterestLoss(BigDecimal interestLoss) {
		this.interestLoss = interestLoss;
	}
	
	
}
