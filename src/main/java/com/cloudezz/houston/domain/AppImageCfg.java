package com.cloudezz.houston.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class AppImageCfg extends BaseImageCfg implements Cloneable {

  private static final long serialVersionUID = 6647698228363181877L;


  @Id
  @Column(name = "app_name")
  private String appName;

  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_APP_IMAGE_DNS", joinColumns = @JoinColumn(name = "app_img_dns_id"))
  protected List<String> dns = new LinkedList<String>();

  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_APP_IMAGE_PORTS", joinColumns = @JoinColumn(name = "app_img_port_id"))
  protected List<String> ports = new LinkedList<String>();

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "env_name")
  @Column(name = "env_value", nullable = true)
  @CollectionTable(name = "T_APP_ENV_VARIABLE_MAPPING", joinColumns = @JoinColumn(
      name = "env_mapping_id"))
  protected Map<String, String> environmentMapping = new HashMap<String, String>();


  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "host_volume")
  @Column(name = "container_value", nullable = true)
  @CollectionTable(name = "T_APP_VOLUME_MAPPING",
      joinColumns = @JoinColumn(name = "vol_mapping_id"))
  protected Map<String, String> hostToDockerVolumeMapping = new HashMap<String, String>();


  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private Application application;

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
    return this.ports;
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
   * @return the hostToDockervolumeMapping
   */
  public Map<String, String> getHostToDockerVolumeMapping() {
    return hostToDockerVolumeMapping;
  }

  /**
   * @param hostToDockervolumeMapping the hostToDockervolumeMapping to set
   */
  public void setHostToDockerVolumeMapping(Map<String, String> hostToDockervolumeMapping) {
    this.hostToDockerVolumeMapping = hostToDockervolumeMapping;
  }

  /**
   * @param hostToDockervolumeMapping the hostToDockervolumeMapping to set
   */
  public void addHostToDockerVolumeMapping(String hostVolume, String dockerVolume) {
    this.hostToDockerVolumeMapping.put(hostVolume, dockerVolume);
  }

  /**
   * @return the environmentMapping
   */
  public Map<String, String> getEnvironmentMapping() {
    return environmentMapping;
  }

  /**
   * @param environmentMapping the environmentMapping to set
   */
  public void setEnvironmentMapping(Map<String, String> environmentMapping) {
    this.environmentMapping = environmentMapping;
  }

  /**
   * @param environmentMapping the environmentMapping to set
   */
  public void addEnvironmentMapping(String envName, String envValue) {
    this.environmentMapping.put(envName, envValue);
  }

  @JsonIgnore
  public Application getApplication() {
    return application;
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  @Override
  public AppImageCfg clone() {
    AppImageCfg appImageCfg = new AppImageCfg();
    appImageCfg.setApplication(this.application);
    appImageCfg.setAppName(this.appName);
    appImageCfg.setGroupName(this.groupName);
    appImageCfg.setHostName(this.hostName);
    appImageCfg.setCpuShares(this.cpuShares);
    appImageCfg.setImageName(this.imageName);
    appImageCfg.setInitScript(this.initScript);
    appImageCfg.setMemory(this.memory);
    appImageCfg.setMemorySwap(this.memorySwap);
    appImageCfg.setPorts(new ArrayList<String>(this.ports));
    appImageCfg.setTty(this.tty);
    appImageCfg.setDaemon(this.daemon);
    appImageCfg.setDataVolumeFrom(this.dataVolumeFrom);
    appImageCfg.setDns(new ArrayList<String>(this.dns));
    appImageCfg.setDockerHostMachine(this.dockerHostMachine);
    appImageCfg.setEnvironmentMapping(new HashMap<String, String>(this.environmentMapping));
    appImageCfg.setHostToDockerVolumeMapping(new HashMap<String, String>(
        this.hostToDockerVolumeMapping));
    return appImageCfg;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || !(obj instanceof AppImageCfg)) {
      return false;
    }
    return this.appName.equals(((AppImageCfg) obj).appName);
  }

  @Override
  public int hashCode() {
    return this.appName.hashCode();
  }

}
