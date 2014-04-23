package com.cloudezz.houston.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
 * Service image info object that hold information related to service like mysql or redis and would
 * be always attached to a application image . These images objects represent how the image will be
 * build and run as containers in docker
 * 
 * @author Thanneer
 * 
 */
@Entity
@Table(name = "T_SERVICE_IMAGE_CONFIG")
public class ServiceImageCfg extends BaseImageCfg implements Cloneable {

  private static final long serialVersionUID = 1857747836263604938L;

  @Column(name = "link_name")
  private String linkName;

  @Id
  @Column(name = "service_name")
  private String serviceName;

  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_SERVICE_IMAGE_DNS", joinColumns = @JoinColumn(
      name = "service_img_dns_id"))
  protected List<String> dns = new LinkedList<String>();

  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "T_SERVICE_IMAGE_PORTS", joinColumns = @JoinColumn(
      name = "service_img_port_id"))
  protected List<String> ports = new LinkedList<String>();

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "env_name")
  @Column(name = "env_value", nullable = true)
  @CollectionTable(name = "T_SERVICE_ENV_VARIABLE_MAPPING", joinColumns = @JoinColumn(
      name = "env_mapping_id"))
  protected Map<String, String> environmentMapping = new HashMap<String, String>();


  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "host_volume")
  @Column(name = "container_value", nullable = true)
  @CollectionTable(name = "T_SERVICE_VOLUME_MAPPING", joinColumns = @JoinColumn(
      name = "vol_mapping_id"))
  protected Map<String, String> hostToDockerVolumeMapping = new HashMap<String, String>();


  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  private Application app;


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
    return app;
  }

  public void setApplication(Application application) {
    this.app = application;
  }

  @Override
  public ServiceImageCfg clone() {
    ServiceImageCfg serviceImgCfg = new ServiceImageCfg();
    serviceImgCfg.setApplication(this.app);
    serviceImgCfg.setServiceName(this.serviceName);  
    serviceImgCfg.setGroupName(this.groupName);
    serviceImgCfg.setHostName(this.hostName);
    serviceImgCfg.setCpuShares(this.cpuShares);
    serviceImgCfg.setImageName(this.imageName);
    serviceImgCfg.setInitScript(this.initScript);
    serviceImgCfg.setMemory(this.memory);
    serviceImgCfg.setMemorySwap(this.memorySwap);
    serviceImgCfg.setPorts(new ArrayList<String>(this.ports));
    serviceImgCfg.setTty(this.tty);
    serviceImgCfg.setDaemon(this.daemon);
    serviceImgCfg.setDataVolumeFrom(this.dataVolumeFrom);
    serviceImgCfg.setDns(new ArrayList<String>(this.dns));
    serviceImgCfg.setDockerHostMachine(this.dockerHostMachine);
    serviceImgCfg.setEnvironmentMapping(new HashMap<String, String>(this.environmentMapping));
    serviceImgCfg.setHostToDockerVolumeMapping(new HashMap<String, String>(
        this.hostToDockerVolumeMapping));
    return serviceImgCfg;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || !(obj instanceof ServiceImageCfg)) {
      return false;
    }
    return this.serviceName.equals(((ServiceImageCfg) obj).serviceName);
  }

  @Override
  public int hashCode() {
    return this.serviceName.hashCode();
  }


}
