package com.cloudezz.houston.deployer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.client.DockerConstant;
import com.cloudezz.houston.deployer.docker.model.Container;
import com.cloudezz.houston.deployer.docker.model.ContainerConfig;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse.NetworkSettings;
import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;
import com.cloudezz.houston.deployer.docker.model.Image;
import com.cloudezz.houston.domain.ApplicationImageConfig;
import com.cloudezz.houston.domain.BaseCloudezzImageConfig;
import com.cloudezz.houston.domain.ServiceImageConfig;
import com.google.common.base.Preconditions;

/**
 * Util class that does most of the build container logic . All the method are util methos to build
 * the container from config and to stop container
 * 
 * @author Thanneer
 * 
 */
public class DeployerUtil {

  public static void checkAndPullImage(DockerClient dockerClient, String imageTag)
      throws CloudezzDeployException {

    List<Image> images = dockerClient.getImages(imageTag, true);

    if (images == null || images.isEmpty() || !images.contains(imageTag)) {
      dockerClient.pull(imageTag);
    }
  }

  public static List<Container> getAllContainers(DockerClient dockerClient)
      throws CloudezzDeployException {
    List<Container> containers = dockerClient.listContainers(true);
    return containers;
  }

  /**
   * Convert {@link BaseCloudezzImageConfig} the pojo to {@link ContainerConfig} understood by
   * docker remote api
   * 
   * @param dockerInstance
   * @return
   */
  public static ContainerConfig getDockerConfigFromCloudezzConfig(
      BaseCloudezzImageConfig cloudezzImageConfig) {

    Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

    ContainerConfig config = new ContainerConfig();
    config.setImage(cloudezzImageConfig.getDockerImageName());
    config.setTty(cloudezzImageConfig.getTty());
    config.setCpuShares(cloudezzImageConfig.getCpuShares());
    config.setHostName(cloudezzImageConfig.getHostName());


    String[] ports = cloudezzImageConfig.getPorts();
    if (ports != null && ports.length > 0) {
      Map<String, Map<String, HostPortBinding>> exposedPorts =
          new HashMap<String, Map<String, HostPortBinding>>();
      for (int i = 0; i < ports.length; i++) {
        String port = ports[i];
        exposedPorts.put(port + "/tcp", new HashMap<String, HostPortBinding>());
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
   * 
   * @param dockerClient
   * @param cloudezzImageConfig
   * @return
   * @throws CloudezzDeployException
   */
  private static boolean createAndSetContainerOnImageConfig(DockerClient dockerClient,
      BaseCloudezzImageConfig cloudezzImageConfig) throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

    ContainerConfig containerConfig = getDockerConfigFromCloudezzConfig(cloudezzImageConfig);
    ContainerInspectResponse containerInspectResponse =
        dockerClient.createAndGetContainer(containerConfig);
    if (containerInspectResponse == null)
      return false;

    cloudezzImageConfig.setContainerId(containerInspectResponse.getId());
    return true;

  }

  /**
   * Start the container once the container is created or if already created then jus start it .
   * 
   * @param dockerClient
   * @param cloudezzImageConfig
   * @return
   * @throws CloudezzDeployException
   */
  public static boolean startContainer(DockerClient dockerClient,
      BaseCloudezzImageConfig cloudezzImageConfig) throws CloudezzDeployException {
    return startContainer(dockerClient, cloudezzImageConfig, null);
  }

  public static boolean startContainer(DockerClient dockerClient,
      BaseCloudezzImageConfig cloudezzImageConfig, HostConfig hostConfig)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

    String containerId = cloudezzImageConfig.getContainerId();
    if (containerId == null) {
      createAndSetContainerOnImageConfig(dockerClient, cloudezzImageConfig);
      containerId = cloudezzImageConfig.getContainerId();
    }

    if (hostConfig != null) {
      return dockerClient.startContainer(containerId, hostConfig);
    } else {
      // we need to give this as the port binding works by passing only hostConfig : might be a bug
      // in the docker rest api
      hostConfig = getDefaultHostConfig(cloudezzImageConfig);
      return dockerClient.startContainer(containerId, hostConfig);
    }
  }

  /**
   * Get the host config like port mapping and volume mapping
   * 
   * @param cloudezzImageConfig
   * @return
   */
  public static HostConfig getDefaultHostConfig(BaseCloudezzImageConfig cloudezzImageConfig) {

    HostConfig hostConfig = new HostConfig();
    hostConfig.setPublishAllPorts(true);

    // port mappings
    Map<String, HostPortBinding[]> portBindings = hostConfig.getPortBindings();
    String portMappings[] = cloudezzImageConfig.getPorts();
    if (portMappings != null) {
      for (int i = 0; i < portMappings.length; i++) {
        HostPortBinding[] portBindingForContainerPort = new HostPortBinding[1];
        portBindingForContainerPort[0] = new HostPortBinding();
        portBindings.put(portMappings[i] + "/tcp", portBindingForContainerPort);
      }
      // note : lil doubtful abt this settings as we are setting port for host so commented ..need
      // to test after uncommeting

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
   * Link image as parent child with link name and return the host config to be used to start the
   * parent image.
   * 
   * @param dockerClient
   * @param applicationImageConfig
   * @param serviceImageConfig
   * @param linkName
   * @return
   * @throws CloudezzDeployException
   */
  public static HostConfig linkImage(DockerClient dockerClient,
      ApplicationImageConfig applicationImageConfig, ServiceImageConfig serviceImageConfig,
      String linkName) throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(applicationImageConfig, "applicationImageConfig arg cannot be null");
    Preconditions.checkNotNull(serviceImageConfig, "serviceImageConfig arg cannot be null");
    Preconditions.checkNotNull(linkName, "linkName arg cannot be null");

    // allow link only when we have the contaier ready as we have to know the ip address and exposed
    // ports
    Preconditions.checkNotNull(serviceImageConfig.getContainerId(),
        "Cannot link a service image that doesnt have a container yet");

    ContainerInspectResponse containerInspectResponse =
        dockerClient.inspectContainer(serviceImageConfig.getContainerId());


    HostConfig hostConfig = DeployerUtil.getDefaultHostConfig(applicationImageConfig);
    hostConfig.setLinks(new String[] {containerInspectResponse.name + ":" + linkName});
    NetworkSettings networkSettings = containerInspectResponse.networkSettings;
    if (networkSettings != null) {
      String[] servicePorts = serviceImageConfig.getPorts();
      String serviceMachineIp = containerInspectResponse.networkSettings.ipAddress;
      Map<String, HostPortBinding[]> ports = networkSettings.ports;
      String tcpForwards = "";
      if (ports != null) {
        Set<String> serviceNetworkPorts = ports.keySet();
        for (int i = 0; i < servicePorts.length; i++) {
          // apply tcp forwards from localhost
          // sample : TCP_FORWARD =
          // "127.0.0.1 3306 172.3.0.4 3306, 127.0.0.1 3000 172.3.0.4 3000"
          if (serviceNetworkPorts.contains(servicePorts[i] + "/tcp")) {
            tcpForwards =
                tcpForwards + "127.0.0.1" + " " + servicePorts[i] + " " + serviceMachineIp + " "
                    + servicePorts[i] + ",";
          }
        }
        if (!tcpForwards.isEmpty())
          applicationImageConfig.addEnvironmentMapping(DockerConstant.ENV_TCP_FORWARD, tcpForwards);
      }
    }

    serviceImageConfig.setLinkName(linkName);
    return hostConfig;
  }

  /**
   * Destroy all the container. It stops and clears the container in that host machine. Be careful
   * while calling this method as it will stop and destroy the container and cannot retrieve data
   * 
   * @param dockerContainerList
   * @throws CloudezzDeployException
   */
  public static void destroyContainers(DockerClient dockerClient, String dockerContainerId)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(dockerContainerId, "dockerContainerId arg cannot be null");
    dockerClient.stopContainer(dockerContainerId);
    dockerClient.removeContainer(dockerContainerId, true);
  }

  /**
   * Destroy all the containers in the list. It stops and clears all the container in that host
   * machine. Be careful while calling this method as it will stop and destroy all the container and
   * cannot retrieve data
   * 
   * @param dockerContainerList
   * @throws CloudezzDeployException
   */
  public static void destroyContainers(DockerClient dockerClient, List<String> dockerContainerList)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(dockerContainerList, "dockerContainerList arg cannot be null");
    for (Iterator<String> iterator = dockerContainerList.iterator(); iterator.hasNext();) {
      String containerId = iterator.next();
      dockerClient.stopContainer(containerId);
      dockerClient.removeContainer(containerId, true);
    }
  }

  /**
   * Destroy all the containers in the host machine. It stops and clears all the container in that
   * host machine. Be careful while calling this method as it will stop and destroy all the
   * containers and cannot retrieve data
   * 
   * @param dockerContainerList
   * @throws CloudezzDeployException
   */
  public static void destroyAllContainers(DockerClient dockerClient) throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    List<Container> containers = dockerClient.listContainers(true);
    for (Iterator<Container> iterator = containers.iterator(); iterator.hasNext();) {
      Container container = iterator.next();
      dockerClient.stopContainer(container.getId());
      dockerClient.removeContainer(container.getId(), true);
    }
  }

}
