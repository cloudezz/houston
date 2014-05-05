package com.cloudezz.houston.deployer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.client.DockerImageStartException;
import com.cloudezz.houston.deployer.docker.client.DockerImageStopException;
import com.cloudezz.houston.deployer.docker.client.utils.DockerUtil;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.Application;
import com.cloudezz.houston.domain.Container;
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.logstream.ContainerLogManager;
import com.cloudezz.houston.repository.ImageInfoRepository;
import com.google.common.base.Preconditions;

@Service
public class DeployerServiceImpl implements DeployerService {

  private final Logger log = LoggerFactory.getLogger(DeployerServiceImpl.class);

  @Inject
  private DeployerHelperService deployerHelperService;

  @Inject
  private ContainerLogManager containerLogManager;

  @Inject
  private ImageInfoRepository imageInfoRepository;


  @Override
  public boolean start(Application application) throws CloudezzDeployException {
    boolean successServiceStart = true;
    boolean successAppStart = true;

    try {
      successServiceStart = startServiceImages(application);
      successAppStart = startAppImages(application);
      application.setStartTime(LocalDateTime.now());
    } catch (CloudezzDeployException e) {
      // call stop to stop any running or hanging container in the service or app img list
      stop(application);
      throw e;
    }

    return successServiceStart && successAppStart;
  }


  private boolean startAppImages(Application application) throws CloudezzDeployException {
    List<Container> containerCache = new ArrayList<Container>();
    boolean success = true;

    try {
      for (AppImageCfg appImageConfig : application.getAppImageCfgs()) {
        DockerClient dockerClient =
            DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
        // setup vol mapping based on img setting vol config info
        ImageInfo imageInfo = imageInfoRepository.findByImageName(appImageConfig.getImageName());
        deployerHelperService.setupContainerVolumeMapping(dockerClient, application.getAppName(),
            appImageConfig, imageInfo);
        success = deployerHelperService.startContainer(dockerClient, appImageConfig);

        if (success) {
          containerCache.add(appImageConfig.getContainer());
        } else {
          // if even one app container start fails then stop and delete the old app
          // containers that were started
          containerLogManager.stopLog(containerCache);
          deployerHelperService.destroyContainers(dockerClient, containerCache);
          throw new DockerImageStartException("Cloudn't start container with id "
              + appImageConfig.getContainer());
        }
        
        // start log
        containerLogManager.startLog(appImageConfig.getContainer(),appImageConfig.getDockerHostMachine());
      }

    } catch (DockerImageStartException e) {
      throw new CloudezzDeployException(
          "One of the App image container start failed so couldn't deploy the application");
    }

    return success;

  }


  private boolean startServiceImages(Application application) throws CloudezzDeployException {
    List<Container> containerCache = new ArrayList<Container>();
    boolean success = true;
    try {
      for (ServiceImageCfg serviceImageConfig : application.getServiceImageCfgs()) {
        DockerClient dockerClient =
            DockerUtil.getDockerClient(serviceImageConfig.getDockerHostMachine());
        // setup vol mapping based on img setting vol config info
        ImageInfo imageInfo =
            imageInfoRepository.findByImageName(serviceImageConfig.getImageName());
        deployerHelperService.setupContainerVolumeMapping(dockerClient, application.getAppName(),
            serviceImageConfig, imageInfo);
        success = deployerHelperService.startContainer(dockerClient, serviceImageConfig);

        if (success) {
          containerCache.add(serviceImageConfig.getContainer());
        } else {
          // if even one service container start fails then stop and delete the old service
          // containers that were started
          deployerHelperService.destroyContainers(dockerClient, containerCache);
          throw new DockerImageStartException("Cloudn't start container with id "
              + serviceImageConfig.getContainer());
        }
      }

    } catch (DockerImageStartException e) {
      throw new CloudezzDeployException(
          "One of the service image container start failed so couldn't deploy the application");
    }

    return success;
  }

  @Override
  public boolean restart(Application application) throws CloudezzDeployException {
    stop(application);
    return start(application);
  }

  @Override
  public boolean stop(Application application) throws CloudezzDeployException {
    Preconditions.checkNotNull(application, "Applicatio arg cannot be null");

    if (!application.isRunning())
      return true;

    List<Container> containerFailList = new ArrayList<Container>();

    // stop service imgs
    Set<ServiceImageCfg> serviceImageConfigs = application.getServiceImageCfgs();
    for (ServiceImageCfg serviceImageConfig : serviceImageConfigs) {
      DockerClient dockerClient =
          DockerUtil.getDockerClient(serviceImageConfig.getDockerHostMachine());
      boolean done = deployerHelperService.stopContainer(dockerClient, serviceImageConfig);
      if (!done) {
        containerFailList.add(serviceImageConfig.getContainer());
      } else {
        // stop log
        containerLogManager.stopLog(serviceImageConfig.getContainer());
      }
    }

    // stop app imgs
    Set<AppImageCfg> appImageConfigs = application.getAppImageCfgs();
    for (AppImageCfg appImageConfig : appImageConfigs) {
      DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
      boolean done = deployerHelperService.stopContainer(dockerClient, appImageConfig);
      if (!done) {
        containerFailList.add(appImageConfig.getContainer());
      } else {
        // stop log
        containerLogManager.stopLog(appImageConfig.getContainer());
      }
    }

    if (containerFailList.size() > 0) {
      throw new DockerImageStopException("Few container couldn't be stopped");
    } else {
      return true;
    }
  }

  @Override
  public boolean delete(Application application) throws CloudezzDeployException {
    // first stop if running
    try {
      stop(application);
    } catch (CloudezzDeployException e) {
      log.error("Error stopping container before delete , may be the container wasn't running", e);
    }

    List<Container> containerFailList = new ArrayList<Container>();
    Set<ServiceImageCfg> serviceImageConfigs = application.getServiceImageCfgs();
    for (ServiceImageCfg serviceImageConfig : serviceImageConfigs) {
      DockerClient dockerClient =
          DockerUtil.getDockerClient(serviceImageConfig.getDockerHostMachine());
      if (serviceImageConfig.getContainer() != null) {
        boolean done = deployerHelperService.deleteContainer(dockerClient, serviceImageConfig);

        if (!done)
          containerFailList.add(serviceImageConfig.getContainer());
      }

    }

    Set<AppImageCfg> appImageCfgs = application.getAppImageCfgs();
    for (AppImageCfg appImageConfig : appImageCfgs) {
      DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
      if (appImageConfig.getContainer() != null) {
        boolean done = deployerHelperService.deleteContainer(dockerClient, appImageConfig);
        if (!done)
          containerFailList.add(appImageConfig.getContainer());

        boolean cleanUpSuccess =
            deployerHelperService.cleanUpVolumeOnDockerHost(dockerClient, appImageConfig);
        if (!cleanUpSuccess)
          throw new DockerImageStopException(
              "Data folder in docker host deletion failed for App Img Cfg : "
                  + appImageConfig.getAppName());
      }
    }


    if (containerFailList.size() > 0) {
      throw new DockerImageStopException("Few container's delete cmd failed for Application : "
          + application.getAppName());
    } else {
      return true;
    }


  }


}
