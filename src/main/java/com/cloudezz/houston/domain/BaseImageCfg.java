package com.cloudezz.houston.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;
import com.fasterxml.jackson.annotation.JsonGetter;
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

  @OneToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
  @JoinColumn(name = "container")
  protected Container container;

  @Column(name = "instance_no")
  private Integer instanceNo;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "docker_host")
  protected DockerHostMachine dockerHostMachine;

  @Column(name = "image_name")
  protected String imageName;

  @Column(name = "host_name")
  protected String hostName = "";

  @Column(name = "domain_name")
  protected String domainName = "";

  @Column(name = "group_name")
  protected String groupName = "";

  protected Long memory = 0L;

  @Column(name = "memory_swap")
  protected Long memorySwap = 0L;

  @Column(name = "cpu_shares")
  protected Integer cpuShares = 0;

  @Column(name = "data_volume_from")
  protected String dataVolumeFrom = "";

  @Column(nullable = false, columnDefinition = "TINYINT")
  protected Boolean daemon = new Boolean(true);

  @Column(nullable = false, columnDefinition = "TINYINT")
  protected Boolean tty = new Boolean(true);

  @Column(name = "init_script", columnDefinition = "VARCHAR(6000)")
  protected String initScript = "";

  @Transient
  @JsonIgnore
  protected HostConfig hostConfig;

  @Transient
  @JsonIgnore
  protected Map<String, String> dockerPortToHostPort = new HashMap<String, String>();

  public abstract List<String> getDns();

  public abstract void setDns(List<String> dns);

  public abstract List<String> getPorts();

  public abstract void setPorts(List<String> ports);

  public abstract String[] getDnsAsArray();

  public abstract String[] getPortsAsArray();


  /**
   * @return the container
   */
  public Container getContainer() {
    return container;
  }

  /**
   * @param container the container to set
   */
  public void setContainer(Container container) {
    this.container = container;
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
   * @return the imageName
   */
  public String getImageName() {
    return imageName;
  }

  /**
   * @param imageName the imageName to set
   */
  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  /**
   * @return the image type
   */
  @JsonGetter
  public String getImageType() {
    if (!imageName.contains("/"))
      return imageName;

    String imageType = "";
    String imageRepoName = "";
    StringTokenizer stringTokenizer = new StringTokenizer(imageName, "/");
    if (stringTokenizer.countTokens() == 2) {
      imageRepoName = stringTokenizer.nextToken();
      imageType = stringTokenizer.nextToken();
    } else {
      return imageName;
    }

    return imageType;
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
    if (hostName == null) {
      this.hostName = getId();
    } else {
      this.hostName = hostName;
    }
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    if (domainName == null) {
      this.domainName = getId();
    } else {
      this.domainName = domainName;
    }
  }


  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
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
   * Get the start init script for image
   * 
   * @return
   */
  public String getInitScript() {
    return initScript;
  }

  public void setInitScript(String initScript) {
    this.initScript = initScript;
  }

  /**
   * @return the volumeMapping
   */
  public String[] getVolumeMapping() {
    String[] volumeMapping = null;
    if (getHostToDockerVolumeMapping() != null && getHostToDockerVolumeMapping().size() > 0) {
      int index = 0;
      volumeMapping = new String[getHostToDockerVolumeMapping().size()];
      for (Iterator<String> iterator = getHostToDockerVolumeMapping().keySet().iterator(); iterator
          .hasNext();) {
        String hostVol = iterator.next();
        String dockerVol = getHostToDockerVolumeMapping().get(hostVol);
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


  public String getDataVolumeFrom() {
    return dataVolumeFrom;
  }

  public void setDataVolumeFrom(String dataVolumeFrom) {
    this.dataVolumeFrom = dataVolumeFrom;
  }

  /**
   * @return the hostToDockervolumeMapping
   */
  public abstract Map<String, String> getHostToDockerVolumeMapping();

  /**
   * @param hostToDockervolumeMapping the hostToDockervolumeMapping to set
   */
  public abstract void setHostToDockerVolumeMapping(Map<String, String> hostToDockervolumeMapping);

  /**
   * @param hostToDockervolumeMapping the hostToDockervolumeMapping to set
   */
  public abstract void addHostToDockerVolumeMapping(String hostVolume, String dockerVolume);

  /**
   * @return the environmentMapping
   */
  public abstract Map<String, String> getEnvironmentMapping();

  /**
   * @param environmentMapping the environmentMapping to set
   */
  public abstract void setEnvironmentMapping(Map<String, String> environmentMapping);

  /**
   * @param environmentMapping the environmentMapping to set
   */
  public abstract void addEnvironmentMapping(String envName, String envValue);

  /**
   * Get application
   * 
   * @return
   */
  public abstract Application getApplication();

  /**
   * Set application
   * 
   * @param application
   */
  public abstract void setApplication(Application application);

  public Map<String, String> getDockerPortToHostPort() {
    return dockerPortToHostPort;
  }

  public void setDockerPortToHostPort(Map<String, String> dockerPortToHostPort) {
    this.dockerPortToHostPort = dockerPortToHostPort;
  }

  public Integer getInstanceNo() {
    return instanceNo;
  }

  public void setInstanceNo(Integer instanceNo) {
    this.instanceNo = instanceNo;
  }


}
