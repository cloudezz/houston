package com.cloudezz.houston.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

/**
 * App image info object that hold information related to application like tomcat web app or php app
 * or wordpress app and would be having zero or more service images linked to it for DB or cache etc
 * service . These images objects represent how the images will be build and run as containers in
 * docker
 * 
 * @author Thanneer
 * 
 */
@Entity
@Table(name = "T_APP_IMAGE_CONFIG")
public class AppImageCfg extends BaseImageCfg {

  private static final long serialVersionUID = 6647698228363181877L;

  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_APP_IMAGE_DNS", joinColumns = @JoinColumn(name = "app_img_dns_id"))
  protected List<String> dns = new LinkedList<String>();

  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_APP_IMAGE_PORTS", joinColumns = @JoinColumn(name = "app_img_port_id"))
  protected List<String> ports = new LinkedList<String>();

  @Id
  @Column(name = "app_name")
  private String appName;
  
  @Column(name = "init_script",columnDefinition="VARCHAR(6000)")
  private String initScript;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "applicationImageConfig")
  private List<ServiceImageCfg> serviceImages;


  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "exposed_service_id", insertable = true, updatable = true, nullable = true,
      unique = true)
  protected ExposedService exposedService;
  
  @JsonIgnore 
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id", nullable = false)
  private User owner;
  

  @Override
  public String getId() {
    return appName;
  }

  @Override
  public void setId(String id) {
    appName = id;
  }

  /**
   * @return the appName
   */
  public String getAppName() {
    return appName;
  }

  /**
   * @param appName the appName to set
   */
  public void setAppName(String appName) {
    this.appName = appName;
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


  public ExposedService getExposedService() {
    return exposedService;
  }

  public void setExposedService(ExposedService exposedService) {
    this.exposedService = exposedService;
  }

  /**
   * @return the serviceImages
   */
  public List<ServiceImageCfg> getServiceImages() {
    return serviceImages;
  }

  /**
   * @param serviceImages the serviceImages to set
   * @throws CloudezzDeployException
   */
  public void addServiceImages(ServiceImageCfg serviceImage) throws CloudezzDeployException {
    Preconditions.checkNotNull(serviceImage, "Service Image cannot be null");
    if (serviceImages == null) {
      serviceImages = new LinkedList<ServiceImageCfg>();
    }
    this.serviceImages.add(serviceImage);
  }

  /**
   * To verify if the service machines and app machine have same port numbers if so then there might
   * be a clash or overlap on port that might cause issues when linking the images. The entire lit
   * of machines exposed port should be unique
   * 
   * @param serviceImage
   * @return
   * @throws CloudezzDeployException
   */
  public boolean hasPortOverLapIssue() throws CloudezzDeployException {

    if (ports == null)
      throw new CloudezzDeployException("The App instance ports to be exposed are not set");

    List<String> allExposedPorts = new ArrayList<String>();
    allExposedPorts.addAll(this.ports);
    for (Iterator<ServiceImageCfg> iterator = serviceImages.iterator(); iterator.hasNext();) {
      BaseImageCfg baseCloudezzImageConfig = iterator.next();
      String[] servicePorts = baseCloudezzImageConfig.getPortsAsArray();
      if (servicePorts != null) {
        for (int i = 0; i < servicePorts.length; i++) {
          if (allExposedPorts.contains(servicePorts[i])) {
            return true;
          } else {
            allExposedPorts.add(servicePorts[i]);
          }
        }
      }
    }
    return false;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public String getInitScript() {
    return initScript;
  }

  public void setInitScript(String initScript) {
    this.initScript = initScript;
  }



}
