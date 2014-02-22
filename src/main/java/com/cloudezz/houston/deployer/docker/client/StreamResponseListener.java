package com.cloudezz.houston.deployer.docker.client;

public interface StreamResponseListener {

  public void recieve(String data);
  
  public void onError(Throwable throwable);
}
