package com.cloudezz.houston.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "T_ORGANISATION")
public class Organisation extends BaseEntity {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Id
  @Column(name = "id")
  private String orgId;

  @OneToMany
  private List<Team> teams;

  @Column(name = "description")
  private String desc;

  @Override
  public String getId() {
    return orgId;
  }

  @Override
  public void setId(String id) {
    this.orgId = id;
  }

  public List<Team> getTeams() {
    return teams;
  }

  public void setTeams(List<Team> teams) {
    this.teams = teams;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Organisation org = (Organisation) o;

    if (!orgId.equals(org.orgId)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return orgId.hashCode();
  }

  @Override
  public String toString() {
    return this.name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }
}
