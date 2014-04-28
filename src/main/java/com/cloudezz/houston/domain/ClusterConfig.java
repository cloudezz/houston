package com.cloudezz.houston.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A ClusterConfig.
 */
@Entity
@Table(name = "T_CLUSTERCONFIG")
public class ClusterConfig extends BaseEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 334995451972353684L;

  @Id
  @Column(name = "id")
  protected String id;

  private String clusterKey;

  private String nodeName;

  private String ip;

  private String port;


  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }
  
  public String getClusterKey() {
    return clusterKey;
  }

  public void setClusterKey(String clusterKey) {
    this.clusterKey = clusterKey;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ClusterConfig clusterconfig = (ClusterConfig) o;

    if (id != clusterconfig.id) {
      return false;
    }

    return true;
  }



}
