package com.sap.dcm.mobile.service.preferences;

import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sap.dcm.mobile.dao.settings.Preferences;
import com.sap.dcm.mobile.dao.settings.PreferencesDao;
import com.sap.dcm.mobile.dao.settings.PreferencesResponse;

public class PreferencesServiceImpl implements IPreferencesService,ApplicationContextAware{

	private ApplicationContext context;
	
	public Response getPreferences() {
		try{
			PreferencesDao dao = (PreferencesDao)this.getContext().getBean("preferencesDao");
			Preferences preferences = dao.getPreferences();
			
			List<String> currencySearchHelp = dao.getCurrencySearchHelp();
			
			PreferencesResponse response = new PreferencesResponse();
			response.setCurrencySearchHelp(currencySearchHelp);
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


	public Response updatePreferences(String currency, String companyCode) {
		try{
			PreferencesDao dao = (PreferencesDao)this.getContext().getBean("preferencesDao");
			dao.updatePreferences(currency, companyCode);
			
			return Response.ok().build();
			
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	
}
