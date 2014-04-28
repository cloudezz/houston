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
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.Application;
import com.cloudezz.houston.domain.ClusterConfig;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.util.RepositoryUtils;


public class DeployerDeployStartStopConfigTest extends BaseApplicationContextLoader {

  @Autowired
  private DockerClient dockerClient;

  @Autowired
  private DeployerService deployer;

  @Autowired
  private DeployerHelperService deployerHelperService;

  private ServiceImageCfg serviceImageConfig = new ServiceImageCfg();

  private Application application = new Application();


  @Before
  public void setup() throws CloudezzDeployException {

    application.setAppName("test123");

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


    AppImageCfg applicationImageConfig = new AppImageCfg();

    applicationImageConfig.setDockerHostMachine(dockerHostMachine);
    applicationImageConfig.setCpuShares(2);
    applicationImageConfig.setDaemon(false);
    applicationImageConfig.setImageName("cloudezz/tomcat7");
    applicationImageConfig.setHostName("testmachine");
    applicationImageConfig.setMemory(512L);
    applicationImageConfig.setMemorySwap(1024L);
    List<String> ports = new ArrayList<>();
    ports.add("8990");
    applicationImageConfig.setPorts(ports);
    applicationImageConfig.setTty(true);
    application.addAppImageCfgs(applicationImageConfig, 1, application.getAppName());
    Map<String, String> hostToDockervolumeMapping = new HashMap<String, String>();
    hostToDockervolumeMapping.put("/opt/bbytes", "cloudezz/data");
    serviceImageConfig.setHostToDockerVolumeMapping(hostToDockervolumeMapping);


    serviceImageConfig.setDockerHostMachine(dockerHostMachine);
    serviceImageConfig.setCpuShares(2);
    serviceImageConfig.setDaemon(false);
    serviceImageConfig.setImageName("cloudezz/redis");
    serviceImageConfig.setHostName("testmachine");
    serviceImageConfig.setMemory(512L);
    serviceImageConfig.setMemorySwap(1024L);
    List<String> servicePorts = new ArrayList<>();
    servicePorts.add("80");
    servicePorts.add("8009");
    serviceImageConfig.setPorts(servicePorts);
    serviceImageConfig.setTty(true);
    serviceImageConfig.setHostToDockerVolumeMapping(hostToDockervolumeMapping);
    application.addServiceImageCfgs(serviceImageConfig, 1);

  }

  @Test
  public void deployImage() throws Exception {

    boolean success = deployer.restart(application);
    Assert.assertTrue(success);

    success = deployer.start(application);
    Assert.assertTrue(success);
    ContainerInspectResponse containerInspectResponse =
        dockerClient.inspectContainer(application.getAppImageCfgs().iterator().next()
            .getContainerId());
    Assert.assertTrue(containerInspectResponse.state.running);

    success = deployer.stop(application);
    Assert.assertTrue(success);

    containerInspectResponse =
        dockerClient.inspectContainer(application.getAppImageCfgs().iterator().next()
            .getContainerId());
    Assert.assertTrue(!containerInspectResponse.state.running);

  }



  @After
  public void cleanup() throws CloudezzDeployException {
    deployerHelperService.destroyAllContainers(dockerClient);
    Assert.assertTrue(dockerClient.getContainersSize() == 0);
  }

}
