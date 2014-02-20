package com.cloudezz.houston.web.rest;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.app.VelocityEngine;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.avis.AvisClient;
import com.bbytes.avis.AvisClientException;
import com.bbytes.avis.NotificationData;
import com.bbytes.avis.NotificationRequest;
import com.bbytes.avis.NotificationType;
import com.bbytes.avis.data.EmailData;
import com.cloudezz.houston.domain.PersistentToken;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.PersistentTokenRepository;
import com.cloudezz.houston.service.UserService;
import com.cloudezz.houston.web.rest.dto.UserDTO;
import com.codahale.metrics.annotation.Timed;

/**
 * @author Dhanush Gopinath
 * @version 1.0.0
 * 
 */
@RestController
@RequestMapping("/app")
public class SignUpResource {

  private final Logger log = LoggerFactory.getLogger(SignUpResource.class);

  @Inject
  private UserService userService;

  @Autowired
  private AvisClient avis;

  @Value("${queue.email.request}")
  private String queueName;

  private SecureRandom random = new SecureRandom();

  @Inject
  private PersistentTokenRepository persistentTokenRepository;

  @Autowired
  private VelocityEngine velocityEngine;

  private static final int DEFAULT_SERIES_LENGTH = 16;

  private static final int DEFAULT_TOKEN_LENGTH = 16;



  @RequestMapping(value = "/rest/signup", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Timed
  public void signUp(@RequestBody UserDTO userDTO, HttpServletRequest request) {
    User user = userService.registerUser(userDTO.getEmail());
    PersistentToken token = new PersistentToken();
    token.setSeries(generateSeriesData());
    token.setUser(user);
    token.setTokenValue(generateTokenData());
    token.setTokenDate(new LocalDate());
    token.setIpAddress(request.getRemoteAddr());
    token.setUserAgent(request.getHeader("User-Agent"));
    try {
      persistentTokenRepository.saveAndFlush(token);
    } catch (DataAccessException e) {
      log.error("Failed to save persistent token ", e);
    }
    // Send Email Using Avis
    try {
      sendNotificationToCustomer();
    } catch (AvisClientException e) {
      log.error(e.getMessage());
    }
  }


  private String generateSeriesData() {
    byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
    random.nextBytes(newSeries);
    return new String(Base64.encode(newSeries));
  }

  private String generateTokenData() {
    byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
    random.nextBytes(newToken);
    return new String(Base64.encode(newToken));
  }


  private void sendNotificationToCustomer() throws AvisClientException {

    User user = userService.getUserWithAuthorities();

    EmailData data = new EmailData();
    data.setHtmlEmail(true);
    data.setTo(new String[] {user.getEmail()});
    data.setSubject("Confirm your account on Cloudezz");
    data.setFrom("noreply@cloudezz.com");
    // https://localhost:8090/account/accept/$accountId/$token

    Map<String, Object> properties = new HashMap<String, Object>();
    String url =
        "https://localhost:8090/account/accept/" + user.getAccountId() + "/"
            + user.getPersistentTokens().iterator().next().getTokenValue();
    properties.put("url", url);

    String body =
        VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/signup-email.vm",
            "UTF-8", properties);
    data.setText(body);
    data.setSentDate(new Date());

    NotificationRequest request = new NotificationRequest();
    request.setId(UUID.randomUUID().toString());
    request.setNotificationType(NotificationType.EMAIL);
    request.setQueueName(queueName);
    NotificationData<String, Serializable> requestData = new NotificationData<>();
    requestData.put(NotificationType.EMAIL.toString(), data);
    request.setData(requestData);

    log.debug(String.format("Email Request send to the queue %s", queueName));
    avis.sendNotification(request);
  }
}
