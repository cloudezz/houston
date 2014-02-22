package com.cloudezz.houston.deployer;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.deployer.docker.client.DockerClient;


public class ContainerDestroyAllTest extends BaseApplicationContextLoader {

  @Autowired
  private DockerClient dockerClient;


  @Test
  public void destroyAllContainers() throws Exception {
    DeployerUtil.destroyAllContainers(dockerClient);
    Assert.assertTrue(dockerClient.getContainersSize() == 0);
  }

}
