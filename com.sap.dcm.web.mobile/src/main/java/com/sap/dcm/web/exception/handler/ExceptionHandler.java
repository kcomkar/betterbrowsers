package com.sap.dcm.web.exception.handler;

import java.util.List;

import com.sap.dcm.web.core.model.ServiceResponseMessage;

/**
 * Interface of an Exception Handler. The Exception Handler can be used to store
 * UI Exceptions to allow multiple errors to be shown on the UI.
 * 
 * @author D049641
 * 
 * @param <T>
 */
public interface ExceptionHandler<T extends Exception> {

	/**
	 * Returns if an error has occurred and is registered in the exception handler
	 * @return true if an error has occured, false if not
	 */
	public abstract boolean hasErrorOccured();

	/**
	 * Adds a new exception to the handler
	 * @param e = the Exception which should be added.
	 */
	public abstract void add(T e);

	/**
	 * Returns all registered errors/exceptions as list.
	 * @return = the list of exceptions
	 */
	public abstract List<T> getExceptions();

	/**
	 * Returns all registered errors/exceptions as an unsorted HTML list. This is can be used to avoid mapping in the ui
	 * @return = html list representation of the exceptions
	 */
	public abstract String getExceptionsAsUnsortedHTMLList();
	
	/**
	 * Returns all registered error messages as a list of @ServiceResponseMessage 
	 * @return
	 */
	public abstract List<ServiceResponseMessage> getExceptionsAsResponseMessages();
}