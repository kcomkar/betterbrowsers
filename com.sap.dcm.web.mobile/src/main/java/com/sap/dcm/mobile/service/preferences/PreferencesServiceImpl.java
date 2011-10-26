package com.sap.dcm.mobile.service.preferences;

import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sap.dcm.mobile.dao.settings.CompanyCodeKV;
import com.sap.dcm.mobile.dao.settings.Preferences;
import com.sap.dcm.mobile.dao.settings.PreferencesDao;
import com.sap.dcm.mobile.dao.settings.PreferencesResponse;

public class PreferencesServiceImpl implements IPreferencesService,ApplicationContextAware{

	private ApplicationContext context;
	
	public Response getPreferences() {
		try{
			PreferencesDao dao = (PreferencesDao)this.getContext().getBean("preferencesDao");
			List<CompanyCodeKV> companyCodeSearchHelp = dao.getCompanyCodeSearchHelp();
			List<String> currencySearchHelp = dao.getCurrencySearchHelp();
			String currency = dao.getReportingCurrency();
			List<String> companyCodes = dao.getCompanyRange();
			
			Preferences preferences = new Preferences();
			preferences.setCompanyCodes(companyCodes);
			preferences.setCurrency(currency);
			
			PreferencesResponse response = new PreferencesResponse();
			response.setCurrencySearchHelp(currencySearchHelp);
			response.setCompanyCodeSearchHelp(companyCodeSearchHelp);
			response.setPreferences(preferences);
			
			return Response.ok().entity(response).build();
			
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

	public ApplicationContext getContext() {
		return context;
	}


	public Response updatePreferences(String currency, List<String> companyCodes) {
		try{
			
			for(String s:companyCodes){
				System.out.println(s);
			}
			PreferencesDao dao = (PreferencesDao)this.getContext().getBean("preferencesDao");
			dao.updatePreferences(currency, companyCodes);
			
			return Response.ok().build();
			
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	
}
