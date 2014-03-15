package com.cloudezz.houston.deployer.docker.client;


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

import com.cloudezz.houston.domain.DockerHostMachine;

public class DockerContainerLogStreamer implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(DockerContainerLogStreamer.class);

  private volatile boolean exit = false;

  private String containerId;

  private DockerHostMachine dockerHostMachine;

  public DockerContainerLogStreamer() {

  }

  public DockerContainerLogStreamer(final String containerId,
      final DockerHostMachine dockerHostMachine) {
    this.containerId = containerId;
    this.dockerHostMachine = dockerHostMachine;

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

      System.out.println("----------------------------------------");
      System.out.println(httpResponse.getStatusLine());
      System.out.println("----------------------------------------");

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
            System.out.println(chunk);
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

  public void requestExit() {
    exit = true;
  }

  public String toString() {
    return "Container " + containerId;
  }

}
