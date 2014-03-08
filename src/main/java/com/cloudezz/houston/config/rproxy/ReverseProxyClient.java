package com.cloudezz.houston.config.rproxy;

public interface ReverseProxyClient {

  public void init();
  
  public boolean register(String frontendURL, String backendURL);
  
  public boolean serverReachable();
}
