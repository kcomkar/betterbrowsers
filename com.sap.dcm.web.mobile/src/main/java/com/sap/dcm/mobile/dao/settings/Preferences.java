package com.sap.dcm.mobile.dao.settings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Preferences {
	private String currency = "";
	private String hana = "";
	
	
	public String getHana() {
		return hana;
	}
	public void setHana(String hana) {
		this.hana = hana;
	}
	private List<String> companyCodes = new ArrayList<String>();
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public List<String> getCompanyCodes() {
		return companyCodes;
	}
	public void setCompanyCodes(List<String> companyCodes) {
		this.companyCodes = companyCodes;
	}
	
	
	
}
