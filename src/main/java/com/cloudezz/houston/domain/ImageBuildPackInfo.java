package com.cloudezz.houston.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Docker Image is the one hold the map between the build back git url and docker image name . This
 * object is a jpa object to the table that holds the map info
 * 
 * @author Thanneer
 * 
 */
@Entity
@Table(name = "T_IMAGE_BUILD_PACK_INFO")
public class ImageBuildPackInfo extends BaseEntity {

  private static final long serialVersionUID = 8051510635093548094L;

  @Id
  private String imageName;

  private String buildPackGitURL;

  @Override
  public String getId() {
    return imageName;
  }
  
  @Override
  public void setId(String id) {
    imageName = id;
  }

  /**
   * @return the imageName
   */
  public String getImageName() {
    return imageName;
  }

  /**
   * @param imageName the imageName to set
   */
  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  /**
   * @return the buildPackGitURL
   */
  public String getBuildPackGitURL() {
    return buildPackGitURL;
  }

  /**
   * @param buildPackGitURL the buildPackGitURL to set
   */
  public void setBuildPackGitURL(String buildPackGitURL) {
    this.buildPackGitURL = buildPackGitURL;
  }



}
