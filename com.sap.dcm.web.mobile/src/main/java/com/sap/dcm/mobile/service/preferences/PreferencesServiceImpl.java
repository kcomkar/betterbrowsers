package com.sap.dcm.mobile.service.preferences;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sap.db.jdbcext.DataSourceSAP;
import com.sap.dcm.mobile.dao.settings.CompanyCodeKV;
import com.sap.dcm.mobile.dao.settings.Preferences;
import com.sap.dcm.mobile.dao.settings.PreferencesDao;
import com.sap.dcm.mobile.dao.settings.PreferencesResponse;

public class PreferencesServiceImpl implements IPreferencesService,ApplicationContextAware{

	private ApplicationContext context;
	
	@Context
	private HttpServletRequest request;
	
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	
	public Response getPreferences() {
		try{
			System.out.println("------------- print session attribute!!!------------");
	    	for(String name : this.getRequest().getSession().getValueNames()){
	    		System.out.println(name);
	    		System.out.println(this.getRequest().getSession().getAttribute(name).getClass());
	    	}
			
			
			PreferencesDao dao = (PreferencesDao)this.getContext().getBean("preferencesDao");
			long begin = System.currentTimeMillis();
			
			List<CompanyCodeKV> companyCodeSearchHelp = dao.getCompanyCodeSearchHelp();
			List<String> currencySearchHelp = dao.getCurrencySearchHelp();
			String currency = dao.getReportingCurrency();
			List<String> companyCodes = dao.getCompanyRange();
			
			Preferences preferences = new Preferences();
			preferences.setCompanyCodes(companyCodes);
			preferences.setCurrency(currency);
			
			DataSourceSAP dataSource = (DataSourceSAP)context.getBean("dataSource");
			String hana = dataSource.getURL();
			preferences.setHana(hana);
			
			PreferencesResponse response = new PreferencesResponse();
			response.setCurrencySearchHelp(currencySearchHelp);
			response.setCompanyCodeSearchHelp(companyCodeSearchHelp);
			response.setPreferences(preferences);
			
			System.out.println(System.currentTimeMillis() - begin);
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
			
			long begin = System.currentTimeMillis();
			
			dao.updatePreferences(currency, companyCodes);
			System.out.println(System.currentTimeMillis() - begin);
			return Response.ok().build();
			
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	
}
