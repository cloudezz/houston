package com.cloudezz.houston.deployer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.client.DockerClientException;
import com.cloudezz.houston.deployer.docker.client.DockerConstant;
import com.cloudezz.houston.deployer.docker.client.DockerHostSSHConnection;
import com.cloudezz.houston.deployer.docker.model.Container;
import com.cloudezz.houston.deployer.docker.model.ContainerConfig;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse.NetworkSettings;
import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;
import com.cloudezz.houston.deployer.docker.model.Image;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.BaseImageCfg;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.domain.ImgSettings.VolumeConfig.VolumeMapping;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.service.ImageService;
import com.cloudezz.houston.util.SocketUtil;
import com.google.common.base.Preconditions;

/**
 * Util class that does most of the build container logic . All the method are util methos to build
 * the container from config and to stop container
 * 
 * @author Thanneer
 * 
 */
@Service
public class DeployerHelperService {

  private static Logger log = LoggerFactory.getLogger(DeployerHelperService.class);

  @Autowired
  private ImageService imageService;

  public void checkAndPullImage(DockerClient dockerClient, String imageTag)
      throws CloudezzDeployException {

    List<Image> images = dockerClient.getImages(imageTag, true);

    if (images == null || images.isEmpty() || !images.contains(imageTag)) {
      dockerClient.pull(imageTag);
    }
  }

  public List<Container> getAllContainers(DockerClient dockerClient) throws CloudezzDeployException {
    List<Container> containers = dockerClient.listContainers(true);
    return containers;
  }

  /**
   * Convert {@link BaseImageCfg} the pojo to {@link ContainerConfig} understood by docker remote
   * api
   * 
   * @param dockerInstance
   * @return
   */
  public ContainerConfig getDockerConfigFromCloudezzConfig(BaseImageCfg cloudezzImageConfig) {

    Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

    ContainerConfig config = new ContainerConfig();
    config.setImage(cloudezzImageConfig.getImageName());
    config.setTty(cloudezzImageConfig.isTty());
    config.setCpuShares(cloudezzImageConfig.getCpuShares());
    config.setHostName(cloudezzImageConfig.getHostName());
    config.setDomainName(cloudezzImageConfig.getDomainName());
    config.setDns(cloudezzImageConfig.getDnsAsArray());
    config.setVolumesFrom(cloudezzImageConfig.getDataVolumeFrom());
    if (cloudezzImageConfig.getName() != null && !cloudezzImageConfig.getName().isEmpty())
      config.setName(cloudezzImageConfig.getName());



    // image specific ports
    String[] ports = cloudezzImageConfig.getPortsAsArray();
    String[] availableHostPort =
        SocketUtil.nextAvailablePort(cloudezzImageConfig.getDockerHostMachine().getIpAddress(),
            ports.length);

    if (ports != null && ports.length > 0) {
      Map<String, Map<String, HostPortBinding>> exposedPorts =
          new HashMap<String, Map<String, HostPortBinding>>();
      for (int i = 0; i < ports.length; i++) {
        String port = ports[i];
        Map<String, HostPortBinding> hostPortMapping = new HashMap<String, HostPortBinding>();
        hostPortMapping.put(port + "/tcp", new HostPortBinding("0.0.0.0", availableHostPort[i]));
        cloudezzImageConfig.getDockerPortToHostPort().put(port, availableHostPort[i]);
        exposedPorts.put(port + "/tcp", hostPortMapping);
      }
      config.setExposedPorts(exposedPorts);
    }


    if (cloudezzImageConfig.getMemory() != null)
      config.setMemoryLimit(cloudezzImageConfig.getMemory() * 1024 * 1024);

    if (cloudezzImageConfig.getMemorySwap() != null)
      config.setMemorySwap(cloudezzImageConfig.getMemorySwap() * 1024 * 1024);

    imageService.setDefaultEnvMapping(cloudezzImageConfig, cloudezzImageConfig.getImageName());

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
  private boolean createAndSetContainerOnImageConfig(DockerClient dockerClient,
      BaseImageCfg cloudezzImageConfig) throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

    // pull the image to the host if not there already ..will take a long time to download the file
    // on the host..time consuming step
    checkAndPullImage(dockerClient, cloudezzImageConfig.getImageName());

    ContainerInspectResponse containerInspectResponse = null;
    ContainerConfig containerConfig = getDockerConfigFromCloudezzConfig(cloudezzImageConfig);
    if (cloudezzImageConfig.getName() != null && !cloudezzImageConfig.getName().isEmpty()) {
      containerInspectResponse =
          dockerClient.createAndGetContainer(containerConfig, cloudezzImageConfig.getName());
    } else {
      containerInspectResponse = dockerClient.createAndGetContainer(containerConfig);
    }

    if (containerInspectResponse == null)
      return false;

    cloudezzImageConfig.setContainerId(containerInspectResponse.getId());
    return true;

  }

  // /**
  // * Create the container and set data containers it on config object
  // *
  // * @param dockerClient
  // * @param cloudezzImageConfig
  // * @return
  // * @throws CloudezzDeployException
  // */
  // private static boolean createAndSetDataContainerOnImageConfig(DockerClient dockerClient,
  // BaseImageCfg cloudezzImageConfig) throws CloudezzDeployException {
  // Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
  // Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");
  //
  // // pull the image to the host if not there already ..will take a long time to download the file
  // // on the host..time consuming step
  // DeployerUtil.checkAndPullImage(dockerClient, cloudezzImageConfig.getImageName());
  //
  // ContainerCreateResponse containerCreateResponse = null;
  // ContainerConfig containerConfig = getDockerConfigFromCloudezzConfig(cloudezzImageConfig);
  // if (cloudezzImageConfig.getName() != null && !cloudezzImageConfig.getName().isEmpty()) {
  // containerCreateResponse =
  // dockerClient.createContainer(containerConfig, cloudezzImageConfig.getName());
  // } else {
  // containerCreateResponse = dockerClient.createContainer(containerConfig);
  // }
  //
  // if (containerCreateResponse == null)
  // return false;
  //
  // cloudezzImageConfig.setContainerId(containerCreateResponse.getId());
  // return true;
  //
  // }

  public boolean stopContainer(DockerClient dockerClient, BaseImageCfg cloudezzImageConfig)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

    // if no container then no need to stop jus return done
    if (cloudezzImageConfig.getContainerId() == null)
      return true;

    return dockerClient.stopContainer(cloudezzImageConfig.getContainerId());

  }

  public boolean killContainer(DockerClient dockerClient, BaseImageCfg cloudezzImageConfig)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

    // if no container then no need to stop jus return done
    if (cloudezzImageConfig.getContainerId() == null)
      return true;

    return dockerClient.kill(cloudezzImageConfig.getContainerId());

  }


  public String createContainer(DockerClient dockerClient, BaseImageCfg cloudezzImageConfig)
      throws CloudezzDeployException {

    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");

    String containerId = cloudezzImageConfig.getContainerId();
    if (containerId == null) {
      createAndSetContainerOnImageConfig(dockerClient, cloudezzImageConfig);
      containerId = cloudezzImageConfig.getContainerId();
    }

    return containerId;
  }

  // public static String createDataContainer(DockerClient dockerClient,
  // BaseImageCfg cloudezzImageConfig) throws CloudezzDeployException {
  //
  // Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
  // Preconditions.checkNotNull(cloudezzImageConfig, "BaseCloudezzImageConfig arg cannot be null");
  //
  // String containerId = cloudezzImageConfig.getContainerId();
  // if (containerId == null) {
  // createAndSetDataContainerOnImageConfig(dockerClient, cloudezzImageConfig);
  // containerId = cloudezzImageConfig.getContainerId();
  // }
  //
  // return containerId;
  // }

  /**
   * Start the container once the container is created or if already created then jus start it .
   * 
   * @param dockerClient
   * @param cloudezzImageConfig
   * @return
   * @throws CloudezzDeployException
   */
  public boolean startContainer(DockerClient dockerClient, BaseImageCfg cloudezzImageConfig)
      throws CloudezzDeployException {
    return startContainer(dockerClient, cloudezzImageConfig, null);
  }

  public boolean startContainer(DockerClient dockerClient, BaseImageCfg cloudezzImageConfig,
      HostConfig hostConfig) throws CloudezzDeployException {

    String containerId = createContainer(dockerClient, cloudezzImageConfig);

    ContainerInspectResponse containerInspectResponse = dockerClient.inspectContainer(containerId);

    if (containerInspectResponse.state != null && containerInspectResponse.state.running) {
      return true;
    }


    ContainerConfig config = containerInspectResponse.getConfig();
    Map<String, Map<String, HostPortBinding>> exposedPorts = config.getExposedPorts();
    String[] defaultPorts = exposedPorts.keySet().toArray((new String[0]));

    Map<String, String> dockerPortToHostPort = cloudezzImageConfig.getDockerPortToHostPort();


    Map<String, HostPortBinding[]> portBindings = new HashMap<>();
    if (defaultPorts != null && defaultPorts.length > 0) {
      for (int i = 0; i < defaultPorts.length; i++) {
        String actualDockerPort = defaultPorts[i];
        String hostPort = dockerPortToHostPort.get(actualDockerPort.split("/")[0]);
        portBindings.put(actualDockerPort, new HostPortBinding[] {new HostPortBinding("0.0.0.0",
            hostPort)});
      }
    }


    if (hostConfig != null) {
      hostConfig.setPortBindings(portBindings);
      dockerClient.startContainer(containerId, hostConfig);
    } else {
      // we need to give this as the port binding works by passing only hostConfig : might be a bug
      // in the docker rest api
      hostConfig = cloudezzImageConfig.getHostConfig();
      hostConfig.setPortBindings(portBindings);
      dockerClient.startContainer(containerId, hostConfig);
    }

    containerInspectResponse = dockerClient.inspectContainer(containerId);

    if (containerInspectResponse.state != null && containerInspectResponse.state.running) {
      return true;
    } else {
      return false;
    }
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
  public HostConfig linkImage(DockerClient dockerClient, AppImageCfg applicationImageConfig,
      ServiceImageCfg serviceImageConfig, String linkName) throws CloudezzDeployException {
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


    HostConfig hostConfig = applicationImageConfig.getHostConfig();
    hostConfig.setLinks(new String[] {containerInspectResponse.name + ":" + linkName});
    applicationImageConfig.setHostConfig(hostConfig);
    NetworkSettings networkSettings = containerInspectResponse.networkSettings;
    if (networkSettings != null) {
      String[] servicePorts = serviceImageConfig.getPortsAsArray();
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
  public void destroyContainers(DockerClient dockerClient, String dockerContainerId)
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
  public void destroyContainers(DockerClient dockerClient, List<String> dockerContainerList)
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
  public void destroyAllContainers(DockerClient dockerClient) throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    List<Container> containers = dockerClient.listContainers(true);
    for (Iterator<Container> iterator = containers.iterator(); iterator.hasNext();) {
      Container container = iterator.next();
      dockerClient.stopContainer(container.getId());
      dockerClient.removeContainer(container.getId(), true);
    }
  }

  public boolean deleteContainer(DockerClient dockerClient, BaseImageCfg baseImageConfig)
      throws CloudezzDeployException {
    try {
      return dockerClient.removeContainer(baseImageConfig.getContainerId(), true);
    } catch (DockerClientException e) {
      throw new CloudezzDeployException(e);
    }
  }

  public boolean deleteContainer(DockerClient dockerClient, String containerNameOrId)
      throws CloudezzDeployException {
    try {
      return dockerClient.removeContainer(containerNameOrId, true);
    } catch (DockerClientException e) {
      throw new CloudezzDeployException(e);
    }
  }

  // /**
  // * The class does the vol mapping of the two main folder '/cloudezz/app' and '/cloudezz/data' in
  // * the docker image to '/cloudezz/instance/{appName}/app' and
  // '/cloudezz/instance/{appName}/data'
  // * of docker host machine . These folder are mainly used to hold the data even after container
  // * destruction or restart. Currently done only for app imgs but has to be done to service img
  // * later. It creates the init file that will be called when the server starts , the init file is
  // * created and copied to '/cloudezz/instance/{appName}/data/cloudezz-config' of docker host and
  // * due to docker volume mapping the file will be available under
  // '/cloudezz/data/cloudezz-config'
  // * inside docker container
  // *
  // * @param dockerClient
  // * @param appName
  // * @param baseImageCfg
  // * @throws IOException
  // */
  // public static void setupDataContainerVolumeMapping(DockerClient dockerClient, String appName,
  // BaseImageCfg baseImageCfg) throws CloudezzDeployException {
  //
  // Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
  // Preconditions.checkNotNull(baseImageCfg, "applicationImageConfig arg cannot be null");
  //
  // String cloudezzHostSideAppFolder =
  // String.format(DockerConstant.FOLDER_APP_DOCKER_HOST_SIDE, appName);
  // String cloudezzHostSideDataFolder =
  // String.format(DockerConstant.FOLDER_DATA_DOCKER_HOST_SIDE, appName);
  // String cloudezzHostSideConfigFolder =
  // cloudezzHostSideAppFolder + DockerConstant.FOLDER_CLOUDEZZ_CONFIG;
  //
  // // creating init file and adding to folder thru ssh
  // try {
  //
  // DockerHostMachine dockerHostMachine = baseImageCfg.getDockerHostMachine();
  // DockerHostSSHConnection dockerHostSSHConnection =
  // new DockerHostSSHConnection(dockerHostMachine);
  //
  // String createAppFolderCMD = "mkdir -p " + cloudezzHostSideAppFolder;
  // String createDataFolderCMD = "mkdir -p " + cloudezzHostSideDataFolder;
  //
  // dockerHostSSHConnection.execCommand(createAppFolderCMD, dockerHostMachine.isSudo());
  // dockerHostSSHConnection.execCommand(createDataFolderCMD, dockerHostMachine.isSudo());
  //
  // // create init script only for app img inside /cloudezz/instance/{appName}/app/cloudezz-config
  // // on docker host machine
  // if (baseImageCfg.getInitScript() != null && !baseImageCfg.getInitScript().isEmpty()) {
  //
  // String createDataCfgFolderCMD = "mkdir -p " + cloudezzHostSideConfigFolder;
  //
  // dockerHostSSHConnection.execCommand(dockerHostMachine.isSudo(), createDataCfgFolderCMD);
  //
  // dockerHostSSHConnection.upload(baseImageCfg.getInitScript().getBytes(),
  // DockerConstant.FILE_CLOUDEZZ_INIT_SH, "/tmp");
  //
  // String copyInitFileCMD =
  // "cp /tmp/" + DockerConstant.FILE_CLOUDEZZ_INIT_SH + "  " + cloudezzHostSideConfigFolder
  // + "/" + DockerConstant.FILE_CLOUDEZZ_INIT_SH;
  // String chmodXInitFileCMD =
  // "chmod +x " + cloudezzHostSideConfigFolder + "/" + DockerConstant.FILE_CLOUDEZZ_INIT_SH;
  //
  // String removeInitFileTmpCMD = "rm /tmp/" + DockerConstant.FILE_CLOUDEZZ_INIT_SH;
  //
  // dockerHostSSHConnection.execCommand(dockerHostMachine.isSudo(), createDataCfgFolderCMD,
  // copyInitFileCMD, chmodXInitFileCMD, removeInitFileTmpCMD);
  // }
  //
  //
  // } catch (Exception e) {
  // throw new CloudezzDeployException(e);
  // }
  // // adding vol mapping
  // baseImageCfg.addHostToDockerVolumeMapping(cloudezzHostSideDataFolder,
  // DockerConstant.FOLDER_DATA_DOCKER_SIDE);
  // baseImageCfg.addHostToDockerVolumeMapping(cloudezzHostSideAppFolder,
  // DockerConstant.FOLDER_APP_DOCKER_SIDE);
  //
  // }

  public void setupContainerVolumeMapping(DockerClient dockerClient, String appName,
      BaseImageCfg baseImageCfg, ImageInfo imageInfo) throws CloudezzDeployException {

    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(baseImageCfg, "applicationImageConfig arg cannot be null");
    Preconditions.checkNotNull(imageInfo, "ImageInfo arg cannot be null");

    String cloudezzHostSideAppFolder =
        String.format(DockerConstant.FOLDER_APP_DOCKER_HOST_SIDE, appName);
    String cloudezzHostSideDataFolder =
        String.format(DockerConstant.FOLDER_DATA_DOCKER_HOST_SIDE, appName);



    // creating init file and adding to folder thru ssh
    try {

      List<VolumeMapping> volumeMappings = imageInfo.getVolumeMapping();
      if (volumeMappings != null) {
        for (VolumeMapping volumeMapping : volumeMappings) {
          String hostVol = volumeMapping.getHostVol();
          String containerVol = volumeMapping.getContainerVol();
          hostVol =
              hostVol.replaceFirst(DockerConstant.VOL_APP_FOLDER_MAPPING_XML_VAR,
                  cloudezzHostSideAppFolder);
          hostVol =
              hostVol.replaceFirst(DockerConstant.VOL_DATA_FOLDER_MAPPING_XML_VAR,
                  cloudezzHostSideDataFolder);
          baseImageCfg.addHostToDockerVolumeMapping(hostVol, containerVol);
        }
      }

      DockerHostMachine dockerHostMachine = baseImageCfg.getDockerHostMachine();
      DockerHostSSHConnection dockerHostSSHConnection =
          new DockerHostSSHConnection(dockerHostMachine);

      String createAppFolderCMD = "mkdir -p " + cloudezzHostSideAppFolder;
      String createDataFolderCMD = "mkdir -p " + cloudezzHostSideDataFolder;

      dockerHostSSHConnection.execCommand(createAppFolderCMD, dockerHostMachine.isSudo());
      dockerHostSSHConnection.execCommand(createDataFolderCMD, dockerHostMachine.isSudo());

    } catch (Exception e) {
      throw new CloudezzDeployException(e);
    }
    // adding vol mapping
    baseImageCfg.addHostToDockerVolumeMapping(cloudezzHostSideDataFolder,
        DockerConstant.FOLDER_DATA_DOCKER_SIDE);
    baseImageCfg.addHostToDockerVolumeMapping(cloudezzHostSideAppFolder,
        DockerConstant.FOLDER_APP_DOCKER_SIDE);

  }

  /**
   * Delete the folder on docker host while deleting the all img cfg. We can call this only for app
   * level as it will go into service level and clean the folders at Linux level as the same volumes
   * are mapped for service images too
   * 
   * @param dockerClient
   * @param appImageCfg
   * @throws CloudezzDeployException
   */
  public boolean cleanUpVolumeOnDockerHost(DockerClient dockerClient, AppImageCfg appImageCfg)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(dockerClient, "DockerClient arg cannot be null");
    Preconditions.checkNotNull(appImageCfg, "applicationImageConfig arg cannot be null");

    boolean success = false;
    // delete the folder on docker host while deleteing the aoo img cfg
    try {
      String cloudezzHostSideFolder =
          String.format(DockerConstant.FOLDER_APP_MAIN_DOCKER_HOST_SIDE, appImageCfg.getAppName());

      DockerHostMachine dockerHostMachine = appImageCfg.getDockerHostMachine();
      DockerHostSSHConnection dockerHostSSHConnection =
          new DockerHostSSHConnection(dockerHostMachine);

      String deleteMainAppFolderCMD = "rm -r -f " + cloudezzHostSideFolder;

      List<String> result =
          dockerHostSSHConnection.execCommand(deleteMainAppFolderCMD, dockerHostMachine.isSudo());

      for (String str : result) {
        log.debug(str);
      }

      success = true;
    } catch (Exception e) {
      throw new CloudezzDeployException(e);
    }

    return success;
  }



  // /**
  // * Start the container that will be used to share data between app and service images based on
  // * cloudezz/data img
  // *
  // * @param dockerClient
  // * @param appImageCfg
  // * @return
  // * @throws CloudezzDeployException
  // */
  // public static String createDataContainer(DockerClient dockerClient, AppImageCfg appImageCfg)
  // throws CloudezzDeployException {
  //
  // if (appImageCfg.getDataContainerName() != null)
  // return appImageCfg.getDataContainerName();
  //
  // String dataContainerName = appImageCfg.getAppName() + "-data";
  //
  // ServiceImageCfg dataImgCfg = new ServiceImageCfg();
  // dataImgCfg.setImageName("cloudezz/data");
  // dataImgCfg.setHostName(dataContainerName);
  // dataImgCfg.setName(dataContainerName);
  // dataImgCfg.setTty(false);
  // dataImgCfg.setDaemon(false);
  // dataImgCfg.setDockerHostMachine(appImageCfg.getDockerHostMachine());
  // dataImgCfg.setInitScript(appImageCfg.getInitScript());
  // setupDataContainerVolumeMapping(dockerClient, appImageCfg.getAppName(), dataImgCfg);
  //
  // try {
  // String containerId = createDataContainer(dockerClient, dataImgCfg);
  // appImageCfg.setDataContainerId(containerId);
  // appImageCfg.setDataContainerName(dataContainerName);
  // startContainer(dockerClient, dataImgCfg);
  // } catch (Exception e) {
  // log.warn(e.getMessage(), e);
  // // Failed to start data container but not an
  // // error so we handle it and don't propagate
  // }
  //
  // return dataContainerName;
  // }
}
