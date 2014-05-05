package com.cloudezz.houston.domain.cloud;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.cloudezz.houston.domain.BaseEntity;

@Entity
@Table(name = "T_CLOUD_PROVIDER")
public class CloudProvider extends BaseEntity {

  private static final long serialVersionUID = -8269442687185467150L;

  @Id
  private String id;

  private String principal;

  private String secret;

  private String location;


  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;

  }

  public String getPrincipal() {
    return principal;
  }

  public void setPrincipal(String principal) {
    this.principal = principal;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }


}
