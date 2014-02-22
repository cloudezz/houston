package com.cloudezz.houston.domain;

/**
 * Service image info object that hold information related to service like mysql
 * or redis and would be always attached to a application image . These images objects
 * represent how the image will be build and run as containers in docker
 * 
 * @author Thanneer
 * 
 */
public class ServiceImageConfig extends BaseCloudezzImageConfig {
  
  private String linkName;

  /**
   * The  name is used to link the service image as child image with parent image . Parent image ----linkname---> child image.
   * @return the linkName
   */
  public String getLinkName() {
    return linkName;
  }

  /**
   * @param linkName the linkName to set
   */
  public void setLinkName(String linkName) {
    this.linkName = linkName;
  }
	
}
