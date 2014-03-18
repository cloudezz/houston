package com.cloudezz.houston.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnMessage;

@Service
public class LogStreamSocketIOService {

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
    System.out.println("CONNECTED :" + client.getSessionId());
    for (int i = 0; i < 100; i++) {
      client.sendJsonObject("  Can us see me :) " + i);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    
  }

  @OnDisconnect
  public void onDisconnectHandler(SocketIOClient client) {
    
    System.out.println("~~~ DISCONNECTED :" + client.getSessionId());
  }



  // only data object is required in arguments,
  // SocketIOClient and AckRequest could be ommited
  @OnMessage
  public void onSomeEventHandler(SocketIOClient client, String data, AckRequest ackRequest) {
    System.out.println(data);
    client.sendJsonObject(data);
  }

}
