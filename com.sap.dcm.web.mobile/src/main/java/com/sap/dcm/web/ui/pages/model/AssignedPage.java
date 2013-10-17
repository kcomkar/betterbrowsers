package com.sap.dcm.web.ui.pages.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Representation of a UI Page which is assigned to a certain user
 * @author D049641
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AssignedPage {

	/**
	 * The id of the ui page
	 */
	@XmlElement(required = true)
	private String id;
	
	/**
	 * The ui text of the ui page
	 */
	@XmlElement
	private String uiText;
	
	/**
	 * indicator if the given page is the default page
	 */
	@XmlElement
	private boolean defaultPage;

	/**
	 * List of sub pages
	 */
	@XmlElement
	private Collection<AssignedPage> subPages;

	public AssignedPage() {
		super();
	}

	public AssignedPage(String id, String uiText, boolean defaultPage) {
		super();
		this.id = id;
		this.uiText = uiText;
		this.defaultPage = defaultPage;
	}

	public AssignedPage(String id, String uiText, boolean defaultPage,
			List<AssignedPage> subPages) {
		this(id, uiText, defaultPage);
		this.subPages = subPages;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUiText() {
		return uiText;
	}

	public void setUiText(String uiText) {
		this.uiText = uiText;
	}

	public boolean isDefaultPage() {
		return defaultPage;
	}

	public void setDefaultPage(boolean defaultPage) {
		this.defaultPage = defaultPage;
	}

	public Collection<AssignedPage> getSubPages() {
		return subPages;
	}

	public void setSubPages(List<AssignedPage> subPages) {
		this.subPages = subPages;
	}

	public void addSubPAge(AssignedPage subPage) {
		if (subPages == null)
			subPages = new ArrayList<AssignedPage>();
		subPages.add(subPage);
	}
}
