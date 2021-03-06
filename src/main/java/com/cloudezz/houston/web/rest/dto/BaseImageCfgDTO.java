package com.cloudezz.houston.web.rest.dto;

import java.util.List;
import java.util.Map;


public class BaseImageCfgDTO {

  protected String appName;

  protected Integer noOfInstance = 1;

  protected String imageName;

  protected String groupName;

  protected String gitURL;

  protected List<String> ports;

  protected String hostName;

  protected String domainName;

  protected Long memory = 0L;

  protected Long memorySwap = 0L;

  protected Integer cpuShares = 0;

  protected Boolean daemon = new Boolean(true);

  protected Map<String, String> environmentMapping;

  protected String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public Integer getNoOfInstance() {
    return noOfInstance;
  }

  public void setNoOfInstance(Integer noOfInstance) {
    this.noOfInstance = noOfInstance;
  }

  public String getGitURL() {
    return gitURL;
  }

  public void setGitURL(String gitURL) {
    this.gitURL = gitURL;
  }

  public List<String> getPorts() {
    return ports;
  }

  public void setPorts(List<String> ports) {
    this.ports = ports;
  }

  public String getHostName() {
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }

  public Long getMemory() {
    return memory;
  }

  public void setMemory(Long memory) {
    this.memory = memory;
  }

  public Long getMemorySwap() {
    return memorySwap;
  }

  public void setMemorySwap(Long memorySwap) {
    this.memorySwap = memorySwap;
  }

  public Integer getCpuShares() {
    return cpuShares;
  }

  public void setCpuShares(Integer cpuShares) {
    this.cpuShares = cpuShares;
  }

  public Boolean getDaemon() {
    return daemon;
  }

  public void setDaemon(Boolean daemon) {
    this.daemon = daemon;
  }

  public Map<String, String> getEnvironmentMapping() {
    return environmentMapping;
  }

  public void setEnvironmentMapping(Map<String, String> environmentMapping) {
    this.environmentMapping = environmentMapping;
  }


}
