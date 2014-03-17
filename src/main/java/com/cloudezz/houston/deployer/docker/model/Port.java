package com.cloudezz.houston.deployer.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Port {

    @JsonProperty("IP")
    private String ip;
    @JsonProperty("PrivatePort")
    private int privatePort;
    @JsonProperty("PublicPort")
    private int publicPort;
    @JsonProperty("Type")
    private String type;
    public String getIp() {
      return ip;
    }
    public void setIp(String ip) {
      this.ip = ip;
    }
    public int getPrivatePort() {
      return privatePort;
    }
    public void setPrivatePort(int privatePort) {
      this.privatePort = privatePort;
    }
    public int getPublicPort() {
      return publicPort;
    }
    public void setPublicPort(int publicPort) {
      this.publicPort = publicPort;
    }
    public String getType() {
      return type;
    }
    public void setType(String type) {
      this.type = type;
    }


   
}
