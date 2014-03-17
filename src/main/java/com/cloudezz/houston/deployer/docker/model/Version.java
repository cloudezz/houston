package com.cloudezz.houston.deployer.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Version {

	@JsonProperty("Version")
	public String version;

	@JsonProperty("KernelVersion")
	public String kernelVersion;

	@JsonProperty("GitCommit")
	public String gitCommit;

	@JsonProperty("GoVersion")
	public String goVersion;

	@JsonProperty("Arch")
	public String arch;
	
	@JsonProperty("Os")
	public String Os;

	@Override
	public String toString() {
		return "Version{" + "version='" + version + '\'' + ", gitCommit='"
				+ gitCommit + '\'' + ", goVersion='" + goVersion + '\''
				+ ", kernelVersion='" + kernelVersion + '\'' + '}';
	}

}
