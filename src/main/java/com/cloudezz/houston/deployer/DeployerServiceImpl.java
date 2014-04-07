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
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.logstream.ContainerLogExecutor;
import com.cloudezz.houston.repository.ImageInfoRepository;
import com.google.common.base.Preconditions;

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
    List<String> containerIdCache = new ArrayList<String>();
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
          containerIdCache.add(appImageConfig.getContainerId());
        } else {
          // if even one app container start fails then stop and delete the old service
          // containers that were started
          deployerHelperService.destroyContainers(dockerClient, containerIdCache);
          throw new DockerImageStartException("Cloudn't start container with id "
              + appImageConfig.getContainerId());
        }
      }

    } catch (DockerImageStartException e) {
      throw new CloudezzDeployException(
          "One of the App image container start failed so couldn't deploy the application");
    }

    return success;

  }


  private boolean startServiceImages(Application application) throws CloudezzDeployException {
    List<String> containerIdCache = new ArrayList<String>();
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
          containerIdCache.add(serviceImageConfig.getContainerId());
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

    List<String> containerIdFailList = new ArrayList<String>();

    // stop service imgs
    Set<ServiceImageCfg> serviceImageConfigs = application.getServiceImageCfgs();
    for (ServiceImageCfg serviceImageConfig : serviceImageConfigs) {
      DockerClient dockerClient =
          DockerUtil.getDockerClient(serviceImageConfig.getDockerHostMachine());
      boolean done = deployerHelperService.stopContainer(dockerClient, serviceImageConfig);
      if (!done) {
        containerIdFailList.add(serviceImageConfig.getContainerId());
      } else {
        // stop log
        containerLogManager.stopLog(serviceImageConfig.getContainerId());
      }
    }

    // stop app imgs
    Set<AppImageCfg> appImageConfigs = application.getAppImageCfgs();
    for (AppImageCfg appImageConfig : appImageConfigs) {
      DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
      boolean done = deployerHelperService.stopContainer(dockerClient, appImageConfig);
      if (!done) {
        containerIdFailList.add(appImageConfig.getContainerId());
      } else {
        // stop log
        containerLogManager.stopLog(appImageConfig.getContainerId());
      }
    }

    if (containerIdFailList.size() > 0) {
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

    List<String> containerIdFailList = new ArrayList<String>();
    Set<ServiceImageCfg> serviceImageConfigs = application.getServiceImageCfgs();
    for (ServiceImageCfg serviceImageConfig : serviceImageConfigs) {
      DockerClient dockerClient =
          DockerUtil.getDockerClient(serviceImageConfig.getDockerHostMachine());
      if (serviceImageConfig.getContainerId() != null) {
        boolean done = deployerHelperService.deleteContainer(dockerClient, serviceImageConfig);

        if (!done)
          containerIdFailList.add(serviceImageConfig.getContainerId());
      }

    }

    Set<AppImageCfg> appImageCfgs = application.getAppImageCfgs();
    for (AppImageCfg appImageConfig : appImageCfgs) {
      DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
      if (appImageConfig.getContainerId() != null) {
        boolean done = deployerHelperService.deleteContainer(dockerClient, appImageConfig);
        if (!done)
          containerIdFailList.add(appImageConfig.getContainerId());

        boolean cleanUpSuccess =
            deployerHelperService.cleanUpVolumeOnDockerHost(dockerClient, appImageConfig);
        if (!cleanUpSuccess)
          throw new DockerImageStopException(
              "Data folder in docker host deletion failed for App Img Cfg : "
                  + appImageConfig.getAppName());
      }
    }


    if (containerIdFailList.size() > 0) {
      throw new DockerImageStopException("Few container's delete cmd failed for Application : "
          + application.getAppName());
    } else {
      return true;
    }


  }


}
