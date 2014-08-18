package com.cloudezz.houston.deployer.docker.model;


import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerInspectResponse {

  @JsonProperty("Id")
  public String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  @JsonProperty("Name")
  public String name;
  @JsonProperty("Created")
  public String created;
  @JsonProperty("Path")
  public String path;
  @JsonProperty("HostnamePath")
  public String hostnamePath;
  @JsonProperty("HostConfig")
  public HostConfig hostConfig;
  @JsonProperty("HostsPath")
  public String hostsPath;
  @JsonProperty("Args")
  public String[] args;
  @JsonProperty("Image")
  public String image;
  @JsonProperty("SysInitPath")
  public String sysInitPath;
  @JsonProperty("ResolvConfPath")
  public String resolvConfPath;
  @JsonProperty("Volumes")
  public Map<String, String> volumes;
  @JsonProperty("VolumesRW")
  public Map<String, String> volumesRW;
  @JsonProperty("Driver")
  public String driver;
  
  @JsonProperty("Config")
  public ContainerConfig config;

  @JsonProperty("State")
  public ContainerState state;


  @JsonProperty("NetworkSettings")
  public NetworkSettings networkSettings;
  



  @JsonProperty("ExecDriver")
  private String execDriver;
  @JsonProperty("MountLabel")
  private String mountLabel;
  @JsonProperty("ProcessLabel")
  private String processLabel;


  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public ContainerConfig getConfig() {
    return config;
  }


  public class NetworkSettings {

    @JsonProperty("IPAddress")
    public String ipAddress;
    @JsonProperty("IPPrefixLen")
    public int ipPrefixLen;
    @JsonProperty("Gateway")
    public String gateway;
    @JsonProperty("Bridge")
    public String bridge;
    @JsonProperty("PortMapping")
    public Map<String, Map<String, String>> portMapping;
    @JsonProperty("Ports")
    public Map<String, HostPortBinding[]> ports;

    @Override
    public String toString() {
      return "NetworkSettings [ipAddress=" + ipAddress + ", ipPrefixLen=" + ipPrefixLen
          + ", gateway=" + gateway + ", bridge=" + bridge + ", portMapping=" + portMapping
          + ", ports=" + ports + "]";
    }
  }

  public class ContainerState {

    @JsonProperty("Running")
    public boolean running;
    @JsonProperty("Pid")
    public int pid;
    @JsonProperty("ExitCode")
    public int exitCode;
    @JsonProperty("StartedAt")
    public String startedAt;
    @JsonProperty("FinishedAt")
    public String finishedAt;
    @JsonProperty("Ghost")
    public boolean ghost;
    @JsonProperty("Paused")
    public boolean paused;


    @Override
    public String toString() {
      return "ContainerState{" + "running=" + running + ", pid=" + pid + ", exitCode=" + exitCode
          + ", startedAt='" + startedAt + '\'' + ", finishedAt='" + finishedAt + '\'' + ", ghost="
          + ghost + '}';
    }
  }

}
