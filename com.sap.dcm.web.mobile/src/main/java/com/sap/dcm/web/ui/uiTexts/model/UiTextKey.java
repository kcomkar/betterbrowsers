package com.sap.dcm.web.ui.uiTexts.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uiTextKey complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uiTextKey">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="textKey" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uiTextKey", namespace="http://www.sap.com/dcm/uiTextMapping")
public class UiTextKey {

    @XmlAttribute(required = true)
    protected String textKey;
 
    /**
     * Gets the value of the textKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextKey() {
        return textKey;
    }

    /**
     * Sets the value of the textKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextKey(String value) {
        this.textKey = value;
    }

}
