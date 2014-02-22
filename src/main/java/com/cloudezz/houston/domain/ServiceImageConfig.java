package com.cloudezz.houston.domain;

import java.util.Random;

/**
 * Service image info object that hold information related to service like mysql or redis and would
 * be always attached to a application image . These images objects represent how the image will be
 * build and run as containers in docker
 * 
 * @author Thanneer
 * 
 */
public class ServiceImageConfig extends BaseImageConfig {

  private String linkName;

  /**
   * The name is used to link the service image as child image with parent image . Parent image
   * ----linkname---> child image.
   * 
   * @return the linkName
   */
  public String getLinkName() {
    // if link name is not there then auto generate one
    if (linkName == null || linkName.isEmpty()) {
      if (dockerImageName.contains("/")) {
        linkName = dockerImageName.replace("/", "_")+"_"+new Random().nextInt(99);
      } else {
        linkName = dockerImageName;
      }
    }
    return linkName;
  }

  /**
   * @param linkName the linkName to set
   */
  public void setLinkName(String linkName) {
    this.linkName = linkName;
  }

}
