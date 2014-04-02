package com.cloudezz.houston.deployer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.client.DockerImageStartException;
import com.cloudezz.houston.deployer.docker.client.DockerImageStopException;
import com.cloudezz.houston.deployer.docker.client.utils.DockerUtil;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.logstream.ContainerLogExecutor;
import com.cloudezz.houston.repository.ImageInfoRepository;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Service
public class DeployerServiceImpl implements DeployerService {

  private final Logger log = LoggerFactory.getLogger(DeployerServiceImpl.class);

  @Inject
  private DeployerHelperService deployerHelperService;
  
  @Inject
  private ContainerLogExecutor containerLogManager;

  @Inject
  private ImageInfoRepository imageInfoRepository;


  @Override
  public boolean start(AppImageCfg appImageConfig) throws CloudezzDeployException {

    DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
    List<String> containerIdCache = new ArrayList<String>();

    try {
      // // create data container
      // String dataContainerName = DeployerUtil.createDataContainer(dockerClient, appImageConfig);
      // // set data container on app img
      // appImageConfig.setDataVolumeFrom(dataContainerName);

      for (ServiceImageCfg serviceImageConfig : appImageConfig.getServiceImages()) {
        serviceImageConfig.setDockerHostMachine(appImageConfig.getDockerHostMachine());
        // // set data container on service image too
        // serviceImageConfig.setDataVolumeFrom(dataContainerName);
        // setup vol mapping based on img setting vol config info
        ImageInfo imageInfo =
            imageInfoRepository.findByImageName(serviceImageConfig.getImageName());
        deployerHelperService.setupContainerVolumeMapping(dockerClient, appImageConfig.getAppName(),
            serviceImageConfig, imageInfo);
        boolean success = deployerHelperService.startContainer(dockerClient, serviceImageConfig);

        if (success) {
          containerIdCache.add(serviceImageConfig.getContainerId());
//          // logic to link the correct ports and env between ,machine for them to work as a group
//          deployerHelperService.linkImage(dockerClient, appImageConfig, serviceImageConfig,
//              serviceImageConfig.getLinkName());
        } else {
          // if even one service container start fails then stop and delete the old service
          // containers that were started
          deployerHelperService.destroyContainers(dockerClient, containerIdCache);
          throw new DockerImageStartException("Cloudn't start container with id "
              + serviceImageConfig.getContainerId());
        }
      }



    } catch (DockerImageStartException e) {
      throw new CloudezzDeployException(
          "One of the service image container start failed so couldn't deploy the application");
    }
   
    // setup vol mapping based on img setting vol config info
    ImageInfo imageInfo = imageInfoRepository.findByImageName(appImageConfig.getImageName());
    deployerHelperService.setupContainerVolumeMapping(dockerClient, appImageConfig.getAppName(),
        appImageConfig, imageInfo);
    // the host config contains all the logic to add env and ports and links to service image
    boolean success =
        deployerHelperService.startContainer(dockerClient, appImageConfig, appImageConfig.getHostConfig());

    // stream log

    containerLogManager.startLog(appImageConfig.getContainerId(),
        appImageConfig.getDockerHostMachine());

    return success;

  }

  @Override
  public boolean restart(AppImageCfg appImageConfig) throws CloudezzDeployException {
    stop(appImageConfig);
    return start(appImageConfig);
  }

  @Override
  public boolean stop(AppImageCfg appImageConfig) throws CloudezzDeployException {
    Preconditions.checkNotNull(appImageConfig, "App Image cfg arg cannot be null");

    DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
    List<String> containerIdFailList = new ArrayList<String>();
    List<ServiceImageCfg> serviceImageConfigs = Lists.reverse(appImageConfig.getServiceImages());
    for (ServiceImageCfg serviceImageConfig : serviceImageConfigs) {

      boolean done = deployerHelperService.stopContainer(dockerClient, serviceImageConfig);

      if (!done)
        containerIdFailList.add(serviceImageConfig.getContainerId());
    }


    // stop log
    containerLogManager.stopLog(appImageConfig.getContainerId());

    // finally stop app image
    boolean success = deployerHelperService.stopContainer(dockerClient, appImageConfig);
    if (containerIdFailList.size() > 0 || !success) {
      throw new DockerImageStopException("Few container couldn't be stopped");
    } else {
      return true;
    }
  }

  @Override
  public boolean delete(AppImageCfg appImageConfig) throws CloudezzDeployException {
    // first stop if running
    try {
      stop(appImageConfig);
    } catch (CloudezzDeployException e) {
      log.error("Error stopping container before delete , may be the container wasn't running", e);
    }
    DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());

    if (!dockerClient.isDockerHostAvailable() && !appImageConfig.isRunning())
      return true;

    List<String> containerIdFailList = new ArrayList<String>();
    List<ServiceImageCfg> serviceImageConfigs = Lists.reverse(appImageConfig.getServiceImages());
    for (ServiceImageCfg serviceImageConfig : serviceImageConfigs) {

      if (serviceImageConfig.getContainerId() != null) {
        boolean done = deployerHelperService.deleteContainer(dockerClient, serviceImageConfig);

        if (!done)
          containerIdFailList.add(serviceImageConfig.getContainerId());
      }

    }

    // finally stop app image
    if (appImageConfig.getContainerId() != null) {

//      String dataContainerId = appImageConfig.getDataContainerId();

      boolean success = deployerHelperService.deleteContainer(dockerClient, appImageConfig);
      if (!success)
        throw new DockerImageStopException("App container deletion failed for App Img Cfg : "
            + appImageConfig.getAppName());

//      // Delete data container too
//      boolean dataContainerDeleted = DeployerUtil.deleteContainer(dockerClient, dataContainerId);
//      if (!dataContainerDeleted)
//        throw new DockerImageStopException("App Data container deletion failed for App Img Cfg : "
//            + appImageConfig.getAppName());


      boolean cleanUpSuccess = deployerHelperService.cleanUpVolumeOnDockerHost(dockerClient, appImageConfig);
      if (!cleanUpSuccess)
        throw new DockerImageStopException(
            "Data folder in docker host deletion failed for App Img Cfg : "
                + appImageConfig.getAppName());

      if (containerIdFailList.size() > 0) {
        throw new DockerImageStopException("Few container's delete cmd failed for App Img Cfg : "
            + appImageConfig.getAppName());
      } else {
        return true;
      }
    }
    return true;

  }


}
