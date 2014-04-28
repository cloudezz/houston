package com.cloudezz.houston.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.cloudezz.houston.config.rproxy.ProxyEtcReverseProxyClient;
import com.cloudezz.houston.config.rproxy.ReverseProxyClient;

@Configuration
public class ReverseProxyConfiguration implements EnvironmentAware {

  private static final String REVERSE_PROXY_SERVER = "reverse-proxy.";

  private static final String PROP_HOST = "host";
  private static final String PROP_PORT = "port";
  private static final String PROP_PASSWORD = "password";
  private static final String PROP_USERNAME = "username";
  private static final String PROP_HTTPS = "https";

  private static final String DEFAULT_HOST = "apps.cloudezz.com";
  private static final Integer DEFAULT_PORT = 80;

  private static final String DEFAULT_USERNAME = "admin";
  private static final String DEFAULT_PASSWORD = "admin";
  private static final Boolean DEFAULT_HTTPS = false;


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
    String port = propertyResolver.getProperty(PROP_PORT, Integer.class, DEFAULT_PORT).toString();
    String username = propertyResolver.getProperty(PROP_USERNAME, DEFAULT_USERNAME);
    String password =
        propertyResolver.getProperty(PROP_PASSWORD,DEFAULT_PASSWORD);
    Boolean https =
        propertyResolver.getProperty(PROP_HTTPS, Boolean.class,DEFAULT_HTTPS);

    ReverseProxyClient rProxyClient = new ProxyEtcReverseProxyClient(host,port,https,username,password);
    rProxyClient.init();

    // check reverse proxy server conn settings
    if(!rProxyClient.serverReachable()){
      log.warn("Warning! Not able to reach reverse proxy server");
      log.debug("Did you configure your reverse proxy rerver settings in your application.yml?");
    }
    

    return rProxyClient;
  }

 
}
