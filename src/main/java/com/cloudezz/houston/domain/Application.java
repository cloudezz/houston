package com.cloudezz.houston.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

@Entity
@Table(name = "T_APPLICATIONS")
public class Application extends BaseEntity {

  private static final long serialVersionUID = 1420676857557056115L;

  @Id
  @Column(name = "app_name")
  private String appName;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "application")
  private List<AppImageCfg> appImageCfgs;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "application")
  private List<ServiceImageCfg> serviceImageCfgs;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "cluster_config")
  protected ClusterConfig clusterConfig;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id", nullable = false)
  private User owner;

  @Column(name = "running", nullable = false, columnDefinition = "TINYINT")
  protected Boolean running = new Boolean(false);

  @Column(name = "start_time")
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
  protected LocalDateTime startTime;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "exposed_service_id", insertable = true, updatable = true, nullable = true,
      unique = true)
  protected List<ExposedService> exposedServices = new ArrayList<ExposedService>();


  @Override
  public String getId() {
    return appName;
  }

  @Override
  public void setId(String id) {
    this.appName = id;
  }


  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public List<ServiceImageCfg> getServiceImageCfgs() {
    return serviceImageCfgs;
  }

  public void setServiceImageCfgs(List<ServiceImageCfg> serviceImageCfgs) {
    this.serviceImageCfgs = serviceImageCfgs;
  }

  /**
   * @return the serviceImages
   */
  public List<ServiceImageCfg> getServiceImages() {
    return serviceImageCfgs;
  }

  /**
   * Add service imgs
   * 
   * @param serviceImage
   * @throws CloudezzDeployException
   */
  public void addServiceImageCfgs(ServiceImageCfg serviceImage) throws CloudezzDeployException {
    Preconditions.checkNotNull(serviceImage, "Service Image cannot be null");
    if (this.serviceImageCfgs == null) {
      this.serviceImageCfgs = new LinkedList<ServiceImageCfg>();
    }
    serviceImage.setApplication(this);
    int instanceNo = this.serviceImageCfgs.size() + 1;
    serviceImage.setInstanceNo(instanceNo);
    serviceImage.setServiceName(appName + "_serv" + instanceNo);
    this.serviceImageCfgs.add(serviceImage);
  }

  /**
   * Set the designated no of copies
   * 
   * @param serviceImage
   * @param noOfInstances no of clone copies
   * @throws CloudezzDeployException
   */
  public void addServiceImageCfgs(ServiceImageCfg serviceImage, Integer noOfInstances)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(serviceImage, "Service Image cannot be null");
    if (this.serviceImageCfgs == null) {
      this.serviceImageCfgs = new LinkedList<ServiceImageCfg>();
    }
    for (int i = 0; i < noOfInstances; i++) {
      serviceImage = serviceImage.clone();
      serviceImage.setApplication(this);
      serviceImage.setInstanceNo(i);
      serviceImage.setServiceName(appName + "_serv" + i);
      this.serviceImageCfgs.add(serviceImage);
    }

  }

  public List<AppImageCfg> getAppImageCfgs() {
    return appImageCfgs;
  }

  public void setAppImageCfgs(List<AppImageCfg> appImageCfgs) {
    this.appImageCfgs = appImageCfgs;
  }

  /**
   * Add app imgs
   * 
   * @param appImageCfg
   * @throws CloudezzDeployException
   */
  public void addAppImageCfgs(AppImageCfg appImageCfg) throws CloudezzDeployException {
    Preconditions.checkNotNull(appImageCfg, "App Image cannot be null");
    if (this.appImageCfgs == null) {
      this.appImageCfgs = new LinkedList<AppImageCfg>();
    }
    appImageCfg.setApplication(this);
    int instanceNo = this.appImageCfgs.size() + 1;
    appImageCfg.setInstanceNo(instanceNo);
    appImageCfg.setAppName(appName + "_app" + instanceNo);
    this.appImageCfgs.add(appImageCfg);
  }

  /**
   * Set the designated no of copies
   * 
   * @param appImageCfg
   * @param noOfInstances no of clone copies
   * @throws CloudezzDeployException
   */
  public void addAppImageCfgs(AppImageCfg appImageCfg, Integer noOfInstances)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(appImageCfg, "App Image cannot be null");
    if (this.appImageCfgs == null) {
      this.appImageCfgs = new LinkedList<AppImageCfg>();
    }
    for (int i = 0; i < noOfInstances; i++) {
      appImageCfg = appImageCfg.clone();
      appImageCfg.setApplication(this);
      appImageCfg.setInstanceNo(i);
      appImageCfg.setAppName(appName + "_app" + i);
      this.appImageCfgs.add(appImageCfg);
    }
  }

  @JsonIgnore
  public ClusterConfig getClusterConfig() {
    return clusterConfig;
  }

  public void setClusterConfig(ClusterConfig clusterConfig) {
    this.clusterConfig = clusterConfig;
  }

  @JsonIgnore
  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Boolean isRunning() {
    return running;
  }

  public void setRunning(Boolean running) {
    this.running = running;
  }

  public List<ExposedService> getExposedServices() {
    return exposedServices;
  }

  public void setExposedServices(List<ExposedService> exposedServices) {
    this.exposedServices = exposedServices;
  }

  public void addExposedServices(ExposedService exposedService) {
    this.exposedServices.add(exposedService);
  }

  @JsonIgnore
  public LocalDateTime getStartTime() {
    return startTime;
  }

  @JsonGetter
  public String getFormattedStartTime() {
    if (startTime == null)
      return null;

    return startTime.toString("yyyy-MM-dd'T'HH:mm:ssZ");
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

}
