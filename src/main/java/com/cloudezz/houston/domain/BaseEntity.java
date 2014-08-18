package com.cloudezz.houston.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Base entity for holding information that is available across the entities
 * 
 * @author Dhanush Gopinath
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

  private static final long serialVersionUID = -5534745615089076782L;

  protected String name;

  @Column(name = "creation_time")
  protected Timestamp creationTime;

  @Column(name = "modification_time")
  protected Timestamp modificationTime;

  /**
   * Child entities can override this to send back their own id's
   * 
   * @return
   */
  public abstract String getId();

  public abstract void setId(String id);

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

  @JsonIgnore
  public Timestamp getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Timestamp creationTime) {
    this.creationTime = creationTime;
  }

  @JsonIgnore
  public Timestamp getModificationTime() {
    return modificationTime;
  }

  public void setModificationTime(Timestamp modificationTime) {
    this.modificationTime = modificationTime;
  }


  @PrePersist
  protected void autoSetCreationTime() {
    this.creationTime = this.modificationTime = new Timestamp(new Date().getTime());
  }

  @PreUpdate
  void autoSetModificationTime() {
    this.modificationTime = new Timestamp(new Date().getTime());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    BaseEntity entity = (BaseEntity) obj;
    if (this.getId().equals(entity.getId())) {
      return true;
    }
    return false;
  }

}
