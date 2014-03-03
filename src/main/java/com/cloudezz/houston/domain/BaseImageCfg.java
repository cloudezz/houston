package com.cloudezz.houston.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The base class for image config
 * 
 * @author Thanneer
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class BaseImageCfg extends BaseEntity {

  private static final long serialVersionUID = 5524208892445624915L;

  protected String containerId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "docker_host")
  protected DockerHostMachine dockerHostMachine;

  @Column(name = "image_name")
  protected String imageName;

  @Column(name = "host_name")
  protected String hostName="";

  protected String user="";

  @Column(name = "domain_name")
  protected String domainName="";

  protected Long memory=0L;

  @Column(name = "memory_swap")
  protected Long memorySwap=0L;

  @Column(name = "cpu_shares")
  protected Integer cpuShares=0;

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "host_volume")
  @Column(name = "volume_mapping")
  @CollectionTable(name = "T_VOLUME_MAPPING", joinColumns = @JoinColumn(name = "vol_mapping_id"))
  protected Map<String, String> hostToDockerVolumeMapping = new HashMap<String, String>();

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "env_name")
  @Column(name = "env_mapping")
  @CollectionTable(name = "T_ENVIRONMENT_VARIABLE_MAPPING", joinColumns = @JoinColumn(
      name = "env_mapping_id"))
  protected Map<String, String> environmentMapping = new HashMap<String, String>();

  protected Boolean daemon=new Boolean(true);

  protected Boolean tty=new Boolean(true);
  
  protected Boolean running=new Boolean(false);

  @Transient
  @JsonIgnore
  protected HostConfig hostConfig;
  
  
  public abstract List<String> getDns();

  public abstract void setDns(List<String> dns);

  public abstract List<String> getPorts();

  public abstract void setPorts(List<String> ports);

  public abstract String[] getDnsAsArray();

  public abstract String[] getPortsAsArray();


  /**
   * @return the containerId
   */
  public String getContainerId() {
    return containerId;
  }

  /**
   * @param containerId the containerId to set
   */
  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  /**
   * @return the dockerHostMachine
   */
  public DockerHostMachine getDockerHostMachine() {
    return dockerHostMachine;
  }

  /**
   * @param dockerHostMachine the dockerHostMachine to set
   */
  public void setDockerHostMachine(DockerHostMachine dockerHostMachine) {
    this.dockerHostMachine = dockerHostMachine;
  }

  /**
   * @return the dockerImageName
   */
  public String getDockerImageName() {
    return imageName;
  }

  /**
   * @param dockerImageName the dockerImageName to set
   */
  public void setDockerImageName(String dockerImageName) {
    this.imageName = dockerImageName;
  }

  /**
   * @return the hostName
   */
  public String getHostName() {
    return hostName;
  }

  /**
   * @param hostName the hostName to set
   */
  public void setHostName(String hostName) {
    this.hostName = hostName;
  }


  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }



  /**
   * @return the memory
   */
  public Long getMemory() {
    return memory;
  }

  /**
   * @param memory the memory to set
   */
  public void setMemory(Long memory) {
    this.memory = memory;
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


  /**
   * @return the daemon
   */
  public Boolean isDaemon() {
    return daemon;
  }

  /**
   * @param daemon the daemon to set
   */
  public void setDaemon(Boolean daemon) {
    this.daemon = daemon;
  }

  public Boolean isRunning() {
    return running;
  }

  public void setRunning(Boolean running) {
    this.running = running;
  }

  /**
   * @return the tty
   */
  public Boolean isTty() {
    return tty;
  }

  /**
   * @param tty the tty to set
   */
  public void setTty(Boolean tty) {
    this.tty = tty;
  }

  /**
   * @return the memorySwap
   */
  public Long getMemorySwap() {
    return memorySwap;
  }

  /**
   * @param memorySwap the memorySwap to set
   */
  public void setMemorySwap(Long memorySwap) {
    this.memorySwap = memorySwap;
  }

  /**
   * @return the cpuShares
   */
  public Integer getCpuShares() {
    return cpuShares;
  }

  /**
   * @param cpuShares the cpuShares to set
   */
  public void setCpuShares(Integer cpuShares) {
    this.cpuShares = cpuShares;
  }

  /**
   * @return the hostConfig
   */
  @Transient
  public HostConfig getHostConfig() {
    if (hostConfig == null) {
      hostConfig = getDefaultHostConfig();
    }
    return hostConfig;
  }

  /**
   * @param hostConfig the hostConfig to set
   */
  public void setHostConfig(HostConfig hostConfig) {
    this.hostConfig = hostConfig;
  }



  /**
   * @return the volumeMapping
   */
  public String[] getVolumeMapping() {
    String[] volumeMapping = null;
    if (hostToDockerVolumeMapping != null && hostToDockerVolumeMapping.size() > 0) {
      int index = 0;
      volumeMapping = new String[hostToDockerVolumeMapping.size()];
      for (Iterator<String> iterator = hostToDockerVolumeMapping.keySet().iterator(); iterator
          .hasNext();) {
        String hostVol = iterator.next();
        String dockerVol = hostToDockerVolumeMapping.get(hostVol);
        String volMap = hostVol + ":" + dockerVol;
        volumeMapping[index] = volMap;
        index++;
      }
    }
    return volumeMapping;
  }

  @Transient
  protected HostConfig getDefaultHostConfig() {

    HostConfig hostConfig = new HostConfig();
    hostConfig.setPublishAllPorts(true);
    hostConfig.setLxcConf(new HostConfig.LxcConf[0]);

    // port mappings
    Map<String, HostPortBinding[]> portBindings = hostConfig.getPortBindings();
    String portMappings[] = this.getPortsAsArray();
    if (portMappings != null) {
      for (int i = 0; i < portMappings.length; i++) {
        HostPortBinding[] portBindingForContainerPort = new HostPortBinding[1];
        portBindingForContainerPort[0] = new HostPortBinding();
        portBindings.put(portMappings[i] + "/tcp", portBindingForContainerPort);
      }
      // note : lil doubtful abt this settings as we are setting port for host so commented ..need
      // to test after uncommeting

    }

    // volume mapping with read write access on docker host machine
    String volMapping[] = this.getVolumeMapping();
    if (volMapping != null && volMapping.length > 0) {
      for (int i = 0; i < volMapping.length; i++) {
        volMapping[i] = volMapping[i] + ":rw";
      }
      hostConfig.setBinds(volMapping);
    }

    return hostConfig;
  }
}
