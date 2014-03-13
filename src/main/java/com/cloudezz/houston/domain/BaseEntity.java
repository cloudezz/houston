package com.cloudezz.houston.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

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

  @JsonIgnore
  @Column(name = "creation_time")
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
  protected LocalDateTime creationTime;

  @JsonIgnore
  @Column(name = "modification_time")
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
  protected LocalDateTime modificationTime;

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
   * Child entities can override this to send back their own id's
   * 
   * @return
   */
  public abstract String getId();

  public abstract void setId(String id);


  public LocalDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(LocalDateTime creationTime) {
    this.creationTime = creationTime;
  }

  public LocalDateTime getModificationTime() {
    return modificationTime;
  }

  public void setModificationTime(LocalDateTime modificationTime) {
    this.modificationTime = modificationTime;
  }


  @PrePersist
  protected void autoSetCreationTime() {
    this.creationTime = this.modificationTime = LocalDateTime.now();
  }

  @PreUpdate
  void autoSetModificationTime() {
    this.modificationTime = LocalDateTime.now();
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
