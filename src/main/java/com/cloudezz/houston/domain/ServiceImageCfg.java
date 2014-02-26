package com.cloudezz.houston.domain;

import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Service image info object that hold information related to service like mysql or redis and would
 * be always attached to a application image . These images objects represent how the image will be
 * build and run as containers in docker
 * 
 * @author Thanneer
 * 
 */
@Entity
@Table(name = "T_SERVICE_IMAGE_CONFIG")
public class ServiceImageCfg extends BaseImageCfg {

  private static final long serialVersionUID = 1857747836263604938L;

  @Column(name="link_name")
  private String linkName;

  @Id
  @Column(name="service_name")
  private String serviceName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "app_config_id")
  private AppImageCfg applicationImageConfig;

  @Override
  public String getId() {
    return serviceName;
  }

  @Override
  public void setId(String id) {
    serviceName = id;
  }

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
        linkName = dockerImageName.replace("/", "_") + "_" + new Random().nextInt(99);
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

  /**
   * @return the serviceName
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * @param serviceName the serviceName to set
   */
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * @return the applicationImageConfig
   */
  public AppImageCfg getApplicationImageConfig() {
    return applicationImageConfig;
  }

  /**
   * @param applicationImageConfig the applicationImageConfig to set
   */
  public void setApplicationImageConfig(AppImageCfg applicationImageConfig) {
    this.applicationImageConfig = applicationImageConfig;
  }



}
