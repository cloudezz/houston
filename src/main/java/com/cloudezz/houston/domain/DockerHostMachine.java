package com.cloudezz.houston.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The host machine of docker where the docker instances will be started.
 * 
 * @author Thanneer
 * 
 */
@Entity
@Table(name = "T_DOCKER_HOST_MACHINE")
public class DockerHostMachine extends BaseEntity {

  private static final long serialVersionUID = 6685249869925623669L;

  // public ip address of the host machine
  @Id
  private String ipAddress;

  // port on which docker api is listening
  private String dockerPort;

  // IAAS provider name like ec2 or digital ocean
  private String cloudProviderName;

  // docker rest api auth details
  private String username;

  // docker rest api auth details
  private String password;

  // docker rest api on http or https
  private boolean https = false;


  @Override
  public String getId() {
    return ipAddress;
  }
  
  @Override
  public void setId(String id) {
    ipAddress = id;
  }

  /**
   * public ip address of the host machine
   * 
   * @return the ipAddress
   */
  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * @param ipAddress the ipAddress to set
   */
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  /**
   * port on which docker api is listening
   * 
   * @return the dockerPort
   */
  public String getDockerPort() {
    return dockerPort;
  }

  /**
   * IAAS Provider name like ec2 or digital ocean
   * 
   * @param dockerPort the dockerPort to set
   */
  public void setDockerPort(String dockerPort) {
    this.dockerPort = dockerPort;
  }

  /**
   * @return the cloudProviderName
   */
  public String getCloudProviderName() {
    return cloudProviderName;
  }

  /**
   * @param cloudProviderName the cloudProviderName to set
   */
  public void setCloudProviderName(String cloudProviderName) {
    this.cloudProviderName = cloudProviderName;
  }

  /**
   * Docker rest api auth details
   * 
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * 
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Docker rest api auth details
   * 
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the https
   */
  public boolean isHttps() {
    return https;
  }

  /**
   * @param https the https to set
   */
  public void setHttps(boolean https) {
    this.https = https;
  }


  public String getDockerDaemonURL() {
    String protocol = "http";
    if (https) {
      protocol = "https";
    }
    return protocol + "://" + ipAddress + ":" + dockerPort;
  }


}
