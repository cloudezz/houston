package com.cloudezz.houston.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name = "T_EXPOSED_SERVICE")
public class ExposedService extends BaseEntity {

  private static final long serialVersionUID = -5956780711994167850L;

  @Id
  @Column(name = "container_id")
  private String containerId;
  
  @Column(name = "instance_no")
  private Integer instanceNo;

  @Column(name = "is_service_image",nullable = false, columnDefinition = "TINYINT")
  private boolean serviceImage;

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "service_name")
  @Column(name = "service_url", nullable = true)
  @CollectionTable(name = "T_EXPOSED_SERVICE_NAME_URL_MAPPING", joinColumns = @JoinColumn(
      name = "container_id"))
  private Map<String, String> serviceToURL = new LinkedHashMap<String, String>();

  public String getContainerId() {
    return this.containerId;
  }

  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  public Map<String, String> getServiceToURL() {
    return serviceToURL;
  }

  public void addServiceToURL(String serviceName, String url) {
    this.serviceToURL.put(serviceName, url);
  }

  @Override
  public String getId() {
    return containerId;
  }

  @Override
  public void setId(String id) {
    containerId = id;
  }


  public boolean isServiceImage() {
    return serviceImage;
  }

  public void setServiceImage(boolean serviceImage) {
    this.serviceImage = serviceImage;
  }

  public Integer getInstanceNo() {
    return instanceNo;
  }

  public void setInstanceNo(Integer instanceNo) {
    this.instanceNo = instanceNo;
  }

}
