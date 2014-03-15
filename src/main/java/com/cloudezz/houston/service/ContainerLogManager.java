package com.cloudezz.houston.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloudezz.houston.deployer.docker.client.DockerContainerLogStreamer;
import com.cloudezz.houston.domain.DockerHostMachine;

@Service
public class ContainerLogManager implements RejectedExecutionHandler {

  private final Logger log = LoggerFactory.getLogger(ContainerLogManager.class);

  Map<String, DockerContainerLogStreamer> containerIdToLogStreamer;

  BlockingQueue<Runnable> worksQueue;

  // Create the ThreadPoolExecutor
  ThreadPoolExecutor executor;

  public ContainerLogManager() {
    containerIdToLogStreamer = new HashMap<String, DockerContainerLogStreamer>();
    worksQueue = new ArrayBlockingQueue<Runnable>(2);
    executor = new ThreadPoolExecutor(3, 50, 10, TimeUnit.SECONDS, worksQueue, this);
    executor.allowCoreThreadTimeOut(true);
  }



  public boolean startLog(String containerId, DockerHostMachine dockerHostMachine) {
    DockerContainerLogStreamer logStreamer =
        new DockerContainerLogStreamer(containerId, dockerHostMachine);
    containerIdToLogStreamer.put(containerId, logStreamer);
    executor.execute(logStreamer);
    return true;
  }

  public boolean stopLog(String containerId) {
    DockerContainerLogStreamer logStreamer = containerIdToLogStreamer.get(containerId);
    if (logStreamer == null)
      return false;

    // request exit
    logStreamer.requestExit();
    return true;
  }

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    log.warn("ContainerLogManager for " + r.toString() + "  has been rejected ! ");
  }

  @PreDestroy
  public void destroy() {
    // stop all
    for (Iterator<String> iterator = containerIdToLogStreamer.keySet().iterator(); iterator
        .hasNext();) {
      stopLog(iterator.next());
    }
    worksQueue.clear();
    executor.shutdownNow();
    containerIdToLogStreamer.clear();
  }
}
