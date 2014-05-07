package com.cloudezz.houston.web.rest.dto;

public class OrganisationDTO {
  
  private String orgId;

  private String orgName;

  private String orgDesc;

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public String getOrgDesc() {
    return orgDesc;
  }

  public void setOrgDesc(String orgDesc) {
    this.orgDesc = orgDesc;
  }

  @Override
  public String toString() {
    return "OrganisationDTO{" + "orgName='" + orgName + '\'' + ", orgDesc='" + orgDesc + '\'' + "}";
  }
}
