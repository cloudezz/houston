package com.cloudezz.houston.web.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.Authority;
import com.cloudezz.houston.domain.PersistentToken;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.PersistentTokenRepository;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.repository.UserSignUpActivationKeyRepository;
import com.cloudezz.houston.security.SecurityUtils;
import com.cloudezz.houston.service.UserService;
import com.cloudezz.houston.web.rest.dto.UserDTO;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class AccountResource {

  private final Logger log = LoggerFactory.getLogger(AccountResource.class);

  @Inject
  private UserRepository userRepository;

  @Inject
  private UserSignUpActivationKeyRepository userSignUpActivationKeyRepository;

  @Autowired
  private UserDetailsService userDetailsService;

  @Inject
  private UserService userService;

  @Inject
  private PersistentTokenRepository persistentTokenRepository;

  /**
   * GET /rest/authenticate -> check if the user is authenticated, and return its login.
   */
  @RequestMapping(value = "/rest/authenticate", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public String isAuthenticated(HttpServletRequest request) {
    log.debug("REST request to check if the current user is authenticated");
    return request.getRemoteUser();
  }

  /**
   * GET /rest/account -> get the current user.
   */
  @RequestMapping(value = "/rest/account", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public UserDTO getAccount(HttpServletResponse response) {
    User user = userService.getUserWithAuthorities();
    if (user == null) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return null;
    }
    Map<String, Boolean> roles = new HashMap<>();
    for (Authority authority : user.getAuthorities()) {
      roles.put(authority.getId(), Boolean.TRUE);
    }
    return new UserDTO(user.getLogin(), user.getFirstName(), user.getLastName(), user.getEmail(),
        roles);
  }

  /**
   * POST /rest/account -> update the current user information.
   */
  @RequestMapping(value = "/rest/account", method = RequestMethod.POST,
      produces = "application/json")
  @Timed
  public void saveAccount(@RequestBody UserDTO userDTO) throws IOException {
    userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(),
        userDTO.getEmail());
  }

  /**
   * POST /rest/change_password -> changes the current user's password
   */
  @RequestMapping(value = "/rest/account/change_password", method = RequestMethod.POST,
      produces = "application/json")
  @Timed
  public void changePassword(@RequestBody String password, HttpServletResponse response)
      throws IOException {
    if (password == null || password.equals("")) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Password should not be empty");
    } else {
      userService.changePassword(password);
    }
  }


  /**
   * POST /rest/change_password -> changes the current user's password
   */
  @RequestMapping(value = "/rest/account/set_password/{accountId}", method = RequestMethod.POST,
      produces = "application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Timed
  public void setPasswordForNewUser(@PathVariable(value = "accountId") String accountId,
      @RequestBody String password, HttpServletResponse response) throws IOException {
    if (password == null || password.equals("")) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Password should not be empty");
    } else {

      User user = userRepository.findByAccountId(accountId);
      if (user == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "No user for that id");
      }
      userService.changePassword(user.getEmail(), password);
      user.setActive(true);
      user = userRepository.saveAndFlush(user);
      
      // use in your method
      UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
      Authentication auth =
          new UsernamePasswordAuthenticationToken(userDetails,
              userDetails.getPassword(), userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);
      // delete TODO: Enable this later
//      userSignUpActivationKeyRepository.delete(user.getEmail());

      response.sendRedirect("/#/appimagecfg");
    }
  }


  /**
   * GET /rest/account/sessions -> get the current open sessions.
   */
  @RequestMapping(value = "/rest/account/sessions", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public List<PersistentToken> getCurrentSessions(HttpServletResponse response) {
    User user = userRepository.findOne(SecurityUtils.getCurrentLogin());
    if (user == null) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return persistentTokenRepository.findByUser(user);
  }

  /**
   * DELETE /rest/account/sessions?series={series} -> invalidate an existing session.
   */
  @RequestMapping(value = "/rest/account/sessions/{series}", method = RequestMethod.DELETE)
  @Timed
  public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
    String decodedSeries = URLDecoder.decode(series, "UTF-8");
    persistentTokenRepository.delete(decodedSeries);
  }
}
