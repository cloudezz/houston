package com.cloudezz.houston.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;

public class BaseImageConfig {

  protected String containerId;

  protected DockerHostMachine dockerHostMachine;

  protected String dockerImageName;

  protected String hostName;

  protected String[] ports;

  protected Long memory;

  protected Long memorySwap;

  protected Integer cpuShares;

  protected Map<String, String> hostToDockerVolumeMapping = new HashMap<String, String>();

  protected Map<String, String> environmentMapping = new HashMap<String, String>();

  protected Boolean daemon;

  protected Boolean tty;

  private HostConfig hostConfig;


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
    return dockerImageName;
  }

  /**
   * @param dockerImageName the dockerImageName to set
   */
  public void setDockerImageName(String dockerImageName) {
    this.dockerImageName = dockerImageName;
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


  public String[] getPorts() {
    return ports;
  }

  public void setPorts(String[] ports) {
    this.ports = ports;
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
  public Boolean getDaemon() {
    return daemon;
  }

  /**
   * @param daemon the daemon to set
   */
  public void setDaemon(Boolean daemon) {
    this.daemon = daemon;
  }

  /**
   * @return the tty
   */
  public Boolean getTty() {
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


  protected HostConfig getDefaultHostConfig() {

    HostConfig hostConfig = new HostConfig();
    hostConfig.setPublishAllPorts(true);

    // port mappings
    Map<String, HostPortBinding[]> portBindings = hostConfig.getPortBindings();
    String portMappings[] = this.getPorts();
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
