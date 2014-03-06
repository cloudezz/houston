package com.cloudezz.houston.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExposedService {

  private String containerId;

  private Map<String, String> serviceToURL= new LinkedHashMap<String,String>();

  public String getContainerId() {
    return containerId;
  }

  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  public Map<String, String> getServiceToURL() {
    return serviceToURL;
  }

  public void addServiceToURL(String serviceName , String url) {
    this.serviceToURL.put(serviceName, url);
  }

}
