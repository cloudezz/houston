package com.cloudezz.houston.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A Cloud.
 */
@Entity
@Table(name = "T_CLOUD")
public class Cloud extends BaseEntity {


  /**
   * 
   */
  private static final long serialVersionUID = 6397084337698179597L;

  @Id
  @Column(name = "cloud_name")
  private String name;
  
  @Column(name = "accountId")
  private String accountId;

  @Column(name = "password")
  private String password;

  @Override
  public String getId() {
    return name;
  }

  @Override
  public void setId(String id) {
    name = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the accountId
   */
  public String getAccountId() {
    return accountId;
  }

  /**
   * @param accountId the accountId to set
   */
  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  /**
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
}
