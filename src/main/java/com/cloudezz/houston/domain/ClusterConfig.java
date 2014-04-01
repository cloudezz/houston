package com.cloudezz.houston.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A ClusterConfig.
 */
@Entity
@Table(name = "T_CLUSTERCONFIG")
public class ClusterConfig implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 334995451972353684L;

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long id;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  private String clusterKey;

  private String nodeName;

  private String ip;

  private String port;

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

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }

}
