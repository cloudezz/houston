package com.cloudezz.houston.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "T_DEPLOYMENT_SCRIPT")
public class DeploymentScript extends BaseEntity {

  private static final long serialVersionUID = 1420676857557056115L;

  @Id
  @Column(name = "script_name")
  protected String scriptName;

  @Column(name = "script_desc", columnDefinition = "VARCHAR(1500)")
  protected String desc = "";
  
  @Column(name = "scriptId")
  protected String scriptId;
  
  @Column(name = "yml_fileName")
  protected String ymlFileName;
  
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_user_id", nullable = false)
  protected User owner;
  
  @Override
  public String getId() {
    return scriptName;
  }

  @Override
  public void setId(String id) {
    this.scriptName = id;
  }

  public String getScriptName() {
    return scriptName;
  }

  public void setScriptName(String scriptName) {
    this.scriptName = scriptName;
  }

  @JsonIgnore
  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getScriptId() {
    return scriptId;
  }

  public void setScriptId(String scriptId) {
    this.scriptId = scriptId;
  }

  public String getYmlFileName() {
    return ymlFileName;
  }

  public void setYmlFileName(String ymlFileName) {
    this.ymlFileName = ymlFileName;
  }

}
