package com.cloudezz.houston.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
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
public class ApplicationImageConfig extends BaseImageConfig {

  private static final long serialVersionUID = 6647698228363181877L;

  private String gitURL;

  @Id
  @Column(name="app_name")
  private String appName;
  
  @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="applicationImageConfig")
  private List<ServiceImageConfig> serviceImages = new LinkedList<ServiceImageConfig>();


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

  /**
   * @return the gitURL
   */
  public String getGitURL() {
    return gitURL;
  }

  /**
   * @param gitURL the gitURL to set
   */
  public void setGitURL(String gitURL) {
    this.gitURL = gitURL;
  }

  /**
   * @return the serviceImages
   */
  public List<ServiceImageConfig> getServiceImages() {
    return serviceImages;
  }

  /**
   * @param serviceImages the serviceImages to set
   * @throws CloudezzDeployException
   */
  public void addServiceImages(ServiceImageConfig serviceImage) throws CloudezzDeployException {
    Preconditions.checkNotNull(serviceImage, "Service Image cannot be null");
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
    for (Iterator<ServiceImageConfig> iterator = serviceImages.iterator(); iterator.hasNext();) {
      BaseImageConfig baseCloudezzImageConfig = iterator.next();
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




}
