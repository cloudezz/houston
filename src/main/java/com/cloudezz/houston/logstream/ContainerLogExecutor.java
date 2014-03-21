package com.cloudezz.houston.logstream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.cloudezz.houston.domain.DockerHostMachine;

@Service
public class ContainerLogExecutor implements RejectedExecutionHandler {

  private final Logger log = LoggerFactory.getLogger(ContainerLogExecutor.class);

  private Map<String, ContainerLogStreamWorker> containerIdToLogStreamer;

  private BlockingQueue<Runnable> worksQueue;
  
  @Inject
  private LogCacheHolder logCacheHolder;
  
  @Inject
  private SimpMessageSendingOperations  messagingTemplate;

  // Create the ThreadPoolExecutor
  private ThreadPoolExecutor executor;

  public ContainerLogExecutor() {
    containerIdToLogStreamer = new HashMap<String, ContainerLogStreamWorker>();
    worksQueue = new ArrayBlockingQueue<Runnable>(2);
    executor = new ThreadPoolExecutor(3, 50, 10, TimeUnit.SECONDS, worksQueue, this);
    executor.allowCoreThreadTimeOut(true);
  }



  public boolean startLog(String containerId, DockerHostMachine dockerHostMachine) {
    ContainerLogStreamWorker logStreamer =
        new ContainerLogStreamWorker(containerId, dockerHostMachine,logCacheHolder,messagingTemplate);
    containerIdToLogStreamer.put(containerId, logStreamer);
    executor.execute(logStreamer);
    return true;
  }

  public boolean stopLog(String containerId) {
    ContainerLogStreamWorker logStreamer = containerIdToLogStreamer.get(containerId);
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
