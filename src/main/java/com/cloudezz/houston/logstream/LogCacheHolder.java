package com.cloudezz.houston.logstream;

import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The call hold the log data for the containers for 5 mins . The log stream request can get the old
 * data to be severed when it joins the first time thru socket io join room option
 * 
 * @author Thanneer
 * @since 1.0.0
 */
@Component
public class LogCacheHolder {

  private Cache<String, String> cache;

  public LogCacheHolder() {
    // expire the cache values after 5 mins
    cache =
        CacheBuilder.newBuilder().maximumSize(50).expireAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterAccess(5, TimeUnit.MINUTES).build();
  }

  public void addData(String containerId, String logData) {
    String data = cache.getIfPresent(containerId);
    if (data == null)
      data = "";

    data = data + logData;
    cache.put(containerId, data);
  }

  public void removeData(String containerId) {
    cache.invalidate(containerId);
  }

  public String getData(String containerId) {
    String data = cache.getIfPresent(containerId);
    if (data == null)
      data = "No log data available...";

    return data;
  }

  @PreDestroy
  public void destroy() {
    if (cache == null)
      return;

    cache.invalidateAll();
    cache.cleanUp();
  }


}
