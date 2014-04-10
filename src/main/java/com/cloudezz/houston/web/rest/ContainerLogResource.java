package com.cloudezz.houston.web.rest;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.cloudezz.houston.logstream.LogCacheHolder;


@Controller
public class ContainerLogResource {

  private final Logger log = LoggerFactory.getLogger(ContainerLogResource.class);

  @Autowired
  private LogCacheHolder logCacheHolder;

  @SubscribeMapping("/init/{containerId}")
  public String containerLogInitMessage(@DestinationVariable String containerId, Principal principal) {
    log.debug("Sending init message");
    if (((Authentication) principal).isAuthenticated()) {
      return logCacheHolder.getData(containerId);
    }
    return "";
  }

}
