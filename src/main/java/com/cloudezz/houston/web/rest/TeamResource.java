package com.cloudezz.houston.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.Team;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.OrganisationRepository;
import com.cloudezz.houston.repository.TeamRepository;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.util.RepositoryUtils;
import com.cloudezz.houston.web.rest.dto.TeamDTO;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing teams.
 */
@RestController
@RequestMapping("/app")
public class TeamResource {

  private final Logger log = LoggerFactory.getLogger(TeamResource.class);

  @Inject
  private TeamRepository teamRepository;

  @Inject
  private OrganisationRepository organisationRepository;

  @Inject
  private UserRepository userRepository;

  /**
   * GET /rest/team/:id -> get the team with id
   */
  @RequestMapping(value = "/rest/team/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public Team getTeamById(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get Team : {}", id);
    Team team = teamRepository.findByTeamId(id);
    if (team == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return team;
  }

  @RequestMapping(value = "/rest/team", method = RequestMethod.POST, produces = "application/json")
  @Timed
  public Team createTeam(@RequestBody TeamDTO teamDto, HttpServletResponse response) {
    log.debug("REST request to get Team : {}", teamDto.getTeamName());
    Team team = new Team();
    team.setId(RepositoryUtils.generateSmallId());
    team.setName(teamDto.getTeamName());
    team.setDesc(teamDto.getTeamDesc());
    team.setOrganisation(organisationRepository.findOne(teamDto.getTeamOrg()));
    team.setUsers(getUsersWithIds(teamDto.getSelectedUsers()));
    team = teamRepository.saveAndFlush(team);
    return team;
  }

  private List<User> getUsersWithIds(List<String> selectedUsers) {
    List<User> assignedUsers = new ArrayList<User>();
    for (String id : selectedUsers) {
      User user = userRepository.findOne(id);
      if (user != null) {
        assignedUsers.add(user);
      }
    }
    return assignedUsers;
  }

  /**
   * GET /rest/teams -> get all the teams.
   */
  @RequestMapping(value = "/rest/team", method = RequestMethod.GET, produces = "application/json")
  @Timed
  public List<TeamDTO> getAllTeams() {
    log.debug("REST request to get all Team");
    List<TeamDTO> teamDtos = null;
    try {
      List<Team> teams = teamRepository.findAll();
      teamDtos = convertToDtos(teams);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return teamDtos;
  }

  private List<TeamDTO> convertToDtos(List<Team> teams) {
    List<TeamDTO> teamDtos = new ArrayList<>();
    for (Team team : teams) {
      TeamDTO tDto = new TeamDTO();
      tDto.setTeamId(team.getId());
      tDto.setTeamName(team.getName());
      tDto.setTeamDesc(team.getDesc());
      tDto.setTeamOrg(team.getOrganisation().getName());
      teamDtos.add(tDto);
    }
    return teamDtos;
  }


}
