package com.cloudezz.houston.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class EnvFormElement {

  @XmlValue
  protected String value;
  @XmlAttribute(name = "type")
  protected String type;
  @XmlAttribute(name = "name")
  protected String name;
  @XmlAttribute(name = "display-name")
  protected String displayName;
  @XmlAttribute(name = "optional")
  protected String optional;

  /**
   * Gets the value of the value property.
   * 
   * @return
   *     possible object is
   *     {@link String }
   *     
   */
  public String getValue() {
      return value;
  }

  /**
   * Sets the value of the value property.
   * 
   * @param value
   *     allowed object is
   *     {@link String }
   *     
   */
  public void setValue(String value) {
      this.value = value;
  }

  /**
   * Gets the value of the type property.
   * 
   * @return
   *     possible object is
   *     {@link String }
   *     
   */
  public String getType() {
      return type;
  }

  /**
   * Sets the value of the type property.
   * 
   * @param value
   *     allowed object is
   *     {@link String }
   *     
   */
  public void setType(String value) {
      this.type = value;
  }

  /**
   * Gets the value of the name property.
   * 
   * @return
   *     possible object is
   *     {@link String }
   *     
   */
  public String getName() {
      return name;
  }

  /**
   * Sets the value of the name property.
   * 
   * @param value
   *     allowed object is
   *     {@link String }
   *     
   */
  public void setName(String value) {
      this.name = value;
  }

  /**
   * Gets the value of the displayName property.
   * 
   * @return
   *     possible object is
   *     {@link String }
   *     
   */
  public String getDisplayName() {
      return displayName;
  }

  /**
   * Sets the value of the displayName property.
   * 
   * @param value
   *     allowed object is
   *     {@link String }
   *     
   */
  public void setDisplayName(String value) {
      this.displayName = value;
  }

  /**
   * Gets the value of the optional property.
   * 
   * @return
   *     possible object is
   *     {@link String }
   *     
   */
  public String getOptional() {
      return optional;
  }

  /**
   * Sets the value of the optional property.
   * 
   * @param value
   *     allowed object is
   *     {@link String }
   *     
   */
  public void setOptional(String value) {
      this.optional = value;
  }

}