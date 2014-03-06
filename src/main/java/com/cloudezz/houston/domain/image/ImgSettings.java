package com.cloudezz.houston.domain.image;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
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
 *         &lt;element name="form">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="form-element" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="display-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ports">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="port">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="display-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="default-value" type="{http://www.w3.org/2001/XMLSchema}short" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
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
@XmlType(name = "", propOrder = {"form","ports"})
@XmlRootElement(name = "img-settings")
public class ImgSettings {

  @XmlElement(required = true,name="form")
  protected EnvForm form;

  @XmlElement(required = true)
  @XmlElementWrapper(name = "ports")
  private List<Port> ports;


  public List<Port> getPorts() {
    return ports;
  }

  public void setPorts(List<Port> ports) {
    this.ports = ports;
  }

  /**
   * Gets the value of the form property.
   * 
   * @return possible object is {@link ImgSettings.Form }
   * 
   */
  public EnvForm getForm() {
    return form;
  }

  /**
   * Sets the value of the form property.
   * 
   * @param value allowed object is {@link ImgSettings.Form }
   * 
   */
  public void setForm(EnvForm value) {
    this.form = value;
  }


}
