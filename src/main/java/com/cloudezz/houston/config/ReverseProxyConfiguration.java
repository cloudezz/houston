package com.cloudezz.houston.config;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.cloudezz.houston.config.rproxy.HipacheReverseProxyClient;
import com.cloudezz.houston.config.rproxy.ReverseProxyClient;

@Configuration
public class ReverseProxyConfiguration implements EnvironmentAware {

  private static final String REVERSE_PROXY_SERVER = "reverse-proxy.";

  private static final String PROP_HOST = "host";
  private static final String PROP_PORT = "port";
  private static final String PROP_REDIS_PORT = "redis-port";
  private static final String PROP_REDIS_HOST = "redis-host";
  private static final String PROP_REDIS_PASSWORD = "redis-password";

  private static final String DEFAULT_HOST = "apps.cloudezz.com";
  private static final Integer DEFAULT_PORT = 80;

  private static final Integer DEFAULT_REDIS_PORT = 6379;
  private static final String DEFAULT_REDIS_HOST = "apps.cloudezz.com";
  private static final String DEFAULT_REDIS_PASSWORD = "password";


  private final Logger log = LoggerFactory.getLogger(ReverseProxyConfiguration.class);

  private RelaxedPropertyResolver propertyResolver;

  public ReverseProxyConfiguration() {}

  @Override
  public void setEnvironment(Environment environment) {
    this.propertyResolver = new RelaxedPropertyResolver(environment, REVERSE_PROXY_SERVER);
  }


  @Bean
  public ReverseProxyClient reverseProxyClient() {
    log.debug("Configuring reverse proxy client");
    String host = propertyResolver.getProperty(PROP_HOST, DEFAULT_HOST);
    int port = propertyResolver.getProperty(PROP_PORT, Integer.class, DEFAULT_PORT);
    String redisHost = propertyResolver.getProperty(PROP_REDIS_HOST, DEFAULT_REDIS_HOST);
    int redisPort =
        propertyResolver.getProperty(PROP_REDIS_PORT, Integer.class, DEFAULT_REDIS_PORT);
    String redisPassword =
        propertyResolver.getProperty(PROP_REDIS_PASSWORD, DEFAULT_REDIS_PASSWORD);

    HipacheReverseProxyClient rProxyClient = new HipacheReverseProxyClient();
    rProxyClient.setRedisHost(redisHost);
    rProxyClient.setRedisPort(redisPort);
    rProxyClient.setRedisPassword(redisPassword);
    rProxyClient.init();

    // check reverse proxy server conn settings
    if(!urlReachable(host, port)){
      log.warn("Warning! Not able to reach reverse proxy server");
      log.debug("Did you configure your reverse proxy rerver settings in your application.yml?");
    }
    
    // check redis server conn settings
    if (!rProxyClient.serverReachable()) {
      log.warn("Warning! Not able to reach reverse proxy redis server");
      log.debug("Did you configure your reverse proxy server redis settings in your application.yml?");
    }

    return rProxyClient;
  }

  private boolean urlReachable(String host, int port) {
    Socket socket = null;
    boolean reachable = false;
    try {
      socket = new Socket(host, port);
      reachable = true;
    } catch (IOException e) {
      reachable = false;
    } finally {
      if (socket != null)
        try {
          socket.close();
        } catch (IOException e) {
        }
    }
    return reachable;
  }

}
