package com.cloudezz.houston.config;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;

@Configuration
public class LogStreamSocketIOServerConfig implements EnvironmentAware {

  private static final String REVERSE_PROXY_SERVER = "log-socket-server.";

  private static final String PROP_PORT = "port";
  private static final String PROP_HTTPS = "https";
  private static final String PROP_KEY_STORE = "keystore";
  private static final String PROP_KEY_STORE_PASSWORD = "keystore-password";

  private static final String DEFAULT_HOST = "0.0.0.0";
  private static final Integer DEFAULT_PORT = 81;
  private static final boolean DEFAULT_HTTPS = false;
  private static final String DEFAULT_KEY_STORE = "/keystore.jks";
  private static final String DEFAULT_KEY_STORE_PASSWORD = "simple";



  private final Logger log = LoggerFactory.getLogger(ReverseProxyConfiguration.class);

  private RelaxedPropertyResolver propertyResolver;

  public LogStreamSocketIOServerConfig() {}

  @Override
  public void setEnvironment(Environment environment) {
    this.propertyResolver = new RelaxedPropertyResolver(environment, REVERSE_PROXY_SERVER);
  }


  @Bean
  public SocketIOServer logSocketServer() {
    log.debug("Configuring log stream socket server");
    int port = propertyResolver.getProperty(PROP_PORT, Integer.class, DEFAULT_PORT);
    boolean https = propertyResolver.getProperty(PROP_HTTPS, Boolean.class, DEFAULT_HTTPS);
    String keyStore = propertyResolver.getProperty(PROP_KEY_STORE, DEFAULT_KEY_STORE);
    String keyStorePassword =
        propertyResolver.getProperty(PROP_KEY_STORE_PASSWORD, DEFAULT_KEY_STORE_PASSWORD);

    com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
    config.setHostname(DEFAULT_HOST);
    config.setPort(port);
    config.setBossThreads(5);
    config.setWorkerThreads(250);
    config.setAuthorizationListener(new AuthorizationListener() {

      @Override
      public boolean isAuthorized(HandshakeData handshakeData) {
        return true; // only for dev purpose 
//        return SecurityUtils.isAuthenticated();
      }
    });

    if (https) {
      config.setKeyStorePassword(keyStorePassword);
      InputStream stream = LogStreamSocketIOServerConfig.class.getResourceAsStream(keyStore);
      config.setKeyStore(stream);
    }

    final SocketIOServer server = new SocketIOServer(config);
    return server;

  }

}
