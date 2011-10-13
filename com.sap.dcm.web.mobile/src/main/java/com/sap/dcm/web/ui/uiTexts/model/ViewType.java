package com.sap.dcm.web.ui.uiTexts.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for viewType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="viewType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uiText" type="{http://www.sap.com/dcm/uiTextMapping}uiTextKey" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="control" type="{http://www.sap.com/dcm/uiTextMapping}uiControlType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "viewType", namespace="http://www.sap.com/dcm/uiTextMapping")
public class ViewType {

	@XmlElement(namespace="http://www.sap.com/dcm/uiTextMapping")
    protected List<UiTextKey> uiText;
	@XmlElement(namespace="http://www.sap.com/dcm/uiTextMapping")
    protected List<UiControlType> control;
    @XmlAttribute(required = true)
    protected String id;

    /**
     * Gets the value of the uiText property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uiText property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUiText().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UiTextKey }
     * 
     * 
     */
    public List<UiTextKey> getUiText() {
        if (uiText == null) {
            uiText = new ArrayList<UiTextKey>();
        }
        return this.uiText;
    }

    /**
     * Gets the value of the control property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the control property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getControl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UiControlType }
     * 
     * 
     */
    public List<UiControlType> getControl() {
        if (control == null) {
            control = new ArrayList<UiControlType>();
        }
        return this.control;
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

}
