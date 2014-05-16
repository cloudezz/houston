package com.cloudezz.houston.web.rest.dto;

import java.util.List;


public class TeamDTO {

  private String teamId;

  private String teamName;

  private OrganisationDTO teamOrg;

  private String teamDesc;

  private List<UserDTO> selectedUsers;

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

  public OrganisationDTO getTeamOrg() {
    return teamOrg;
  }

  public void setTeamOrg(OrganisationDTO teamOrg) {
    this.teamOrg = teamOrg;
  }

  public String getTeamDesc() {
    return teamDesc;
  }

  public void setTeamDesc(String teamDesc) {
    this.teamDesc = teamDesc;
  }

  public List<UserDTO> getSelectedUsers() {
    return selectedUsers;
  }

  public void setSelectedUsers(List<UserDTO> selectedUsers) {
    this.selectedUsers = selectedUsers;
  }

}
