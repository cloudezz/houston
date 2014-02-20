package com.cloudezz.houston.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.cloudezz.houston.deployer.docker.model.Container;

public class BaseCloudezzImageConfig {

	private Container container;

	private DockerHostMachine dockerHostMachine;

	private String dockerImageName;

	private String hostName;

	private String[] port;

	private Long memory;

	private Long memorySwap;

	private Integer cpuShares;

	private Map<String, String> hostToDockerVolumeMapping = new HashMap<String, String>();

	private Map<String, String> environmentMapping = new HashMap<String, String>();

	private Boolean daemon;

	private Boolean tty;

	/**
	 * @return the container
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * @param container
	 *            the container to set
	 */
	public void setContainer(Container container) {
		this.container = container;
	}

	/**
	 * @return the dockerHostMachine
	 */
	public DockerHostMachine getDockerHostMachine() {
		return dockerHostMachine;
	}

	/**
	 * @param dockerHostMachine
	 *            the dockerHostMachine to set
	 */
	public void setDockerHostMachine(DockerHostMachine dockerHostMachine) {
		this.dockerHostMachine = dockerHostMachine;
	}

	/**
	 * @return the dockerImageName
	 */
	public String getDockerImageName() {
		return dockerImageName;
	}

	/**
	 * @param dockerImageName
	 *            the dockerImageName to set
	 */
	public void setDockerImageName(String dockerImageName) {
		this.dockerImageName = dockerImageName;
	}

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName
	 *            the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	

	/**
	 * @return the port
	 */
	public String[] getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String[] port) {
		this.port = port;
	}

	/**
	 * @return the memory
	 */
	public Long getMemory() {
		return memory;
	}

	/**
	 * @param memory
	 *            the memory to set
	 */
	public void setMemory(Long memory) {
		this.memory = memory;
	}

	/**
	 * @return the hostToDockervolumeMapping
	 */
	public Map<String, String> getHostToDockerVolumeMapping() {
		return hostToDockerVolumeMapping;
	}

	/**
	 * @param hostToDockervolumeMapping
	 *            the hostToDockervolumeMapping to set
	 */
	public void setHostToDockerVolumeMapping(Map<String, String> hostToDockervolumeMapping) {
		this.hostToDockerVolumeMapping = hostToDockervolumeMapping;
	}

	/**
	 * @param hostToDockervolumeMapping
	 *            the hostToDockervolumeMapping to set
	 */
	public void addHostToDockerVolumeMapping(String hostVolume, String dockerVolume) {
		this.hostToDockerVolumeMapping.put(hostVolume, dockerVolume);
	}

	/**
	 * @return the environmentMapping
	 */
	public Map<String, String> getEnvironmentMapping() {
		return environmentMapping;
	}

	/**
	 * @param environmentMapping
	 *            the environmentMapping to set
	 */
	public void setEnvironmentMapping(Map<String, String> environmentMapping) {
		this.environmentMapping = environmentMapping;
	}

	/**
	 * @param environmentMapping
	 *            the environmentMapping to set
	 */
	public void addEnvironmentMapping(String envName, String envValue) {
		this.environmentMapping.put(envName, envValue);
	}

	/**
	 * @return the daemon
	 */
	public Boolean getDaemon() {
		return daemon;
	}

	/**
	 * @param daemon
	 *            the daemon to set
	 */
	public void setDaemon(Boolean daemon) {
		this.daemon = daemon;
	}

	/**
	 * @return the tty
	 */
	public Boolean getTty() {
		return tty;
	}

	/**
	 * @param tty
	 *            the tty to set
	 */
	public void setTty(Boolean tty) {
		this.tty = tty;
	}

	/**
	 * @return the memorySwap
	 */
	public Long getMemorySwap() {
		return memorySwap;
	}

	/**
	 * @param memorySwap
	 *            the memorySwap to set
	 */
	public void setMemorySwap(Long memorySwap) {
		this.memorySwap = memorySwap;
	}

	/**
	 * @return the cpuShares
	 */
	public Integer getCpuShares() {
		return cpuShares;
	}

	/**
	 * @param cpuShares
	 *            the cpuShares to set
	 */
	public void setCpuShares(Integer cpuShares) {
		this.cpuShares = cpuShares;
	}

	/**
	 * @return the volumeMapping
	 */
	public String[] getVolumeMapping() {
		String[] volumeMapping = null;
		if (hostToDockerVolumeMapping != null && hostToDockerVolumeMapping.size() > 0) {
			int index = 0;
			volumeMapping = new String[hostToDockerVolumeMapping.size()];
			for (Iterator<String> iterator = hostToDockerVolumeMapping.keySet().iterator(); iterator.hasNext();) {
				String hostVol = iterator.next();
				String dockerVol = hostToDockerVolumeMapping.get(hostVol);
				String volMap = hostVol + ":" + dockerVol;
				volumeMapping[index] = volMap;
				index++;
			}
		}
		return volumeMapping;
	}

}
