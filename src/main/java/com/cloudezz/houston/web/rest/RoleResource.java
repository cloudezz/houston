package com.cloudezz.houston.web.rest;

import java.util.List;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.Role;
import com.cloudezz.houston.repository.RoleRepository;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class RoleResource {

  private final Logger log = LoggerFactory.getLogger(RoleResource.class);

  @Inject
  private RoleRepository roleRepository;

  /**
   * GET /rest/cloud -> get the cloud.
   */
  @RequestMapping(value = "/rest/role/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public Role getCloud(@PathVariable("id") String id) {
    Role role = roleRepository.findOne(id);
    return role;
  }

  /**
   * POST /rest/cloud -> post the cloud.
   */
  @RequestMapping(value = "/rest/role", method = RequestMethod.POST, produces = "application/json",
      consumes = "application/json")
  @Timed
  public Role saveCloud(@RequestBody Role role) {
    role = roleRepository.save(role);
    return role;
  }

  /**
   * GET /rest/cloud -> Get all cloud.
   */
  @RequestMapping(value = "/rest/role", method = RequestMethod.GET, produces = "application/json")
  @Timed
  public List<Role> getAll() {
    List<Role> roles = roleRepository.findAll();
    return roles;
  }

}
