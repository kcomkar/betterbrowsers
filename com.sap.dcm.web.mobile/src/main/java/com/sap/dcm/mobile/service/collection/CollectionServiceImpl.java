package com.sap.dcm.mobile.service.collection;

import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sap.dcm.mobile.dao.collection.CollectionDetailResponse;
import com.sap.dcm.mobile.dao.collection.CollectionOverviewDao;
import com.sap.dcm.mobile.dao.collection.CollectionOverviewResponse;
import com.sap.dcm.mobile.dao.collection.CustomerKPI;
import com.sap.dcm.mobile.dao.collection.InvoiceDetail;
import com.sap.dcm.mobile.dao.collection.InvoiceDetailResponse;
import com.sap.dcm.mobile.dao.collection.InvoiceHeader;
import com.sap.dcm.mobile.dao.collection.Note;
import com.sap.dcm.mobile.dao.collection.NoteListResponse;

public class CollectionServiceImpl implements ICollectionService,ApplicationContextAware{

	private ApplicationContext context;

	public ApplicationContext getContext() {
		return context;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

	public Response getOverview() {
		try{
			CollectionOverviewDao dao = (CollectionOverviewDao)this.getContext().getBean("collectionOverviewDao");
			long begin = System.currentTimeMillis();
			
			CollectionOverviewResponse response = dao.getHitList();
			
			System.out.println(System.currentTimeMillis() - begin);
			return Response.ok().entity(response).build();
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
		
	}
	
	public Response getCustomerKPI(String companyCode, String customerId) {
		try{
			CollectionOverviewDao dao = (CollectionOverviewDao)this.getContext().getBean("collectionOverviewDao");
			long begin = System.currentTimeMillis();
			CustomerKPI customerKPI = dao.getCustomerKPI(companyCode,customerId);
			List<InvoiceHeader> invoiceHeaders = dao.getInvoices(companyCode,customerId);
			//List<Note> notes = dao.getNotes(customerId);
			
			CollectionDetailResponse response = new CollectionDetailResponse();
			response.setCustomerKPI(customerKPI);
			response.setInvoiceHeaders(invoiceHeaders);
			//response.setNotes(notes);
			
			System.out.println(System.currentTimeMillis() - begin);
			
			if(customerKPI == null){
				return Response.noContent().build();
			}
			else{
				return Response.ok().entity(response).build();
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	public Response createNote(String companyCode,String customerId, String contact, String text) {
		CollectionOverviewDao dao = (CollectionOverviewDao)this.getContext().getBean("collectionOverviewDao");
		try{
			long begin = System.currentTimeMillis();
			dao.createNote(companyCode,customerId, contact, text);
			System.out.println(System.currentTimeMillis() - begin);
			return Response.ok().build();
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	public Response getNotes(String companyCode,String customerId) {
		CollectionOverviewDao dao = (CollectionOverviewDao)this.getContext().getBean("collectionOverviewDao");
		try{
			long begin = System.currentTimeMillis();
			List<Note> notes = dao.getNotes(companyCode,customerId);
			NoteListResponse result = new NoteListResponse();
			result.setNotes(notes);
			result.setCustomerId(customerId);
			
			System.out.println(System.currentTimeMillis() - begin);
			
			return Response.ok().entity(result).build();
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	public Response getInvoice(String companyCode,String customerId, String invoiceId) {
		try{
			CollectionOverviewDao dao = (CollectionOverviewDao)this.getContext().getBean("collectionOverviewDao");
			long begin = System.currentTimeMillis();
			InvoiceDetail invoiceDetail = dao.getInvoice(companyCode,customerId,invoiceId);
			
			InvoiceDetailResponse response = new InvoiceDetailResponse();
			response.setInvoiceDetail(invoiceDetail);
			
			System.out.println(System.currentTimeMillis() - begin);
			if(invoiceDetail == null){
				return Response.noContent().build();
			}
			else{
				return Response.ok().entity(response).build();
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

	
	
}
