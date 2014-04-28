package com.cloudezz.houston.deployer.docker.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerConfig {

	@JsonProperty("Hostname")     private String    hostName = "";
    @JsonProperty("Name")         private String    name;
    @JsonProperty("Domainname")   private String    domainName = "";
    @JsonProperty("PortSpecs")    private String[]  portSpecs;
    @JsonProperty("ExposedPorts") private Map<String, Map<String, HostPortBinding>> exposedPorts = new HashMap<String, Map<String, HostPortBinding>>();
    @JsonProperty("User")         private String    user = "";
    @JsonProperty("Tty")          private Boolean   tty = false;
    @JsonProperty("OpenStdin")    private Boolean   stdinOpen = false;
    @JsonProperty("StdinOnce")    private Boolean   stdInOnce = false;
    @JsonProperty("Memory")       private Long      memoryLimit = 0L;
    @JsonProperty("MemorySwap")   private Long      memorySwap = 0L;
    @JsonProperty("CpuShares")    private Integer   cpuShares = 0;
    @JsonProperty("AttachStdin")  private Boolean   attachStdin = false;
    @JsonProperty("AttachStdout") private Boolean   attachStdout = false;
    @JsonProperty("AttachStderr") private Boolean   attachStderr = false;
    @JsonProperty("Env")          private String[]  env;
    @JsonProperty("Cmd")          private String[]  cmd;
    @JsonProperty("Dns")          private String[]  dns;
    @JsonProperty("Image")        private String    image;
    @JsonProperty("Volumes")      private Object    volumes;
    @JsonProperty("VolumesFrom")  private String    volumesFrom = "";
    @JsonProperty("Entrypoint")   private String[]  entrypoint = new String[]{};
    @JsonProperty("NetworkDisabled") private Boolean networkDisabled = false;
    @JsonProperty("Privileged")   private Boolean privileged = false;
    @JsonProperty("WorkingDir")   private String workingDir = "";


    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public Boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(Boolean privileged) {
        this.privileged = privileged;
    }

    public Boolean getNetworkDisabled() {
        return networkDisabled;
    }

    public void setNetworkDisabled(boolean networkDisabled) {
        this.networkDisabled = networkDisabled;
    }

    public String getHostName() {
    	return hostName;
    }
    
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDomainName() {
    	return domainName;
    }
    
    public void setDomainName(String domainName) {
    	this.domainName = domainName;
    }
    
    public String[] getPortSpecs() {
        return portSpecs;
    }

    public void setPortSpecs(String[] portSpecs) {
        this.portSpecs = portSpecs;
    }

    public Map<String, Map<String, HostPortBinding>> getExposedPorts() {
    	return exposedPorts;
    }
    
    public void setExposedPorts(Map<String, Map<String, HostPortBinding>> exposedPorts) {
    	this.exposedPorts = exposedPorts;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean isTty() {
        return tty;
    }

    public void setTty(Boolean tty) {
        this.tty = tty;
    }

    public Boolean isStdinOpen() {
        return stdinOpen;
    }

    public void setStdinOpen(Boolean stdinOpen) {
        this.stdinOpen = stdinOpen;
    }

    public Boolean isStdInOnce() {
        return stdInOnce;
    }

    public void setStdInOnce(Boolean stdInOnce) {
        this.stdInOnce = stdInOnce;
    }

    public Long getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Long memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public Long getMemorySwap() {
        return memorySwap;
    }

    public void setMemorySwap(Long memorySwap) {
        this.memorySwap = memorySwap;
    }

    public Integer getCpuShares() {
        return cpuShares;
    }

    public void setCpuShares(Integer cpuShares) {
        this.cpuShares = cpuShares;
    }

    public boolean isAttachStdin() {
        return attachStdin;
    }

    public void setAttachStdin(boolean attachStdin) {
        this.attachStdin = attachStdin;
    }

    public Boolean isAttachStdout() {
        return attachStdout;
    }

    public void setAttachStdout(Boolean attachStdout) {
        this.attachStdout = attachStdout;
    }

    public Boolean isAttachStderr() {
        return attachStderr;
    }

    public void setAttachStderr(Boolean attachStderr) {
        this.attachStderr = attachStderr;
    }

    public String[] getEnv() {
        return env;
    }

    public void setEnv(String[] env) {
        this.env = env;
    }

    public String[] getCmd() {
        return cmd;
    }

    public void setCmd(String[] cmd) {
        this.cmd = cmd;
    }

    public String[] getDns() {
        return dns;
    }

    public void setDns(String[] dns) {
        this.dns = dns;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Object getVolumes() {
        return volumes;
    }

    public void setVolumes(Object volumes) {
        this.volumes = volumes;
    }

    public String getVolumesFrom() {
        return volumesFrom;
    }

    public void setVolumesFrom(String volumesFrom) {
        this.volumesFrom = volumesFrom;
    }

    public String[] getEntrypoint() {
        return entrypoint;
    }

    public void setEntrypoint(String[] entrypoint) {
        this.entrypoint = entrypoint;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ContainerConfig [hostName=" + hostName + ", name=" + name + ", domainName=" + domainName
				+ ", portSpecs=" + Arrays.toString(portSpecs) + ", exposedPorts=" + exposedPorts + ", user=" + user
				+ ", tty=" + tty + ", stdinOpen=" + stdinOpen + ", stdInOnce=" + stdInOnce + ", memoryLimit="
				+ memoryLimit + ", memorySwap=" + memorySwap + ", cpuShares=" + cpuShares + ", attachStdin="
				+ attachStdin + ", attachStdout=" + attachStdout + ", attachStderr=" + attachStderr + ", env="
				+ Arrays.toString(env) + ", cmd=" + Arrays.toString(cmd) + ", dns=" + Arrays.toString(dns) + ", image="
				+ image + ", volumes=" + volumes + ", volumesFrom=" + volumesFrom + ", entrypoint="
				+ Arrays.toString(entrypoint) + ", networkDisabled=" + networkDisabled + ", privileged=" + privileged
				+ ", workingDir=" + workingDir + "]";
	}

}
