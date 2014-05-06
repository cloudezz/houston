package com.cloudezz.houston.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "T_CONTAINER")
public class Container extends BaseEntity {

  private static final long serialVersionUID = 4880506167220787354L;

  @Id
  private String containerId;

  private String host;

  private Integer SSHPort;

  private String SSHPemPrivateKey;

  private String SSHPemPrivateKeyPassword;

  private String SSHPassword;

  private String SSHUsername;

  @Override
  public String getId() {
    return containerId;
  }

  @Override
  public void setId(String id) {
    this.containerId = id;
  }

  public String getContainerId() {
    return containerId;
  }

  public String getHost() {
    return host;
  }

  public Integer getSSHPort() {
    return SSHPort;
  }

  public String getSSHPemPrivateKey() {
    return SSHPemPrivateKey;
  }

  public String getSSHPemPrivateKeyPassword() {
    return SSHPemPrivateKeyPassword;
  }

  public String getSSHPassword() {
    return SSHPassword;
  }

  public String getSSHUsername() {
    return SSHUsername;
  }

  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setSSHPort(Integer sSHPort) {
    SSHPort = sSHPort;
  }

  public void setSSHPemPrivateKey(String sSHPemPrivateKey) {
    SSHPemPrivateKey = sSHPemPrivateKey;
  }

  public void setSSHPemPrivateKeyPassword(String sSHPemPrivateKeyPassword) {
    SSHPemPrivateKeyPassword = sSHPemPrivateKeyPassword;
  }

  public void setSSHPassword(String sSHPassword) {
    SSHPassword = sSHPassword;
  }

  public void setSSHUsername(String sSHUsername) {
    SSHUsername = sSHUsername;
  }

  public String toString() {
    return containerId;
  }
}
