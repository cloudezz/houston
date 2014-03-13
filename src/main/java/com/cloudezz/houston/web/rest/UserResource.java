package com.cloudezz.houston.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/app")
public class UserResource {

  private final Logger log = LoggerFactory.getLogger(UserResource.class);

  @Inject
  private UserRepository userRepository;

  /**
   * GET /rest/users/:login -> get the "login" user.
   */
  @RequestMapping(value = "/rest/users/{login}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public User getUser(@PathVariable String login, HttpServletResponse response) {
    log.debug("REST request to get User : {}", login);
    User user = userRepository.findOne(login);
    if (user == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return user;
  }

  /**
   * GET /rest/users/:login -> get the "login" user.
   */
  @RequestMapping(value = "/rest/users/{accountId}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public User getUserByAccountId(@PathVariable String accountId, HttpServletResponse response) {
    log.debug("REST request to get User : {}", accountId);
    User user = userRepository.findByAccountId(accountId);
    if (user == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return user;
  }
  
  
  @RequestMapping(value = "/rest/users", method = RequestMethod.POST,
      produces = "application/json")
  @Timed
  public User createUser( @RequestBody User user, HttpServletResponse response) {
    log.debug("REST request to get User : {}", user.getName());
    return user;
  }

}
