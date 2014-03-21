package com.cloudezz.houston.config.rproxy;

public interface ReverseProxyClient {

  public void init();
  
  public boolean add(String frontendURL, String backendURL);
  
  public boolean update(String frontendURL, String oldBackendURL, String newBackendURL);
 
  public boolean delete(String frontendURL);
  
  public boolean deleteBackend(String frontendURL,String backendURL);
  
  public boolean serverReachable();
}
