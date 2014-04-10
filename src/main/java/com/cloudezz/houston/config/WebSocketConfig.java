package com.cloudezz.houston.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.cloudezz.houston.logstream.TopicSubscriptionInterceptor;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "com.cloudezz.houston", excludeFilters = @ComponentScan.Filter(
    type = FilterType.ANNOTATION, value = Configuration.class))
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
  
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/log/register/").withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.setInterceptors(new TopicSubscriptionInterceptor());
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic/");
    registry.setApplicationDestinationPrefixes("/sockjs/log");
  }

}
