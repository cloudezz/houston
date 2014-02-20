package com.cloudezz.houston.deployer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.domain.ApplicationImageConfig;

@Component
public class CloudezzDeployerImpl implements CloudezzDeployer {

  @Autowired
  private DockerClient dockerClient;

  @Override
  public boolean deploy(ApplicationImageConfig appImageConfig) {

    return false;
  }

  @Override
  public boolean start(ApplicationImageConfig appImageConfig) {

    return false;
  }

  @Override
  public boolean restart(ApplicationImageConfig appImageConfig) {

    return false;
  }

  @Override
  public boolean stop(ApplicationImageConfig appImageConfig) {

    return false;
  }

  @Override
  public boolean deleteInstance(ApplicationImageConfig appImageConfig) {

    return false;
  }

  @Override
  public boolean delete(ApplicationImageConfig appImageConfig) {

    return false;
  }

}
