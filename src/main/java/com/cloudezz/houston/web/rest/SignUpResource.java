package com.cloudezz.houston.web.rest;

import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.cloudezz.houston.domain.UserSignUpActivationKey;
import com.cloudezz.houston.repository.PersistentTokenRepository;
import com.cloudezz.houston.repository.RepositoryUtils;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.repository.UserSignUpActivationKeyRepository;
import com.cloudezz.houston.service.UserService;
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
  
  @Autowired
  private JavaMailSender mailSender;

  @Value("${queue.email.request}")
  private String queueName;

  private SecureRandom random = new SecureRandom();

  @Inject
  private PersistentTokenRepository persistentTokenRepository;

  @Inject
  private UserSignUpActivationKeyRepository userSignUpActivationKeyRepository;

  @Autowired
  private VelocityEngine velocityEngine;

  private static final int DEFAULT_SERIES_LENGTH = 16;

  private static final int DEFAULT_TOKEN_LENGTH = 16;

  @Inject
  private UserRepository userRepository;

  @RequestMapping(value = "/rest/signup", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Timed
  public void signUp(@RequestParam(value = "email") String email, HttpServletRequest request) {

    if (email == null || email.isEmpty()) {
      log.error("Email Id is Empty");
      return;
    }
    User user = userService.registerUser(email);
    // PersistentToken token = new PersistentToken();
    // token.setSeries(generateSeriesData());
    // token.setUser(user);
    // token.setTokenValue(generateTokenData());
    // token.setTokenDate(new LocalDate());
    // token.setIpAddress(request.getRemoteAddr());
    // token.setUserAgent(request.getHeader("User-Agent"));
    // try {
    // persistentTokenRepository.saveAndFlush(token);
    // } catch (DataAccessException e) {
    // log.error("Failed to save persistent token ", e);
    // }

    // Send Email Using Avis
    try {
      sendNotificationToCustomer(user);
    } catch (AvisClientException e) {
      log.error(e.getMessage());
    }
  }


  @RequestMapping(value = "/signup/accept/{accountId}/{key}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Timed
  public boolean acceptRegistration(@PathVariable(value = "accountId") String accountId, @PathVariable(
      value = "key") String key, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    User user = userRepository.findByAccountId(accountId);
    if (user == null) {
      log.error(String.format("No User identified by id %s", accountId));
      return false;
    }
    UserSignUpActivationKey actKey = userSignUpActivationKeyRepository.findOne(user.getEmail());
    if (actKey == null) {
      log.error(String.format("No Activation key identified by email %s and account %s",
          user.getEmail(), accountId));
      return false;
    }
    // if the click is within a day then activate the user
   
    LocalDateTime now =  LocalDateTime.now();
    Period p = Period.fieldDifference(now, actKey.getSignUpDate());
    //less that one day
    long actPeriodInMillis = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
    if (p.getMillis()<= actPeriodInMillis) {

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
      Set<PersistentToken> persistentTokens = new HashSet<>();
      persistentTokens.add(token);
      user.setPersistentTokens(persistentTokens);
      userRepository.saveAndFlush(user);
      // delete the activation key
//      userSignUpActivationKeyRepository.delete(actKey);
      response.sendRedirect("/#/setpassword/"+accountId);
      return true;

    } else {
      response.sendRedirect("/#/signup");
    }
    return true;
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


  private void sendNotificationToCustomer(User user) throws AvisClientException {

    EmailData data = new EmailData();
    data.setHtmlEmail(true);
    data.setTo(new String[] {user.getEmail()});
    data.setSubject("Confirm your account on Cloudezz");
    data.setFrom("noreply@cloudezz.com");
    // https://localhost:8090/account/accept/$accountId/$token

    String activationKey = RepositoryUtils.generateBigId();
    UserSignUpActivationKey userSignUpActivationKey = new UserSignUpActivationKey();
    userSignUpActivationKey.setId(user.getEmail());
    userSignUpActivationKey.setActivationKey(activationKey);
    userSignUpActivationKey.setSignUpDate(LocalDateTime.now());
    userSignUpActivationKey = userSignUpActivationKeyRepository.save(userSignUpActivationKey);

    if (userSignUpActivationKey == null)
      throw new PersistenceException(
          "Could not persist the activation key to database during user signup");



    Map<String, Object> properties = new HashMap<String, Object>();
    String url =
        "http://localhost:8090/app/signup/accept/" + user.getAccountId() + "/" + activationKey;
    properties.put("url", url);

    String body =
        VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "templates/signup-email.vm",
            "UTF-8", properties);
    data.setText(body);
    data.setSentDate(new Date());


    NotificationRequest request = new NotificationRequest();
    request.setId(userSignUpActivationKey.getActivationKey());
    request.setNotificationType(NotificationType.EMAIL);
    request.setQueueName(queueName);
    NotificationData<String, Serializable> requestData = new NotificationData<>();
    requestData.put(NotificationType.EMAIL.toString(), data);
    request.setData(requestData);

    log.debug(String.format("Email Request send to the queue %s", queueName));
    try {
      avis.sendNotification(request);
    }
    catch(Exception e) {
      log.error("Exception in Sending Email via Avis. Sending directly.");
      sendHtmlEmail(data);
    }
  }
  
  private void sendHtmlEmail(final EmailData emailData) {
      MimeMessagePreparator preparator = new MimeMessagePreparator() {
          public void prepare(MimeMessage mimeMessage) throws Exception {
              MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
              message.setTo(emailData.getTo());
              if (emailData.getFrom() != null) {
                  message.setFrom(emailData.getFrom());
              }
              if (emailData.getCc() != null && emailData.getCc().length > 0) {
                  message.setCc(emailData.getCc());
              }
              if (emailData.getBcc() != null && emailData.getBcc().length > 0) {
                  message.setBcc(emailData.getBcc());
              }
              message.setSentDate(new Date());
              message.setSubject(emailData.getSubject());
              mimeMessage.setContent(emailData.getText(), "text/html");
          }
      };
      try {
        mailSender.send(preparator);
      }catch (Exception e) {
        log.error("Exception in Sending Email. Cant send email");
        e.printStackTrace();
      }
  }
  
}
