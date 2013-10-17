package com.sap.dcm.mobile.dao.settings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PreferencesResponse {

	private Preferences preferences = new Preferences();
	
	private List<String> currencySearchHelp = new ArrayList<String>();
	public List<String> getCurrencySearchHelp() {
		return currencySearchHelp;
	}
	public void setCurrencySearchHelp(List<String> currencySearchHelp) {
		this.currencySearchHelp = currencySearchHelp;
	}
	public Preferences getPreferences() {
		return preferences;
	}
	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}
	
	private List<CompanyCodeKV> companyCodeSearchHelp = new ArrayList<CompanyCodeKV>();
	public List<CompanyCodeKV> getCompanyCodeSearchHelp() {
		return companyCodeSearchHelp;
	}
	public void setCompanyCodeSearchHelp(List<CompanyCodeKV> companyCodeSearchHelp) {
		this.companyCodeSearchHelp = companyCodeSearchHelp;
	}
	
	
}
