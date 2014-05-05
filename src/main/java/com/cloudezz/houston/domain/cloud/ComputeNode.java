package com.cloudezz.houston.domain.cloud;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.cloudezz.houston.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The host machine of docker where the docker instances will be started.
 * 
 * @author Thanneer
 * 
 */

@Entity
@Table(name = "T_COMPUTE_NODE")
public class ComputeNode extends BaseEntity {

  
  private static final long serialVersionUID = 701978505682078184L;

  @Id
  private String id;
  
  // public ip address of the host machine
  
  @Column(name = "ip_address")
  private String ipAddress;

  // port on which docker api is listening
  @Column(name = "docker_port")
  private String dockerPort;


  // host name of ec2 or digital ocean m/c
  @Column(name = "host_name")
  private String hostName;

  // IAAS provider name like ec2 or digital ocean
  @ManyToOne
  private CloudProvider cloudProvider;

  // docker rest api auth details
  private String username;

  // docker rest api auth details
  private String password;

  // port ssh service
  @Column(name = "ssh_port")
  private int sshPort;

  // shh key to connect to docker host
  @Column(name = "ssh_key", length = 10000)
  private String key;

  // docker rest api on http or https
  @Column(nullable = false, columnDefinition = "TINYINT")
  private boolean https = false;

  // docker rest api on http or https
  @Column(nullable = false, columnDefinition = "TINYINT")
  private boolean sudo = false;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
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


  @JsonIgnore
  public String getDockerDaemonURL() {
    String protocol = "http";
    if (https) {
      protocol = "https";
    }
    return protocol + "://" + ipAddress + ":" + dockerPort;
  }

  public String getHostName() {
    if (hostName == null)
      return ipAddress;

    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public boolean isSudo() {
    return sudo;
  }

  public void setSudo(boolean sudo) {
    this.sudo = sudo;
  }

  public int getSshPort() {
    return sshPort;
  }

  public void setSshPort(int sshPort) {
    this.sshPort = sshPort;
  }

  public CloudProvider getCloudProvider() {
    return cloudProvider;
  }

  public void setCloudProvider(CloudProvider cloudProvider) {
    this.cloudProvider = cloudProvider;
  }



}
