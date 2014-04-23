package com.cloudezz.houston.web.rest.dto;

import java.util.LinkedList;
import java.util.List;


public class ApplicationDTO {

  protected String appName;

  protected List<AppImageCfgDTO> appImages;

  protected List<ServiceImageCfgDTO> serviceImages;

  protected String desc = "";

  protected List<String> tags = new LinkedList<String>();

  protected String id;

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    if (id != null && !id.isEmpty()) {
      return id;
    }
    return appName;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public List<AppImageCfgDTO> getAppImages() {
    return appImages;
  }

  public void setAppImages(List<AppImageCfgDTO> appImages) {
    this.appImages = appImages;
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
