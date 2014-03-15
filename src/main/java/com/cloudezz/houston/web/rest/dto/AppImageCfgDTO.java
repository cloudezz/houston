package com.cloudezz.houston.web.rest.dto;

import java.util.List;


public class AppImageCfgDTO extends BaseImageCfgDTO {

  protected List<ServiceImageCfgDTO> serviceImages;
  
  protected String initScript;

  public String getInitScript() {
    return initScript;
  }

  public void setInitScript(String initScript) {
    this.initScript = initScript;
  }

  public List<ServiceImageCfgDTO> getServiceImages() {
    return serviceImages;
  }

  public void setServiceImages(List<ServiceImageCfgDTO> serviceImages) {
    this.serviceImages = serviceImages;
  }

}
