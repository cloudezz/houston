package com.cloudezz.houston.deployer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.domain.ApplicationImageConfig;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ServiceImageConfig;
import com.google.common.base.Preconditions;

@Component
public class DeployerImpl implements Deployer {

  @Override
  public boolean deploy(ApplicationImageConfig appImageConfig) throws CloudezzDeployException {
    DockerHostMachine hostMachine = appImageConfig.getDockerHostMachine();
    Preconditions.checkNotNull(hostMachine, "Docker Host Machine information has to be set on App Image config");
    
    DockerClient dockerClient = new DockerClient(appImageConfig.getDockerHostMachine().getDockerDaemonURL());
    List<String> containerIdCache = new ArrayList<String>(); 
    if(dockerClient.isDockerHostAvailable())
    {
      for (ServiceImageConfig serviceImageConfig : appImageConfig.getServiceImages()) {
        boolean success = DeployerUtil.startContainer(dockerClient, serviceImageConfig);
        
      }
    }
    
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
