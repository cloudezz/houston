package com.cloudezz.houston.service;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.CloudezzException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.client.DockerConstant;
import com.cloudezz.houston.deployer.docker.client.utils.DockerUtil;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ExposedService;
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.domain.ImgSettings.PortConfig.Port;
import com.cloudezz.houston.repository.ImageInfoRepository;
import com.google.common.base.Preconditions;

@Service
public class ImageService {

  @Autowired
  private ImageInfoRepository imageInfoRepository;

  public ExposedService getExposedService(AppImageCfg appImageCfg) throws CloudezzException {
    
    Preconditions.checkNotNull(appImageCfg, "App Image cfg arg cannot be null");
    
    if (appImageCfg.getContainerId() == null && !appImageCfg.isRunning()) {
      throw new CloudezzException(
          "Cannot get exposed service information from a instance that is not running");
    }
    
    ExposedService exposedService = new ExposedService();
    try {
      ImageInfo imageInfo = imageInfoRepository.findByImageName(appImageCfg.getDockerImageName());
      if (imageInfo == null) {
        throw new CloudezzException("Image info not available");
      }
      List<Port> ports = imageInfo.getPortsExposed();
//      need to create proper pojo for img settings to get port
      exposedService.setContainerId(appImageCfg.getContainerId());
      exposedService.setAppImageCfg(appImageCfg);
      DockerHostMachine dockerHostMachine = appImageCfg.getDockerHostMachine();
      DockerClient dockerClient = DockerUtil.getDockerClient(dockerHostMachine);
      ContainerInspectResponse containerInspectResponse =
          dockerClient.inspectContainer(appImageCfg.getContainerId());
      Map<String, HostPortBinding[]> hostPortBindings =
          containerInspectResponse.networkSettings.ports;
      if (hostPortBindings != null) {
        for (String dockerPort : hostPortBindings.keySet()) {
          HostPortBinding[] hostPortBinds = hostPortBindings.get(dockerPort);
          if (hostPortBinds != null && hostPortBinds.length > 0) {
            checkDefaultPorts(dockerPort, hostPortBinds, dockerHostMachine, exposedService);
            for (Port port : ports) {
              String portValue = port.getDefaultValue() + "/tcp";
              if (dockerPort.equals(portValue)) {
                String url = "http://";
                if (dockerHostMachine.isHttps()) {
                  url = "https://";
                }
                url = url + dockerHostMachine.getHostName() + ":" + hostPortBinds[0].getHostPort();
                exposedService.addServiceToURL(port.getDisplayName(), url);
              }
            }
          }
        }
      }
    } catch (CloudezzDeployException | JAXBException e) {
      throw new CloudezzException(e);
    }

    return exposedService;
  }

  private void checkDefaultPorts(String dockerPort, HostPortBinding hostPortBinds[],
      DockerHostMachine dockerHostMachine, ExposedService exposedService) {
    if (dockerPort.equals(DockerConstant.DEFAULT_SSH_PORT)) {
      exposedService.addServiceToURL(DockerConstant.SSH_SERVICE_NAME, "ssh://root@"
          + dockerHostMachine.getHostName() + ":" + hostPortBinds[0].getHostPort());
    } else if (dockerPort.equals(DockerConstant.DEFAULT_WEB_SHELL_PORT)) {
      String url = "http://";
      if (dockerHostMachine.isHttps()) {
        url = "https://";
      }
      url = url + dockerHostMachine.getHostName() + ":" + hostPortBinds[0].getHostPort();
      exposedService.addServiceToURL(DockerConstant.WEB_SHELL_SERVICE_NAME, url);
    }
  }
}
