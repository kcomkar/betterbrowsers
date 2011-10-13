package com.sap.dcm.web.exception.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sap.dcm.web.core.model.ServiceResponseMessage;
import com.sap.dcm.web.exception.AbstractDCMException;
import com.sap.dcm.web.ui.impl.UITextProvider;

/**
 * Default Exception Handler of Dynamic Cash Management. If provided, the
 * messages will automatically be converted into the provided locale.
 * 
 * @author D049641
 * 
 */
public class BasicDCMExceptionHandler implements
		ExceptionHandler<AbstractDCMException> {
	private List<AbstractDCMException> exceptions;
	private Locale locale;

	/**
	 * Initializes a default BasicDCMExceptionHandler without specific locale.
	 */
	public BasicDCMExceptionHandler() {
		exceptions = new ArrayList<AbstractDCMException>();
	}

	/**
	 * Initializes the basic DCM ExceptionHandler for the given locale.
	 * 
	 * @param locale
	 *            = the locale to be used for messages
	 */
	public BasicDCMExceptionHandler(Locale locale) {
		this();
		this.locale = locale;
	}

	/**
	 * Initializes the basic DCM ExceptionHandler for the given locale.
	 * 
	 * @param locale
	 *            = the locale to be used for messages
	 */
	public BasicDCMExceptionHandler(String locale) {
		this(UITextProvider.getLocale(locale));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.exception.ExceptionHandler#hasErrorOccured()
	 */
	public boolean hasErrorOccured() {
		return (exceptions.size() > 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.exception.ExceptionHandler#add(com.sap.dcm.web.exception
	 * .AbstractDCMException)
	 */
	public void add(AbstractDCMException e) {
		exceptions.add(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.dcm.web.exception.ExceptionHandler#getExceptions()
	 */
	public List<AbstractDCMException> getExceptions() {
		return exceptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.dcm.web.exception.ExceptionHandler#getExceptionsAsUnsortedHTMLList
	 * ()
	 */
	public String getExceptionsAsUnsortedHTMLList() {
		if (hasErrorOccured()) {
			String html = "<ul>";

			for (AbstractDCMException e : exceptions)
				html += "<li>" + e.getLocalizedMessage(locale) + "</li>";

			return html + "</ul>";
		}else{
			try{
				UITextProvider p = new UITextProvider(locale);
				String text = p.getUIText("msg.core.unknownError");
				return "<ul><li>"+text+"</li></ul>";
			}catch(Exception e){
				return "<ul><li>An unknown Error has occured</li></ul>";
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.sap.dcm.web.exception.handler.ExceptionHandler#getExceptionsAsResponseMessages()
	 */
	public List<ServiceResponseMessage> getExceptionsAsResponseMessages() {
		List<ServiceResponseMessage> result = new ArrayList<ServiceResponseMessage>();
		if (hasErrorOccured()) {
			for (AbstractDCMException e : exceptions)
				result.add(new ServiceResponseMessage(e.getLocalizedMessage(), true));
		}else{
			String text = "An unknown Error has occured";
			try{
				UITextProvider p = new UITextProvider(locale);
				text = p.getUIText("msg.core.unknownError");
			}catch(Exception e){
			}
			result.add(new ServiceResponseMessage(text, true));
		}
		return result;
	}
}
