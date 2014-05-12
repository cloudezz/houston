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

import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.web.rest.dto.UserDTO;
import com.codahale.metrics.annotation.Timed;

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

  @RequestMapping(value = "/rest/users", method = RequestMethod.POST, produces = "application/json")
  @Timed
  public User createUser(@RequestBody UserDTO userDto, HttpServletResponse response) {
    User user = new User();
    user.setFirstName(userDto.getFirstName());
    user.setLastName(userDto.getLastName());
    user.setEmail(userDto.getEmail());
    user.setLogin(userDto.getEmail());
    user = userRepository.saveAndFlush(user);
    return user;
  }

  /**
   * GET /rest/users -> get all the users.
   */
  @RequestMapping(value = "/rest/users", method = RequestMethod.GET, produces = "application/json")
  @Timed
  public List<UserDTO> getAllUsers() {
    log.debug("REST request to get all Users");
    List<UserDTO> userDtos = null;
    try {
      List<User> users = userRepository.findAll();
      userDtos = convertToDtos(users);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return userDtos;
  }

  private List<UserDTO> convertToDtos(List<User> users) {

    List<UserDTO> userDtos = new ArrayList<>();
    for (User user : users) {
      UserDTO userDto = new UserDTO();
      userDto.setEmail(user.getEmail());
      userDto.setFirstName(user.getFirstName());
      userDto.setLastName(user.getLastName());
      userDto.setLogin(user.getLogin());
      userDtos.add(userDto);
    }
    return userDtos;

  }



}
