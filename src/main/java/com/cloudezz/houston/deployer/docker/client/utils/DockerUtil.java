package com.cloudezz.houston.deployer.docker.client.utils;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.google.common.base.Preconditions;

public class DockerUtil {


  public static DockerClient getDockerClient(DockerHostMachine hostMachine)
      throws CloudezzDeployException {
    Preconditions.checkNotNull(hostMachine,
        "Docker host machine information has to be set on App image config");

    DockerClient dockerClient = new DockerClient(hostMachine.getDockerDaemonURL());
    if (!dockerClient.isDockerHostAvailable())
      throw new CloudezzDeployException(
          "Could not reach docker host machine to deploy the containers ");

    return dockerClient;
  }
}
