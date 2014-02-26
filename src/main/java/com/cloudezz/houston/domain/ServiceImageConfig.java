package com.cloudezz.houston.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Service image info object that hold information related to service like mysql or redis and would
 * be always attached to a application image . These images objects represent how the image will be
 * build and run as containers in docker
 * 
 * @author Thanneer
 * 
 */
@Entity
@Table(name = "T_SERVICE_IMAGE_CONFIG")
public class ServiceImageConfig extends BaseImageConfig {

  private static final long serialVersionUID = 1857747836263604938L;

  @Column(name="link_name")
  private String linkName;

  @Id
  @Column(name="service_name")
  private String serviceName;
  
  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_SERVICE_IMAGE_DNS")
  protected List<String> dns = new LinkedList<String>();

  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_SERVICE_IMAGE_PORTS")
  protected List<String> ports = new LinkedList<String>();
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "app_config_id")
  private ApplicationImageConfig applicationImageConfig;

  @Override
  public String getId() {
    return serviceName;
  }

  @Override
  public void setId(String id) {
    serviceName = id;
  }

  /**
   * The name is used to link the service image as child image with parent image . Parent image
   * ----linkname---> child image.
   * 
   * @return the linkName
   */
  public String getLinkName() {
    // if link name is not there then auto generate one
    if (linkName == null || linkName.isEmpty()) {
      if (imageName.contains("/")) {
        linkName = imageName.replace("/", "_") + "_" + new Random().nextInt(99);
      } else {
        linkName = imageName;
      }
    }
    return linkName;
  }

  /**
   * @param linkName the linkName to set
   */
  public void setLinkName(String linkName) {
    this.linkName = linkName;
  }

  /**
   * @return the serviceName
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * @param serviceName the serviceName to set
   */
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public List<String> getDns() {
    return dns;
  }

  public void setDns(List<String> dns) {
    this.dns = dns;
  }

  public List<String> getPorts() {
    return ports;
  }

  public void setPorts(List<String> ports) {
    this.ports = ports;
  }

  
  public String[] getDnsAsArray() {
    return dns.toArray(new String[dns.size()]);
  }


  public String[] getPortsAsArray() {
    return ports.toArray(new String[ports.size()]);
  }

  
  /**
   * @return the applicationImageConfig
   */
  public ApplicationImageConfig getApplicationImageConfig() {
    return applicationImageConfig;
  }

  /**
   * @param applicationImageConfig the applicationImageConfig to set
   */
  public void setApplicationImageConfig(ApplicationImageConfig applicationImageConfig) {
    this.applicationImageConfig = applicationImageConfig;
  }



}
