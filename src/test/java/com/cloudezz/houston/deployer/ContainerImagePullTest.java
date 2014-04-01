package com.cloudezz.houston.deployer;



import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.client.DockerClientException;


public class ContainerImagePullTest extends BaseApplicationContextLoader {

  @Autowired
  private DockerClient dockerClient;
  
  @Autowired
  private DeployerHelperService deployerHelperService;
  
  private static final String imageName = "busybox";

  @Before
  public void setup() throws DockerClientException {
    List<String> images = dockerClient.getAllImages();
    if (images.contains(imageName))
      dockerClient.removeImage(imageName);
  }

  @Test
  public void imagePull() throws Exception {
    List<String> images = dockerClient.getAllImages();
    Assert.assertTrue(!images.contains(imageName));
    
    deployerHelperService.checkAndPullImage(dockerClient, imageName);
    
    images = dockerClient.getAllImages();
    Assert.assertTrue(images.contains(imageName));

  }
}
