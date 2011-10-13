package com.sap.dcm.web.ui.pages.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="page" type="{http://www.sap.com/dcm/uiPage}page" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="defaultPage" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "pages", namespace = "http://www.sap.com/dcm/uiPage")
public class Pages {

	@XmlElement(required = true, namespace = "http://www.sap.com/dcm/uiPage")
	protected List<Page> page;
	@XmlAttribute
	protected String defaultPage;

	/**
	 * Gets the value of the page property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the page property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getPage().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Page }
	 * 
	 * 
	 */
	public List<Page> getPage() {
		if (page == null) {
			page = new ArrayList<Page>();
		}
		return this.page;
	}

	/**
	 * Gets the value of the defaultPage property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDefaultPage() {
		return defaultPage;
	}

	/**
	 * Sets the value of the defaultPage property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDefaultPage(String value) {
		this.defaultPage = value;
	}

}
