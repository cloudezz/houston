package com.cloudezz.houston.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.domain.ApplicationImageConfig;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ServiceImageConfig;

/**
 * Test class for the all image config related repository.
 * 
 */
public class ImageConfigRepositoryTest extends BaseApplicationContextLoader {


  @Autowired
  private AppImageConfigRepository appImageConfigRepository;
  
  @Autowired
  private DockerHostMachineRepository dockerHostMachineRepository;

  private ServiceImageConfig serviceImageConfig = new ServiceImageConfig();

  private ApplicationImageConfig applicationImageConfig = new ApplicationImageConfig();
  
  private  DockerHostMachine dockerHostMachine = new DockerHostMachine();

  @Before
  public void setup() throws CloudezzDeployException {

    dockerHostMachine.setIpAddress("localhost");
    dockerHostMachine.setDockerPort("4243");
    dockerHostMachine.setCloudProviderName("my local machine");

    applicationImageConfig.setAppName(UUID.randomUUID().toString());
    applicationImageConfig.setDockerHostMachine(dockerHostMachine);
    applicationImageConfig.setCpuShares(2);
    applicationImageConfig.setDaemon(false);
    applicationImageConfig.setDockerImageName("cloudezz/tomcat7");
    applicationImageConfig.setHostName("testmachine");
    applicationImageConfig.setMemory(512L);
    applicationImageConfig.setMemorySwap(1024L);
    List<String> ports = new ArrayList<String>();
    ports.add("8990");
    applicationImageConfig.setPorts(ports);
    applicationImageConfig.setTty(true);
    applicationImageConfig.addServiceImages(serviceImageConfig);
    Map<String, String> hostToDockervolumeMapping = new HashMap<String, String>();
    hostToDockervolumeMapping.put("/opt/bbytes", "cloudezz/data");
    serviceImageConfig.setHostToDockerVolumeMapping(hostToDockervolumeMapping);


    serviceImageConfig.setServiceName(UUID.randomUUID().toString());
    serviceImageConfig.setDockerHostMachine(dockerHostMachine);
    serviceImageConfig.setCpuShares(2);
    serviceImageConfig.setDaemon(false);
    serviceImageConfig.setDockerImageName("cloudezz/base");
    serviceImageConfig.setHostName("testmachine");
    serviceImageConfig.setMemory(512L);
    serviceImageConfig.setMemorySwap(1024L);
    List<String> servicePorts = new ArrayList<String>();
    servicePorts.add("80");
    servicePorts.add("8009");
    serviceImageConfig.setPorts(servicePorts);
    serviceImageConfig.setTty(true);
    serviceImageConfig.setHostToDockerVolumeMapping(hostToDockervolumeMapping);
  }

  @Test
  public void testRemoveOldPersistentData() {
    
    dockerHostMachine = dockerHostMachineRepository.save(dockerHostMachine);
    
    applicationImageConfig = appImageConfigRepository.saveAndFlush(applicationImageConfig);
    Assert.assertNotNull(applicationImageConfig);
  }


}
