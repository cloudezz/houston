package com.cloudezz.houston.web.rest.dto;

import java.util.LinkedList;
import java.util.List;


public class AppImageCfgDTO extends BaseImageCfgDTO {

  protected List<ServiceImageCfgDTO> serviceImages;
  
  protected String initScript;
  
  protected String desc = "";
  
  protected List<String> tags = new LinkedList<String>();

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

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

}
