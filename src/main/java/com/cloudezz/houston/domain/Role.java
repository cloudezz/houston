package com.cloudezz.houston.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A Cloud.
 */
@Entity
@Table(name = "T_ROLE")
public class Role extends BaseEntity {

  private static final long serialVersionUID = -6805097159089522437L;
  
  @Id
  @Column(name = "role_name")
  private String name;

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
  
}
