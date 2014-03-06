package com.cloudezz.houston.deployer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.client.DockerImageStartException;
import com.cloudezz.houston.deployer.docker.client.DockerImageStopException;
import com.cloudezz.houston.deployer.docker.client.utils.DockerUtil;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.google.common.collect.Lists;

@Component
public class DeployerImpl implements Deployer {

  @Override
  public boolean start(AppImageCfg appImageConfig) throws CloudezzDeployException {

    DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
    List<String> containerIdCache = new ArrayList<String>();

    try {
      for (ServiceImageCfg serviceImageConfig : appImageConfig.getServiceImages()) {
        boolean success = DeployerUtil.startContainer(dockerClient, serviceImageConfig);

        if (success) {
          containerIdCache.add(serviceImageConfig.getContainerId());
          // logic to link the correct ports and env between ,machine for them to work as a group
          DeployerUtil.linkImage(dockerClient, appImageConfig, serviceImageConfig,
              serviceImageConfig.getLinkName());
        } else {
          // if even one service container start fails then stop and delete the old service
          // containers that were started
          DeployerUtil.destroyContainers(dockerClient, containerIdCache);
          throw new DockerImageStartException("Cloudn't start container with id "
              + serviceImageConfig.getContainerId());
        }

      }
    } catch (DockerImageStartException e) {
      throw new CloudezzDeployException(
          "One of the service image container start failed so couldn't deploy the application");
    }
    if (appImageConfig.hasPortOverLapIssue()) {
      throw new CloudezzDeployException(
          "Port Overlap Issue :  The service image added exposes the ports exposed by other service or by app image");
    }
    // the host config contains all the logic to add env and ports and links to service image
    return DeployerUtil
        .startContainer(dockerClient, appImageConfig, appImageConfig.getHostConfig());

  }


  @Override
  public boolean restart(AppImageCfg appImageConfig) throws CloudezzDeployException {
    stop(appImageConfig);
    return start(appImageConfig);
  }

  @Override
  public boolean stop(AppImageCfg appImageConfig) throws CloudezzDeployException {
    DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
    List<String> containerIdFailList = new ArrayList<String>();
    List<ServiceImageCfg> serviceImageConfigs = Lists.reverse(appImageConfig.getServiceImages());
    for (ServiceImageCfg serviceImageConfig : serviceImageConfigs) {


      boolean done = DeployerUtil.stopContainer(dockerClient, serviceImageConfig);

      if (!done)
        containerIdFailList.add(serviceImageConfig.getContainerId());
    }

    // finally stop app image
    boolean success = DeployerUtil.stopContainer(dockerClient, appImageConfig);
    if (containerIdFailList.size() > 0 || !success) {
      throw new DockerImageStopException("Few container couldn't be stopped");
    } else {
      return true;
    }
  }

  @Override
  public boolean deleteInstance(AppImageCfg appImageConfig) throws CloudezzDeployException {

    DockerClient dockerClient = DockerUtil.getDockerClient(appImageConfig.getDockerHostMachine());
    List<String> containerIdFailList = new ArrayList<String>();
    List<ServiceImageCfg> serviceImageConfigs = Lists.reverse(appImageConfig.getServiceImages());
    for (ServiceImageCfg serviceImageConfig : serviceImageConfigs) {


      boolean done = DeployerUtil.deleteContainer(dockerClient, serviceImageConfig);

      if (!done)
        containerIdFailList.add(serviceImageConfig.getContainerId());
    }

    // finally stop app image
    boolean success = DeployerUtil.deleteContainer(dockerClient, appImageConfig);
    if (containerIdFailList.size() > 0 || !success) {
      throw new DockerImageStopException("Few container's delete failed");
    } else {
      return true;
    }
  }

  @Override
  public boolean delete(AppImageCfg appImageConfig) {
    return false;
  }

}
