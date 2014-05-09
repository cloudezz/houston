package com.cloudezz.houston.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import com.cloudezz.houston.domain.Application;
import com.cloudezz.houston.domain.BaseImageCfg;
import com.cloudezz.houston.domain.Container;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ExposedService;
import com.cloudezz.houston.domain.ImageInfo;
import com.cloudezz.houston.domain.ImgSettings.PortConfig.Port;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.firework.domain.Node;
import com.cloudezz.houston.repository.ApplicationRepository;
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

  @Autowired
  private ApplicationRepository applicationRepository;

  @Autowired
  private Environment env;


  public Set<ExposedService> getExposedService(Application application) throws CloudezzException {

    Preconditions.checkNotNull(application, "Application arg cannot be null");

    Set<ExposedService> exposedServices = new LinkedHashSet<>();

    for (AppImageCfg appImageCfg : application.getAppImageCfgs()) {

      if (appImageCfg.getContainer() == null && !application.isRunning()) {
        throw new CloudezzException(
            "Cannot get exposed service information from a instance that is not running");
      }

      try {
        ImageInfo imageInfo = imageInfoRepository.findByImageName(appImageCfg.getImageName());
        if (imageInfo == null) {
          throw new CloudezzException("Image info not available");
        }
        ExposedService exposedService = new ExposedService();
        List<Port> ports = imageInfo.getPortsExposed();
        // need to create proper pojo for img settings to get port
        exposedService.setContainerId(appImageCfg.getContainer().getId());
        exposedService.setName(appImageCfg.getAppName());
        exposedService.setServiceImage(false);
        exposedService.setInstanceNo(appImageCfg.getInstanceNo());
        DockerHostMachine dockerHostMachine = appImageCfg.getDockerHostMachine();
        DockerClient dockerClient = DockerUtil.getDockerClient(dockerHostMachine);
        ContainerInspectResponse containerInspectResponse =
            dockerClient.inspectContainer(appImageCfg.getContainer().getId());


        Map<String, HostPortBinding[]> hostPortBindings =
            containerInspectResponse.networkSettings.ports;
        if (hostPortBindings != null) {
          for (String dockerPort : hostPortBindings.keySet()) {
            HostPortBinding[] hostPortBinds = hostPortBindings.get(dockerPort);
            if (hostPortBinds != null && hostPortBinds.length > 0) {
              checkDefaultPorts(dockerPort, hostPortBinds, dockerHostMachine, appImageCfg,
                  exposedService);
              application.addAppImageCfgs(appImageCfg);
              for (Port port : ports) {
                String portValue = port.getValue() + "/tcp";
                if (dockerPort.equals(portValue)) {
                  String url = "http://";
                  if (dockerHostMachine.isHttps()) {
                    url = "https://";
                  }
                  url =
                      url + dockerHostMachine.getHostName() + ":" + hostPortBinds[0].getHostPort();
                  exposedService.addServiceToURL(port.getDisplayName(), url);
                }
              }
            }
          }
        }
        exposedServices.add(exposedService);

      } catch (CloudezzDeployException | JAXBException e) {
        throw new CloudezzException(e);
      }
    }

    return exposedServices;

  }

  public void updateContainer(Application application) throws CloudezzException {
    try {

      for (AppImageCfg appImageCfg : application.getAppImageCfgs()) {

        if (appImageCfg.getContainer() == null && !application.isRunning()) {
          throw new CloudezzException("Cannot update container for a instance that is not running");
        }


        DockerHostMachine dockerHostMachine = appImageCfg.getDockerHostMachine();
        DockerClient dockerClient = DockerUtil.getDockerClient(dockerHostMachine);
        ContainerInspectResponse containerInspectResponse =
            dockerClient.inspectContainer(appImageCfg.getContainer().getId());

        Container container = appImageCfg.getContainer();
        container.setHost(containerInspectResponse.networkSettings.ipAddress);
        container.setName(containerInspectResponse.name);
        container.setSSHUsername(DockerConstant.DEFAULT_USER);
        container.setSSHPort(getSSHPort(containerInspectResponse.networkSettings.ports));
        container.setSSHPassword(getSSHPassword(containerInspectResponse.getConfig().getEnv()));
        appImageCfg.setContainer(container);
        application.addAppImageCfgs(appImageCfg);
      }

      for (ServiceImageCfg serviceImageCfg : application.getServiceImageCfgs()) {

        if (serviceImageCfg.getContainer() == null && !application.isRunning()) {
          throw new CloudezzException("Cannot update container for a instance that is not running");
        }

        DockerHostMachine dockerHostMachine = serviceImageCfg.getDockerHostMachine();
        DockerClient dockerClient = DockerUtil.getDockerClient(dockerHostMachine);
        ContainerInspectResponse containerInspectResponse =
            dockerClient.inspectContainer(serviceImageCfg.getContainer().getId());

        Container container = serviceImageCfg.getContainer();
        container.setHost(containerInspectResponse.networkSettings.ipAddress);
        container.setName(containerInspectResponse.name);
        container.setSSHPort(getSSHPort(containerInspectResponse.networkSettings.ports));
        container.setSSHPassword(getSSHPassword(containerInspectResponse.getConfig().getEnv()));
        serviceImageCfg.setContainer(container);
        application.addServiceImageCfgs(serviceImageCfg);
      }

      applicationRepository.saveAndFlush(application);

    } catch (CloudezzDeployException e) {
      throw new CloudezzException(e);
    }
  }

  private Integer getSSHPort(Map<String, HostPortBinding[]> ports) {
    Map<String, HostPortBinding[]> hostPortBindings = ports;
    if (hostPortBindings != null) {
      for (String dockerPort : hostPortBindings.keySet()) {
        HostPortBinding[] hostPortBinds = hostPortBindings.get(dockerPort);
        if (hostPortBinds != null && hostPortBinds.length > 0) {
          if (dockerPort.equals(DockerConstant.DEFAULT_SSH_PORT)) {
            return Integer.parseInt(hostPortBinds[0].getHostPort());
          }
        }
      }
    }
    return 0;
  }

  private String getSSHPassword(String[] envList) {
    if (envList == null)
      return "";

    for (int i = 0; i < envList.length; i++) {
      String env[] = envList[i].split("=");
      if (env[0].equals(DockerConstant.ENV_SSH_ROOT_PASSWORD)) {
        return env[1];
      }
    }
    return "";
  }

  public void setExposedPorts(BaseImageCfg baseImageCfg, String imageName) {
    ImageInfo imageInfo = imageInfoRepository.findByImageName(imageName);
    if (imageInfo == null)
      return;

    baseImageCfg.setPorts(imageInfo.getDefaultPorts());

    if (imageInfo.getExposedPorts() == null)
      return;

    baseImageCfg.getPorts().addAll(imageInfo.getExposedPorts());
  }

  public void setDefaultEnvMapping(BaseImageCfg baseImageCfg, String imageName) {
    ImageInfo imageInfo = imageInfoRepository.findByImageName(imageName);
    if (imageInfo == null)
      return;

    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_HOST_IP, baseImageCfg
        .getDockerHostMachine().getIpAddress());
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_IS_SERVICE,
        ((Boolean) (baseImageCfg instanceof ServiceImageCfg)).toString());
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_SERF_CLUSTER_ID, baseImageCfg
        .getApplication().getClusterConfig().getId().toString());
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_SERF_CLUSTER_KEY, baseImageCfg
        .getApplication().getClusterConfig().getClusterKey());
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_SERF_HOST_PORT, baseImageCfg
        .getDockerPortToHostPort().get(DockerConstant.DEFAULT_SERF_PORT));
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_SERF_ROLE, imageInfo.getRole());

    // setting the houston ip address and port as env variable for machine to query the cluster
    // leader
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_HOUSTON_PORT,
        env.getProperty(DockerConstant.PROP_HOUSTON_PORT));
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_HOUSTON_PROTOCOL,
        DockerConstant.ENV_HOUSTON_PROTOCOL_VALUE);

    if ("0.0.0.0".equals(env.getProperty(DockerConstant.PROP_HOUSTON_IP))) {
      try {
        baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_HOUSTON_HOST_IP, InetAddress
            .getLocalHost().getHostAddress());
      } catch (UnknownHostException e) {
        log.error(e.getMessage());
        baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_HOUSTON_HOST_IP,
            env.getProperty(DockerConstant.PROP_HOUSTON_IP));
      }
    } else {
      baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_HOUSTON_HOST_IP,
          env.getProperty(DockerConstant.PROP_HOUSTON_IP));
    }



    if (imageInfo.getExposedPorts() == null || imageInfo.getExposedPorts().size() == 0)
      return;

    String hostExpostedPort = "";
    int size = imageInfo.getExposedPorts().size();
    int counter = 0;
    for (Iterator<String> iterator = imageInfo.getExposedPorts().iterator(); iterator.hasNext();) {
      String dockerPort = iterator.next();
      String hostPort = baseImageCfg.getDockerPortToHostPort().get(dockerPort);
      counter++;
      if (counter == size)
        hostExpostedPort = hostExpostedPort + dockerPort + ":" + hostPort;
      else
        hostExpostedPort = hostExpostedPort + dockerPort + ":" + hostPort + ",";

    }
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_DEFAULT_PORT_TO_EXPOSE,
        imageInfo.getExposedImagePorts());
    baseImageCfg.addEnvironmentMapping(DockerConstant.ENV_DEFAULT_HOST_PORT_TO_EXPOSE,
        hostExpostedPort);
  }

  private void checkDefaultPorts(String dockerPort, HostPortBinding hostPortBinds[],
      DockerHostMachine dockerHostMachine, AppImageCfg appImageCfg, ExposedService exposedService) {
    if (dockerPort.equals(DockerConstant.DEFAULT_SSH_PORT)) {
      exposedService.addServiceToURL(DockerConstant.SSH_SERVICE_NAME, "ssh://"
          + DockerConstant.DEFAULT_USER + "@" + dockerHostMachine.getHostName() + ":"
          + hostPortBinds[0].getHostPort());
    } else if (dockerPort.equals(DockerConstant.DEFAULT_WEB_SHELL_PORT)
        && appImageCfg.getEnvironmentMapping().containsKey(DockerConstant.ENV_WEB_SHELL)) {
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


  /**
   * Get the node for the base image of the container is available
   * 
   * @param baseImageCfg
   * @return
   */
  public Node getNodeForImageConfig(BaseImageCfg baseImageCfg) {
    Preconditions.checkNotNull(baseImageCfg, "BaseImageCfg arg cannot be null");
    Preconditions.checkNotNull(baseImageCfg.getContainer(), "Container not available");
    Container container = baseImageCfg.getContainer();
    Node node = new Node();
    node.setHost(container.getHost());
    node.setName(container.getName());
    node.setSSHPassword(container.getSSHPassword());
    node.setSSHPemPrivateKey(container.getSSHPemPrivateKey());
    node.setSSHPemPrivateKeyPassword(container.getSSHPemPrivateKeyPassword());
    node.setSSHPort(container.getSSHPort());
    node.setSSHUsername(container.getSSHUsername());
    node.setSudo(false);

    return node;

  }

}
