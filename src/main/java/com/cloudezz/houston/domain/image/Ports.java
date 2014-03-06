package com.cloudezz.houston.domain.image;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="port">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="display-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="default-value" type="{http://www.w3.org/2001/XMLSchema}short" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"port"})
public class Ports {

  @XmlElement(required = true)
  protected Port port;

  /**
   * Gets the value of the port property.
   * 
   * @return possible object is {@link ImgSettings.Ports.Port }
   * 
   */
  public Port getPort() {
    return port;
  }

  /**
   * Sets the value of the port property.
   * 
   * @param value allowed object is {@link ImgSettings.Ports.Port }
   * 
   */
  public void setPort(Port value) {
    this.port = value;
  }
}
