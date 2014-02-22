package com.cloudezz.houston.deployer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ServiceImageConfig;


public class DeployerDeployConfigTest extends BaseApplicationContextLoader {

  @Autowired
  private DockerClient dockerClient;

  private ServiceImageConfig serviceImageConfig;

  @Before
  public void setup() throws CloudezzDeployException {
    serviceImageConfig = new ServiceImageConfig();
    DockerHostMachine dockerHostMachine = new DockerHostMachine();
    dockerHostMachine.setIpAddress("localhost");
    dockerHostMachine.setDockerPort("4243");
    dockerHostMachine.setCloudProviderName("my local machine");

    serviceImageConfig.setDockerHostMachine(dockerHostMachine);
    serviceImageConfig.setCpuShares(2);
    serviceImageConfig.setDaemon(false);
    serviceImageConfig.setDockerImageName("cloudezz/base");
    serviceImageConfig.setHostName("testmachine");
    serviceImageConfig.setMemory(512L);
    serviceImageConfig.setMemorySwap(1024L);
    serviceImageConfig.setPorts(new String[] {"80", "8009"});
    serviceImageConfig.setTty(true);

  }

  @Test
  public void deployImage() throws Exception {
    boolean success = DeployerUtil.startContainer(dockerClient, serviceImageConfig);
    Assert.assertTrue(success);
  }



  @After
  public void cleanup() throws CloudezzDeployException {
    DeployerUtil.destroyAllContainers(dockerClient);
    Assert.assertTrue(dockerClient.getContainersSize() == 0);
  }

}
