package com.sap.dcm.mobile;

import java.sql.SQLException;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.security.core.context.SecurityContext;

import com.sap.dcm.mobile.security.GJHanaAuthenticationProvider.GJAuthentication;

/**
 * Application Lifecycle Listener implementation class MobileSessionListener
 *
 */
public class MobileSessionListener implements HttpSessionListener {

    /**
     * Default constructor. 
     */
    public MobileSessionListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent se) {
       System.out.println("--------------new session created!!!------------");
    }

	/**
     * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent se) {
    	System.out.println("------------- session destroyed!!!------------");
    	SecurityContext sc = (SecurityContext)se.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
    	if(sc == null) return;
    	if(sc.getAuthentication() == null) return;
    	if( sc.getAuthentication() instanceof GJAuthentication){
    		GJAuthentication gja = (GJAuthentication)sc.getAuthentication();
    		if(gja.connection != null){
    			try {
					gja.connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	
    }
	
}
