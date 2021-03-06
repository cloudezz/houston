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
import com.cloudezz.houston.domain.Application;
import com.cloudezz.houston.domain.ClusterConfig;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.util.RepositoryUtils;


public class DeployerVolumeAttachTest extends BaseApplicationContextLoader {

  @Autowired
  private DockerClient dockerClient;

  @Autowired
  private DeployerHelperService deployerHelperService;
  
  private ServiceImageCfg  serviceImageConfig = new ServiceImageCfg();
  
  private Application application = new Application();


  @Before
  public void setup() throws CloudezzDeployException {
    ClusterConfig clusterConfig = new ClusterConfig();
    clusterConfig.setId(RepositoryUtils.generateBigId());
    clusterConfig.setClusterKey(RepositoryUtils.generateBigRandomAlphabetic());
    clusterConfig.setName("test123");
    application.setClusterConfig(clusterConfig);

    DockerHostMachine dockerHostMachine = new DockerHostMachine();
    dockerHostMachine.setIpAddress("127.0.0.1");
    dockerHostMachine.setDockerPort("4243");
    dockerHostMachine.setName("localhost");
    dockerHostMachine.setCloudProviderName("local");
    dockerHostMachine.setHttps(false);
    dockerHostMachine.setSshPort("2222");
    dockerHostMachine.setSudo(true);
    dockerHostMachine.setUsername("vagrant");
    dockerHostMachine.setPassword("vagrant");
    

    serviceImageConfig.setDockerHostMachine(dockerHostMachine);
    serviceImageConfig.setCpuShares(2);
    serviceImageConfig.setDaemon(false);
    serviceImageConfig.setImageName("cloudezz/base");
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
    boolean success = deployerHelperService.startContainer(dockerClient, serviceImageConfig);
    Assert.assertTrue(success);
    ContainerInspectResponse containerInspectResponse =
        dockerClient.inspectContainer(serviceImageConfig.getContainerId());
    Assert.assertTrue(containerInspectResponse.volumes.size() > 0);
    
  }

  @After
  public void cleanup() throws CloudezzDeployException {
    deployerHelperService.destroyAllContainers(dockerClient);
    Assert.assertTrue(dockerClient.getContainersSize() == 0);
  }

}
