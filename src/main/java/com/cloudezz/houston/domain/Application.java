package com.cloudezz.houston.domain;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
  protected String appName;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  protected Set<AppImageCfg> appImageCfgs = new LinkedHashSet<AppImageCfg>();

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  protected Set<ServiceImageCfg> serviceImageCfgs = new LinkedHashSet<ServiceImageCfg>();

  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_APP_TAGS", joinColumns = @JoinColumn(name = "app_tags_id"))
  protected List<String> tags = new LinkedList<String>();

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "cluster_config")
  protected ClusterConfig clusterConfig;

  @Column(name = "app_desc", columnDefinition = "VARCHAR(1500)")
  protected String desc = "";
  
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id", nullable = false)
  protected User owner;

  @Column(name = "running", nullable = false, columnDefinition = "TINYINT")
  protected Boolean running = new Boolean(false);

  @Column(name = "start_time")
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
  protected LocalDateTime startTime;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "exposed_service_id")
  protected Set<ExposedService> exposedServices = new LinkedHashSet<ExposedService>();


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

  public Set<ServiceImageCfg> getServiceImageCfgs() {
    return serviceImageCfgs;
  }

  public void setServiceImageCfgs(Set<ServiceImageCfg> serviceImageCfgs) {
    this.serviceImageCfgs = serviceImageCfgs;
  }

  public void addServiceImageCfgs(ServiceImageCfg serviceImageCfg) {
    serviceImageCfg.setHostName(this.getAppName());
    serviceImageCfg.setApplication(this);
    this.serviceImageCfgs.add(serviceImageCfg);
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
    for (int i = 1; i < noOfInstances + 1; i++) {
      serviceImage = serviceImage.clone();
      serviceImage.setApplication(this);
      serviceImage.setInstanceNo(i);
      serviceImage.setServiceName(serviceImage.getServiceName() + "_serv" + i);
      this.serviceImageCfgs.add(serviceImage);
    }

  }

  public Set<AppImageCfg> getAppImageCfgs() {
    return appImageCfgs;
  }

  public void setAppImageCfgs(Set<AppImageCfg> appImageCfgs) {
    this.appImageCfgs = appImageCfgs;
  }

  
  public void addAppImageCfgs(AppImageCfg appImageCfg) {
    appImageCfg.setHostName(this.getAppName());
    appImageCfg.setApplication(this);
    this.appImageCfgs.add(appImageCfg);
  }

  /**
   * Set the designated no of copies
   * 
   * @param appImageCfg
   * @param noOfInstances no of clone copies
   * @param string
   * @throws CloudezzDeployException
   */
  public void addAppImageCfgs(AppImageCfg appImageCfg, Integer noOfInstances, String appName)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(appImageCfg, "App Image cannot be null");
    for (int i = 1; i < noOfInstances + 1; i++) {
      appImageCfg = appImageCfg.clone();
      appImageCfg.setApplication(this);
      appImageCfg.setInstanceNo(i);
      appImageCfg.setAppName(appName + "_app" + (this.appImageCfgs.size() + 1));
      appImageCfg.setGroupName(appName);
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

  public Set<ExposedService> getExposedServices() {
    return exposedServices;
  }

  public void setExposedServices(Set<ExposedService> exposedServices) {
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


  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

}
