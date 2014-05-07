package com.cloudezz.houston.web.rest.dto;


public class TeamDTO {
  
  private String teamId;
  
  private String teamName;

  private String teamOrg;

  private String teamDesc;

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public String getTeamOrg() {
    return teamOrg;
  }

  public void setTeamOrg(String teamOrg) {
    this.teamOrg = teamOrg;
  }

  public String getTeamDesc() {
    return teamDesc;
  }

  public void setTeamDesc(String teamDesc) {
    this.teamDesc = teamDesc;
  }

}
