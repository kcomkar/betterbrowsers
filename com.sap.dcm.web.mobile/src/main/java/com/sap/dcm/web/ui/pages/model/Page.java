package com.sap.dcm.web.ui.pages.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for page complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="page">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="page" type="{http://www.sap.com/dcm/uiPage}subPage" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="allowedRole" type="{http://www.sap.com/dcm/uiPage}role" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defaultPage" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "page", namespace="http://www.sap.com/dcm/uiPage")
public class Page {
	@XmlElement(namespace="http://www.sap.com/dcm/uiPage")
    protected List<SubPage> page;
	@XmlElement(namespace="http://www.sap.com/dcm/uiPage")
    protected List<Role> allowedRole;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String uiText;
    @XmlAttribute
    protected String defaultPage;

    /**
     * Gets the value of the page property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the page property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubPage }
     * 
     * 
     */
    public List<SubPage> getPage() {
        if (page == null) {
            page = new ArrayList<SubPage>();
        }
        return this.page;
    }

    /**
     * Gets the value of the allowedRole property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedRole property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedRole().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Role }
     * 
     * 
     */
    public List<Role> getAllowedRole() {
        if (allowedRole == null) {
            allowedRole = new ArrayList<Role>();
        }
        return this.allowedRole;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the defaultPage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultPage() {
        return defaultPage;
    }

    /**
     * Sets the value of the defaultPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultPage(String value) {
        this.defaultPage = value;
    }
    

    /**
     * Gets the value of the uiText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUiText() {
        return uiText;
    }
    
    /**
     * Sets the value of the uiText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUiText(String value) {
        this.uiText = value;
    }

}
