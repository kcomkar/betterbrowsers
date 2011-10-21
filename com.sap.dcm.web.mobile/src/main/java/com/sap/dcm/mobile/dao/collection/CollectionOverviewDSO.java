package com.sap.dcm.mobile.dao.collection;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionOverviewDSO {

	private String customerId;
	private String customerName;
	private BigDecimal dso;
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
	public BigDecimal getDso() {
		return dso;
	}
	public void setDso(BigDecimal dso) {
		this.dso = dso;
	}
	
	
}
