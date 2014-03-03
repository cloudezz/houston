package com.cloudezz.houston.web.rest.dto;

import java.util.List;


public class AppImageCfgDTO extends BaseImageCfgDTO {

  protected List<ServiceImageCfgDTO> serviceImages;

  public List<ServiceImageCfgDTO> getServiceImages() {
    return serviceImages;
  }

  public void setServiceImages(List<ServiceImageCfgDTO> serviceImages) {
    this.serviceImages = serviceImages;
  }

}
