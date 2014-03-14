package com.cloudezz.houston.deployer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ServiceImageCfg;


public class DeployerVolumeAttachTest extends BaseApplicationContextLoader {

  @Autowired
  private DockerClient dockerClient;

  private ServiceImageCfg serviceImageConfig;

  @Before
  public void setup() throws CloudezzDeployException {
    serviceImageConfig = new ServiceImageCfg();
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
    List<String> servicePorts = new ArrayList<>();
    servicePorts.add("80");
    servicePorts.add("8009");
    serviceImageConfig.setPorts(servicePorts);
    serviceImageConfig.setTty(true);
    
    Map<String,String> hostToDockervolumeMapping = new HashMap<String,String>();
    hostToDockervolumeMapping.put("/opt/bbytes", "cloudezz/data");
    serviceImageConfig.setHostToDockerVolumeMapping(hostToDockervolumeMapping);
  }

  @Test
  public void deployImage() throws Exception {
    boolean success = DeployerUtil.startContainer(dockerClient, serviceImageConfig);
    Assert.assertTrue(success);
    ContainerInspectResponse containerInspectResponse =
        dockerClient.inspectContainer(serviceImageConfig.getContainerId());
    Assert.assertTrue(containerInspectResponse.volumes.size() > 0);
    
  }

  @After
  public void cleanup() throws CloudezzDeployException {
    DeployerUtil.destroyAllContainers(dockerClient);
    Assert.assertTrue(dockerClient.getContainersSize() == 0);
  }

}
