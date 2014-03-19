package com.cloudezz.houston.service;

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
import com.corundumstudio.socketio.annotation.OnMessage;

@Service
public class LogStreamSocketIOService {

  private final Logger log = LoggerFactory.getLogger(LogStreamSocketIOService.class);

  
  @Inject
  private SocketIOServer server;

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
    log.info("New Socket IO client for Log Stream connected sessionid " +client.getSessionId());
    for (int i = 0; i < 100; i++) {
      client.sendJsonObject("TEst data " + i);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @OnDisconnect
  public void onDisconnectHandler(SocketIOClient client) {
    log.info("Socket IO client diconnected sessionid" +client.getSessionId());
    
  }



  // only data object is required in arguments,
  // SocketIOClient and AckRequest could be ommited
  @OnMessage
  public void onSomeEventHandler(SocketIOClient client, String data, AckRequest ackRequest) {
    System.out.println(data);
  }

}
