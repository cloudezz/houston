package com.cloudezz.houston.logstream;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import com.cloudezz.houston.domain.DockerHostMachine;

public class ContainerLogStreamWorker implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(ContainerLogStreamWorker.class);

  private volatile boolean exit = false;

  private String containerId;

  private DockerHostMachine dockerHostMachine;

  private LogCacheHolder logCacheHolder;
  
  private SimpMessageSendingOperations messagingTemplate;

  public ContainerLogStreamWorker(final String containerId,
      final DockerHostMachine dockerHostMachine,final LogCacheHolder logCacheHolder, SimpMessageSendingOperations messagingTemplate) {
    this.containerId = containerId;
    this.dockerHostMachine = dockerHostMachine;
    this.logCacheHolder = logCacheHolder;
    this.messagingTemplate=messagingTemplate;
  }

  @Override
  public void run() {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    String url =
        String.format("%s/containers/%s/attach?logs=1&stream=1&stdout=1",
            dockerHostMachine.getDockerDaemonURL(), containerId);
    CloseableHttpResponse httpResponse = null;
    try {
      HttpPost httpPostRequest = new HttpPost(url);
      httpResponse = httpClient.execute(httpPostRequest);
      HttpEntity entity = httpResponse.getEntity();

      byte[] buffer = new byte[1024];
      if (entity != null) {
        InputStream inputStream = entity.getContent();
        try {
          int bytesRead = 0;
          BufferedInputStream bis = new BufferedInputStream(inputStream);
          while ((bytesRead = bis.read(buffer)) != -1) {
            if (exit) {
              return;
            }
            String chunk = new String(buffer, 0, bytesRead);
            System.out.print(chunk);
            pushLogToWebSocket(chunk);
          }
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        } finally {
          try {
            inputStream.close();
          } catch (Exception ignore) {
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      try {
        if (httpResponse != null) {
          httpResponse.close();
        }
        httpClient.close();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }

  }

  private void pushLogToWebSocket(final String chunk) {
    logCacheHolder.addData(containerId, chunk);
    messagingTemplate.convertAndSend("/topic/log/"+containerId, chunk);
  }

  public void requestExit() {
    exit = true;
    logCacheHolder.removeData(containerId);
    messagingTemplate.convertAndSend("/topic/log/"+containerId, "Shutting down...\r\n");
  }

  public String toString() {
    return "Container " + containerId;
  }

}
