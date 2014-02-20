package com.cloudezz.houston.deployer.docker.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Info {

	@JsonProperty("Containers")
	private Integer containers;
	@JsonProperty("Debug")
	private Integer debug;
	@JsonProperty("Driver")
	private String driver;
	@JsonProperty("DriverStatus")
	private List<List<String>> driverStatus = new ArrayList<List<String>>();
	@JsonProperty("IPv4Forwarding")
	private Integer IPv4Forwarding;
	@JsonProperty("Images")
	private Integer images;
	@JsonProperty("IndexServerAddress")
	private String indexServerAddress;
	@JsonProperty("InitPath")
	private String initPath;
	@JsonProperty("InitSha1")
	private String initSha1;
	@JsonProperty("KernelVersion")
	private String kernelVersion;
	@JsonProperty("LXCVersion")
	private String LXCVersion;
	@JsonProperty("MemoryLimit")
	private Integer memoryLimit;
	@JsonProperty("NEventsListener")
	private Integer nEventsListener;
	@JsonProperty("NFd")
	private Integer NFd;
	@JsonProperty("NGoroutines")
	private Integer nGoroutines;
	@JsonProperty("SwapLimit")
	private Integer swapLimit;
	
	@SuppressWarnings("unused")
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("Containers")
	public Integer getContainers() {
	return containers;
	}

	@JsonProperty("Containers")
	public void setContainers(Integer containers) {
	this.containers = containers;
	}

	@JsonProperty("Debug")
	public Integer getDebug() {
	return debug;
	}

	@JsonProperty("Debug")
	public void setDebug(Integer debug) {
	this.debug = debug;
	}

	@JsonProperty("Driver")
	public String getDriver() {
	return driver;
	}

	@JsonProperty("Driver")
	public void setDriver(String driver) {
	this.driver = driver;
	}

	@JsonProperty("DriverStatus")
	public List<List<String>> getDriverStatus() {
	return driverStatus;
	}

	@JsonProperty("DriverStatus")
	public void setDriverStatus(List<List<String>> driverStatus) {
	this.driverStatus = driverStatus;
	}

	@JsonProperty("IPv4Forwarding")
	public Integer getIPv4Forwarding() {
	return IPv4Forwarding;
	}

	@JsonProperty("IPv4Forwarding")
	public void setIPv4Forwarding(Integer IPv4Forwarding) {
	this.IPv4Forwarding = IPv4Forwarding;
	}

	@JsonProperty("Images")
	public Integer getImages() {
	return images;
	}

	@JsonProperty("Images")
	public void setImages(Integer images) {
	this.images = images;
	}

	@JsonProperty("IndexServerAddress")
	public String getIndexServerAddress() {
	return indexServerAddress;
	}

	@JsonProperty("IndexServerAddress")
	public void setIndexServerAddress(String indexServerAddress) {
	this.indexServerAddress = indexServerAddress;
	}

	@JsonProperty("InitPath")
	public String getInitPath() {
	return initPath;
	}

	@JsonProperty("InitPath")
	public void setInitPath(String initPath) {
	this.initPath = initPath;
	}

	@JsonProperty("InitSha1")
	public String getInitSha1() {
	return initSha1;
	}

	@JsonProperty("InitSha1")
	public void setInitSha1(String initSha1) {
	this.initSha1 = initSha1;
	}

	@JsonProperty("KernelVersion")
	public String getKernelVersion() {
	return kernelVersion;
	}

	@JsonProperty("KernelVersion")
	public void setKernelVersion(String kernelVersion) {
	this.kernelVersion = kernelVersion;
	}

	@JsonProperty("LXCVersion")
	public String getLXCVersion() {
	return LXCVersion;
	}

	@JsonProperty("LXCVersion")
	public void setLXCVersion(String LXCVersion) {
	this.LXCVersion = LXCVersion;
	}

	@JsonProperty("MemoryLimit")
	public Integer getMemoryLimit() {
	return memoryLimit;
	}

	@JsonProperty("MemoryLimit")
	public void setMemoryLimit(Integer memoryLimit) {
	this.memoryLimit = memoryLimit;
	}

	@JsonProperty("NEventsListener")
	public Integer getNEventsListener() {
	return nEventsListener;
	}

	@JsonProperty("NEventsListener")
	public void setNEventsListener(Integer nEventsListener) {
	this.nEventsListener = nEventsListener;
	}

	@JsonProperty("NFd")
	public Integer getNFd() {
	return NFd;
	}

	@JsonProperty("NFd")
	public void setNFd(Integer NFd) {
	this.NFd = NFd;
	}

	@JsonProperty("NGoroutines")
	public Integer getNGoroutines() {
	return nGoroutines;
	}

	@JsonProperty("NGoroutines")
	public void setNGoroutines(Integer NGoroutines) {
	this.nGoroutines = NGoroutines;
	}

	@JsonProperty("SwapLimit")
	public Integer getSwapLimit() {
	return swapLimit;
	}

	@JsonProperty("SwapLimit")
	public void setSwapLimit(Integer swapLimit) {
	this.swapLimit = swapLimit;
	}

	


    @Override
    public String toString() {
        return "Info{" +
                "debug=" + true +
                ", containers=" + containers +
                ", images=" + images +
                ", driver=" + driver +
                ", driverStatus=" + printDriverStatus() +
                ", NFd=" + NFd +
                ", NGoroutines=" + nGoroutines +
                ", memoryLimit=" + memoryLimit +
                ", lxcVersion='" + LXCVersion + '\'' +
                ", nEventListener=" + nEventsListener +
                ", kernelVersion='" + kernelVersion + '\'' +
                ", IPv4Forwarding='" + IPv4Forwarding + '\'' +
                ", IndexServerAddress='" + indexServerAddress + '\'' +
                ", SwapLimit='" + swapLimit + '\'' +
                '}';
    }

	private String printDriverStatus() {
		StringBuffer result = new StringBuffer();
		result.append("[");
		for (List<String> entry : driverStatus) {
			result.append(entry);
			result.append(",");
		}
		result.append("]");
		return result.toString();
	}
}
