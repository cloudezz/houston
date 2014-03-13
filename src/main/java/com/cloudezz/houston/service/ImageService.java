package com.cloudezz.houston.service;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.CloudezzException;
import com.cloudezz.houston.deployer.docker.client.DockerClient;
import com.cloudezz.houston.deployer.docker.client.DockerClientException;
import com.cloudezz.houston.deployer.docker.client.DockerConstant;
import com.cloudezz.houston.deployer.docker.client.utils.DockerUtil;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ExposedService;
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.domain.ImgSettings.PortConfig.Port;
import com.cloudezz.houston.repository.DockerHostMachineRepository;
import com.cloudezz.houston.repository.ImageInfoRepository;
import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Preconditions;

@Service
public class ImageService {

  private final Logger log = LoggerFactory.getLogger(ImageService.class);

  @Autowired
  private ImageInfoRepository imageInfoRepository;

  @Autowired
  private DockerHostMachineRepository dockHostMachineRepository;


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
      // need to create proper pojo for img settings to get port
      exposedService.setContainerId(appImageCfg.getContainerId());
      exposedService.setName(appImageCfg.getAppName());
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

  /**
   * This cron job pulls all the images registered in cloudezz database to the all the registered
   * docker host machine . The images are pulled from central docker repo.
   */
  // cron to run every midnight to pull image to all docker host
  @Scheduled(cron = "0 0 0 * * ?")
  @Timed
  public void pullImagesToDockerHost() {
    log.debug("Started the cron job that pulls images in all docker images");
    List<ImageInfo> images = imageInfoRepository.findAll();
    if (images == null || images.size() == 0)
      return;

    List<DockerHostMachine> dockerHostMachines = dockHostMachineRepository.findAll();
    for (DockerHostMachine dockerHostMachine : dockerHostMachines) {
      DockerClient dockerClient = new DockerClient(dockerHostMachine.getDockerDaemonURL());
      for (ImageInfo image : images) {
        try {
          dockerClient.pull(image.getImageName());
        } catch (DockerClientException e) {
          log.error(
              "Error while pulling images from docker host "
                  + dockerHostMachine.getDockerDaemonURL(), e);
        }
      }

    }
  }


}
