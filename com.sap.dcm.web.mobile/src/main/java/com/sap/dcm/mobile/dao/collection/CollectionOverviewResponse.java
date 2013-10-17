package com.sap.dcm.mobile.dao.collection;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CollectionOverviewResponse {
	private String currentUser = "";
	
	private List<CollectionOverviewDSO> dsoList = new ArrayList<CollectionOverviewDSO>();
	private List<CollectionOverviewOpenAmount> openAmountList = new ArrayList<CollectionOverviewOpenAmount>();
	private List<CollectionOverviewInterestLoss> interestLossList = new ArrayList<CollectionOverviewInterestLoss>();
	public String getCurrentUser() {
		return currentUser;
	}
	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}
	public List<CollectionOverviewDSO> getDsoList() {
		return dsoList;
	}
	public void setDsoList(List<CollectionOverviewDSO> dsoList) {
		this.dsoList = dsoList;
	}
	public List<CollectionOverviewOpenAmount> getOpenAmountList() {
		return openAmountList;
	}
	public void setOpenAmountList(List<CollectionOverviewOpenAmount> openAmountList) {
		this.openAmountList = openAmountList;
	}
	public List<CollectionOverviewInterestLoss> getInterestLossList() {
		return interestLossList;
	}
	public void setInterestLossList(
			List<CollectionOverviewInterestLoss> interestLossList) {
		this.interestLossList = interestLossList;
	}
	
	
	
	
}
