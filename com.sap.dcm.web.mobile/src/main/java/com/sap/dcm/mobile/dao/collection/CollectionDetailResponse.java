package com.sap.dcm.mobile.dao.collection;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionDetailResponse {

	private CustomerKPI customerKPI;
	private List<InvoiceHeader> invoiceHeaders = new ArrayList<InvoiceHeader>();
	//private List<Note> notes = new ArrayList<Note>();
	
	public CustomerKPI getCustomerKPI() {
		return customerKPI;
	}
	public void setCustomerKPI(CustomerKPI customerKPI) {
		this.customerKPI = customerKPI;
	}
	public List<InvoiceHeader> getInvoiceHeaders() {
		return invoiceHeaders;
	}
	public void setInvoiceHeaders(List<InvoiceHeader> invoiceHeaders) {
		this.invoiceHeaders = invoiceHeaders;
	}
	/*public List<Note> getNotes() {
		return notes;
	}
	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}*/
	
	
}
