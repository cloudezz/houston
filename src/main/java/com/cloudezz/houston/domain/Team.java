package com.cloudezz.houston.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "T_TEAM")
public class Team extends BaseEntity {

  private static final long serialVersionUID = 1L;
  @ManyToMany
  private List<User> users;
  @NotNull
  @Id
  @Column(name = "id")
  private String teamId;
  
  @ManyToOne
  private Organisation organisation;

  @Column(name = "description")
  private String desc;

  @Override
  public String getId() {
    return teamId;
  }

  @Override
  public void setId(String id) {
    this.teamId = id;
  }

  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public Organisation getOrganisation() {
    return organisation;
  }

  public void setOrganisation(Organisation organisation) {
    this.organisation = organisation;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }



}
