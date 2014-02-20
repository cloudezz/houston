package com.cloudezz.houston.domain;

import java.io.Serializable;

/**
 * Base entity for holding information that is available across the entities
 * 
 * @author Dhanush Gopinath
 * @since 1.0.0
 */
public abstract class BaseEntity implements Serializable {

  private static final long serialVersionUID = -5534745615089076782L;

  private String name;


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
