package com.cloudezz.houston.deployer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.model.Container;
import com.cloudezz.houston.deployer.docker.model.ContainerConfig;
import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;
import com.cloudezz.houston.deployer.docker.model.Image;
import com.cloudezz.houston.domain.BaseCloudezzImageConfig;
import com.google.common.base.Preconditions;

/**
 * Util class that does most of the build container logic . All the method are util methos to build the container from config and to stop container 
 * @author Thanneer
 *
 */
public class CloudezzDeployerUtil {

	public void checkAndPullImage(DockerClient dockerClient, String imageTag) throws CloudezzDeployException {
		List<Image> images = dockerClient.getImages(imageTag, true);
		if (images == null || images.isEmpty() || !images.contains(imageTag)) {
			dockerClient.pull(imageTag);
		}
	}

	/**
	 * Convert {@link BaseCloudezzImageConfig} the pojo to
	 * {@link ContainerConfig} understood by docker remote api
	 * 
	 * @param dockerInstance
	 * @return
	 */
	public ContainerConfig getDockerConfigFromCloudezzConfig(BaseCloudezzImageConfig cloudezzImageConfig) {
		Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

		ContainerConfig config = new ContainerConfig();
		config.setImage(cloudezzImageConfig.getDockerImageName());
		config.setTty(cloudezzImageConfig.getTty());
		config.setCpuShares(cloudezzImageConfig.getCpuShares());
		config.setHostName(cloudezzImageConfig.getHostName());
		
	
		String[] ports = cloudezzImageConfig.getPort();
		if(ports!=null && ports.length>0){
			Map<String, Map<String, HostPortBinding>> exposedPorts = new HashMap<String, Map<String,HostPortBinding>>();
			for (int i = 0; i < ports.length; i++) {
				String port = ports[i];
				exposedPorts.put(port, new HashMap<String, HostPortBinding>());
			}
			config.setExposedPorts(exposedPorts);
		}
		

		if (cloudezzImageConfig.getMemory() != null)
			config.setMemoryLimit(cloudezzImageConfig.getMemory() * 1024 * 1024);

		if (cloudezzImageConfig.getMemorySwap() != null)
			config.setMemorySwap(cloudezzImageConfig.getMemorySwap() * 1024 * 1024);

		// set the env variable map
		Map<String, String> envMap = cloudezzImageConfig.getEnvironmentMapping();
		if (envMap != null && envMap.size() > 0) {
			String[] envMapToDocker = new String[envMap.size()];
			int index = 0;
			for (Iterator<String> iterator = envMap.keySet().iterator(); iterator.hasNext();) {
				String envName = iterator.next();
				String envValue = envMap.get(envName);
				String envToDocker = envName + "=" + envValue;
				envMapToDocker[index] = envToDocker;
				index++;
			}

			config.setEnv(envMapToDocker);
		}

		// set the host to docker volume map info
		Map<String, String> volumeMap = cloudezzImageConfig.getHostToDockerVolumeMapping();
		if (volumeMap != null && volumeMap.size() > 0) {
			Map<String, Map<String, String>> volMap = new HashMap<String, Map<String, String>>();
			for (Iterator<String> iterator = volumeMap.keySet().iterator(); iterator.hasNext();) {
				String[] vol = new String[2];
				vol[0] = iterator.next();
				vol[1] = volumeMap.get(vol[0]);
				volMap.put(vol[1], new HashMap<String, String>());
			}
			config.setVolumes(volMap);
		}

		return config;

	}

	/**
	 * Create the container and set it on config object
	 * @param dockerClient
	 * @param cloudezzImageConfig
	 * @return
	 * @throws CloudezzDeployException
	 */
	private boolean createAndSetContainerOnImageConfig(DockerClient dockerClient,
			BaseCloudezzImageConfig cloudezzImageConfig) throws CloudezzDeployException {
		Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
		Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

		Container container = dockerClient
				.createAndGetContainer(getDockerConfigFromCloudezzConfig(cloudezzImageConfig));
		if (container == null)
			return false;

		cloudezzImageConfig.setContainer(container);
		return true;

	}

	/**
	 * Start the container once the container is created or if already created
	 * then jus start it .
	 * 
	 * @param dockerClient
	 * @param cloudezzImageConfig
	 * @return
	 * @throws CloudezzDeployException
	 */
	public boolean startContainer(DockerClient dockerClient, BaseCloudezzImageConfig cloudezzImageConfig)
			throws CloudezzDeployException {
		Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
		Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

		Container container = cloudezzImageConfig.getContainer();
		if (container == null) {
			createAndSetContainerOnImageConfig(dockerClient, cloudezzImageConfig);
			container = cloudezzImageConfig.getContainer();
		}
		
		// note : plz try without host config as arg in test case 
		return dockerClient.startContainer(container.getId(),getHostConfig(cloudezzImageConfig));

	}
	
	/**
	 * Get the host config like port mapping and volume mapping
	 * @param cloudezzImageConfig
	 * @return
	 */
	private HostConfig getHostConfig(BaseCloudezzImageConfig cloudezzImageConfig){
		
		HostConfig hostConfig = new HostConfig();
		hostConfig.setPublishAllPorts(true);

		// port mappings
		Map<String, HostPortBinding[]> portBindings = hostConfig.getPortBindings();
		String portMappings[] = cloudezzImageConfig.getPort();
		if(portMappings!=null){
			for (int i = 0; i < portMappings.length; i++) {
				HostPortBinding[] portBindingForContainerPort = new HostPortBinding[1];
				
			 
				portBindingForContainerPort[0] = new HostPortBinding("0.0.0.0", portMappings[i]);
				portBindings.put(portMappings[i] + "/tcp", portBindingForContainerPort);
			}
			// note : lil doubtful abt this settings as we are setting port for host so commented ..need to test after uncommeting 
			// hostConfig.setPortBindings(portBindings);
			
		}
		
		// volume mapping with read write access on docker host machine
		String volMapping[] = cloudezzImageConfig.getVolumeMapping();
		if (volMapping != null && volMapping.length > 0) {
			for (int i = 0; i < volMapping.length; i++) {
				volMapping[i] = volMapping[i] + ":rw";
			}
			hostConfig.setBinds(volMapping);
		}
		
		return hostConfig;
	}

	/**
	 * Destroy all the container. It stops and clears the container in that host
	 * machine. Be careful while calling this method as it will stop and destroy
	 * the container and cannot retrieve data
	 * 
	 * @param dockerContainerList
	 * @throws CloudezzDeployException
	 */
	public void destroyContainers(DockerClient dockerClient, String dockerContainerId) throws CloudezzDeployException {
		Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
		Preconditions.checkNotNull(dockerContainerId, "dockerContainerId arg cannot be null");
		dockerClient.stopContainer(dockerContainerId);
		dockerClient.removeContainer(dockerContainerId, true);
		dockerClient.kill(dockerContainerId);
	}

	/**
	 * Destroy all the containers in the list. It stops and clears all the
	 * container in that host machine. Be careful while calling this method as
	 * it will stop and destroy all the container and cannot retrieve data
	 * 
	 * @param dockerContainerList
	 * @throws CloudezzDeployException
	 */
	public void destroyContainers(DockerClient dockerClient, List<String> dockerContainerList)
			throws CloudezzDeployException {
		Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
		Preconditions.checkNotNull(dockerContainerList, "dockerContainerList arg cannot be null");
		for (Iterator<String> iterator = dockerContainerList.iterator(); iterator.hasNext();) {
			String containerId = iterator.next();
			dockerClient.stopContainer(containerId);
			dockerClient.removeContainer(containerId, true);
			dockerClient.kill(containerId);
		}
	}
	/**
	 * Destroy all the containers in the host machine. It stops and clears all the
	 * container in that host machine. Be careful while calling this method as
	 * it will stop and destroy all the containers and cannot retrieve data
	 * 
	 * @param dockerContainerList
	 * @throws CloudezzDeployException
	 */
	public void destroyAllContainers(DockerClient dockerClient) throws CloudezzDeployException {
		Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
		List<Container> containers = dockerClient.listContainers(true);
		for (Iterator<Container> iterator = containers.iterator(); iterator.hasNext();) {
			Container container = iterator.next();
			dockerClient.stopContainer(container.id);
			dockerClient.removeContainer(container.id, true);
			dockerClient.kill(container.id);
		}
	}

}
