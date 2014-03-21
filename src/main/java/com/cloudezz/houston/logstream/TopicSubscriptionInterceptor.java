package com.cloudezz.houston.logstream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;



public class TopicSubscriptionInterceptor extends ChannelInterceptorAdapter implements
    ChannelInterceptor {

  private final Logger log = LoggerFactory.getLogger(TopicSubscriptionInterceptor.class);

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())
        && headerAccessor.getUser() != null
        && headerAccessor.getUser() instanceof UsernamePasswordAuthenticationToken) {
      if (!loggedIn((UsernamePasswordAuthenticationToken) headerAccessor.getUser())) {
        throw new IllegalArgumentException("Login to view log message");
      }
      UsernamePasswordAuthenticationToken userToken =
          (UsernamePasswordAuthenticationToken) headerAccessor.getUser();
      if (!validateSubscription((User) userToken.getPrincipal(), headerAccessor.getDestination())) {
        throw new IllegalArgumentException("No permission to view log message");
      }
    }
    return message;
  }

  private boolean validateSubscription(User principal, String topicDestination) {
    log.debug("Validate subscription for {} to topic {}", principal.getUsername(), topicDestination);
    // verify if the user email id has permission to view the log of this container ..he has to be
    // the owner of the container
    return true;
  }


  private boolean loggedIn(UsernamePasswordAuthenticationToken authToken) {
    if (authToken == null)
      return false;

    return authToken.isAuthenticated();
  }



}
