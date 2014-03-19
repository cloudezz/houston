package com.cloudezz.houston.logstream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

@Service
public class LogStreamSocketIOService {

  private final Logger log = LoggerFactory.getLogger(LogStreamSocketIOService.class);

  @Inject
  private SocketIOServer server;
  
  @Inject
  private LogCacheHolder logCacheHolder;

  @PostConstruct
  public void init() {
    server.addListeners(this);
    server.start();
  }

  @PreDestroy
  public void destroy() {
    if (server != null)
      server.stop();
  }

  @OnConnect
  public void onConnectHandler(SocketIOClient client) {
    log.debug("New Socket IO client for Log Stream connected sessionid " + client.getSessionId());
  }

  @OnDisconnect
  public void onDisconnectHandler(SocketIOClient client) {
    log.debug("Socket IO client diconnected sessionid " + client.getSessionId());
  }

  @OnEvent("join-log-stream")
  public void onSomeEventHandler(SocketIOClient client, String containerId, AckRequest ackRequest) {
    log.debug("Client with sessionid " + client.getSessionId()
        + " connected to log stream of container  " + containerId);
    client.joinRoom(containerId);
    client.sendEvent("log", logCacheHolder.getData(containerId));
  }



}
