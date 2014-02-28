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
import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ServiceImageCfg;


public class DeployerImageEnvVariableTest extends BaseApplicationContextLoader {

  @Autowired
  private DockerClient dockerClient;

  private ServiceImageCfg serviceImageConfig= new ServiceImageCfg();

  private AppImageCfg applicationImageConfig= new AppImageCfg();

  @Before
  public void setup() throws CloudezzDeployException {

    DockerHostMachine dockerHostMachine = new DockerHostMachine();
    dockerHostMachine.setIpAddress("localhost");
    dockerHostMachine.setDockerPort("4243");
    dockerHostMachine.setCloudProviderName("my local machine");
    
    applicationImageConfig.setDockerHostMachine(dockerHostMachine);
    applicationImageConfig.setCpuShares(2);
    applicationImageConfig.setDaemon(false);
    applicationImageConfig.setDockerImageName("cloudezz/base");
    applicationImageConfig.setHostName("testmachine");
    applicationImageConfig.setMemory(512L);
    applicationImageConfig.setMemorySwap(1024L);
    List<String> ports = new ArrayList<>();
    ports.add("8990");
    applicationImageConfig.setPorts(ports);
    applicationImageConfig.setTty(true);
    Map<String, String> environmentMapping = new HashMap<String, String>();
    environmentMapping.put("MYSQL_ROOT_PASSWORD", "test123");
    applicationImageConfig.setEnvironmentMapping(environmentMapping);
    applicationImageConfig.addServiceImages(serviceImageConfig);
    Map<String, String> hostToDockervolumeMapping = new HashMap<String, String>();
    hostToDockervolumeMapping.put("/opt/bbytes", "cloudezz/data");
    serviceImageConfig.setHostToDockerVolumeMapping(hostToDockervolumeMapping);

   
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
    serviceImageConfig.setHostToDockerVolumeMapping(hostToDockervolumeMapping);
  }

  @Test
  public void attachAndDeployImage() throws Exception {
    boolean success = DeployerUtil.startContainer(dockerClient, serviceImageConfig);
    Assert.assertTrue(success);
    ContainerInspectResponse containerInspectResponse =
        dockerClient.inspectContainer(serviceImageConfig.getContainerId());

    HostConfig hostConfig = applicationImageConfig.getHostConfig();
    hostConfig.setLinks(new String[] {containerInspectResponse.name+":dep_base"});
    DeployerUtil.startContainer(dockerClient, applicationImageConfig, hostConfig);

    containerInspectResponse =
        dockerClient.inspectContainer(serviceImageConfig.getContainerId());

  }

  @After
  public void cleanup() throws CloudezzDeployException {
    DeployerUtil.destroyAllContainers(dockerClient);
    Assert.assertTrue(dockerClient.getContainersSize() == 0);
  }

}