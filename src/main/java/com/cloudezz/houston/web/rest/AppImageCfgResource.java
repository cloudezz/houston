package com.cloudezz.houston.web.rest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.cloudezz.houston.deployer.DeployerService;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.CloudezzException;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.Application;
import com.cloudezz.houston.domain.ClusterConfig;
import com.cloudezz.houston.domain.ExposedService;
import com.cloudezz.houston.domain.FileMeta;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.ApplicationRepository;
import com.cloudezz.houston.repository.DockerHostMachineRepository;
import com.cloudezz.houston.repository.RepositoryUtils;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.security.SecurityUtils;
import com.cloudezz.houston.service.ImageService;
import com.cloudezz.houston.web.rest.dto.AppImageCfgDTO;
import com.cloudezz.houston.web.rest.dto.ServiceImageCfgDTO;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing AppImageCfg.
 */
@RestController
@RequestMapping("/app")
public class AppImageCfgResource {

  private final Logger log = LoggerFactory.getLogger(AppImageCfgResource.class);

  @Inject
  private ApplicationRepository applicationRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private DockerHostMachineRepository dockerHostMachineRepository;

  @Autowired
  private DeployerService deployer;

  @Autowired
  private ImageService imageService;

  /**
   * POST /rest/appimagecfgs -> Create a new appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs", method = RequestMethod.POST,
      produces = "application/json", consumes = "application/json")
  @Timed
  @Transactional
  public void create(@RequestBody AppImageCfgDTO appimagecfgDto) {
    log.debug("REST request to save AppImageCfg : {}", appimagecfgDto);

    try {
      Application application = createApplication(appimagecfgDto);

      // set owner
      String email = SecurityUtils.getCurrentLogin();
      User currentUser = userRepository.getOne(email);
      application.setOwner(currentUser);
      
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

  }


  private Application createApplication(AppImageCfgDTO appimagecfgDto)
      throws CloudezzDeployException {
    Application application = new Application();

    ClusterConfig clusterConfig = new ClusterConfig();
    clusterConfig.setId(RepositoryUtils.generateBigId());
    clusterConfig.setClusterKey(RepositoryUtils.generateBigRandomAlphabetic());
    clusterConfig.setName(appimagecfgDto.getAppName());
    application.setClusterConfig(clusterConfig);

    AppImageCfg appCfg = new AppImageCfg();
    appCfg.setAppName(appimagecfgDto.getAppName());
    appCfg.setCpuShares(appimagecfgDto.getCpuShares());
    appCfg.setDaemon(appimagecfgDto.getDaemon());
    appCfg.setImageName(appimagecfgDto.getImageName());
    appCfg.setDomainName(appimagecfgDto.getDomainName());
    appCfg.setEnvironmentMapping(appimagecfgDto.getEnvironmentMapping());
    appCfg.setHostName(appimagecfgDto.getHostName());
    appCfg.setMemory(appimagecfgDto.getMemory());
    appCfg.setMemorySwap(appimagecfgDto.getMemorySwap());
    appCfg.setDockerHostMachine(dockerHostMachineRepository.getOne("127.0.0.1"));
    application.addAppImageCfgs(appCfg, appimagecfgDto.getNoOfInstance());

    // set the ports that are to be exposed from image info exposer ports.
    imageService.setExposedPorts(appCfg, appimagecfgDto.getImageName());

    appCfg.setInitScript(appimagecfgDto.getInitScript());

    if (appimagecfgDto.getServiceImages() != null) {
      for (ServiceImageCfgDTO serviceImageCfgDTO : appimagecfgDto.getServiceImages()) {
        ServiceImageCfg serviceImageCfg = new ServiceImageCfg();
        serviceImageCfg.setServiceName(serviceImageCfgDTO.getAppName());
        serviceImageCfg.setCpuShares(serviceImageCfgDTO.getCpuShares());
        serviceImageCfg.setDaemon(serviceImageCfgDTO.getDaemon());
        serviceImageCfg.setImageName(serviceImageCfgDTO.getImageName());
        serviceImageCfg.setDomainName(serviceImageCfgDTO.getDomainName());
        serviceImageCfg.setEnvironmentMapping(serviceImageCfgDTO.getEnvironmentMapping());
        serviceImageCfg.setHostName(serviceImageCfgDTO.getHostName());
        serviceImageCfg.setMemory(serviceImageCfgDTO.getMemory());
        serviceImageCfg.setMemorySwap(serviceImageCfgDTO.getMemorySwap());
        serviceImageCfg.setPorts(appimagecfgDto.getPorts());
        serviceImageCfg.setDockerHostMachine(dockerHostMachineRepository.getOne("127.0.0.1"));
        // set the ports that are to be exposed from image info exposer ports.
        imageService.setExposedPorts(serviceImageCfg, serviceImageCfgDTO.getImageName());
        application.addServiceImageCfgs(serviceImageCfg, serviceImageCfgDTO.getNoOfInstance());
      }
    }

    return application;
  }


  @RequestMapping(value = "/rest/appimagecfgs/start/{id}", method = RequestMethod.POST)
  @Timed
  public boolean start(@PathVariable String id, HttpServletResponse response) {
    Application  application= applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    boolean success = false;
    try {
      success = deployer.start(application);
      if (!success)
        return false;

      application.setRunning(true);
      // set the exposed service after start of the image
      try {
        List<ExposedService> exposedService = imageService.getExposedService(application);
        application.setExposedServices(exposedService);
      } catch (CloudezzException e) {
        log.error(e.getMessage());
      }
      application = applicationRepository.save(application);

    } catch (CloudezzDeployException e) {
      log.error(e.getMessage(), e);
    }
    return success;
  }


  @RequestMapping(value = "/rest/appimagecfgs/stop/{id}", method = RequestMethod.POST)
  @Timed
  public boolean stop(@PathVariable String id, HttpServletResponse response) {
    Application application = applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    boolean success = false;
    try {
      success = deployer.stop(application);
      if (success) {
        application.setRunning(false);
        applicationRepository.save(application);
      }
    } catch (CloudezzDeployException e) {
      log.error(e.getMessage(), e);
    }
    return success;
  }


  /**
   * GET /rest/appimagecfgs -> get all the appimagecfgs.
   */
  @RequestMapping(value = "/rest/appimagecfgs", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public List<Application> getAllForCurrentUser() {
    log.debug("REST request to get all AppImageCfgs");
    try {
      User currentLoggedInUser = userRepository.getOne(SecurityUtils.getCurrentLogin());
      return applicationRepository.getAllForUser(currentLoggedInUser);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }



  /**
   * GET /rest/appimagecfgs/:id -> get the "id" appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public Application get(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get AppImageCfg : {}", id);
    Application application = applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return application;
  }


  /**
   * GET /rest/appimagecfgs/:id -> get the "id" appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs/{id}/service", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public List<ExposedService> getServiceExposed(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get AppImageCfg : {}", id);
    Application application = applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    try {
      return imageService.getExposedService(application);
    } catch (CloudezzException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return null;

  }


  /**
   * DELETE /rest/appimagecfgs/:id -> delete the "id" appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs/{id}", method = RequestMethod.DELETE,
      produces = "application/json")
  @Timed
  public void delete(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to delete AppImageCfg : {}", id);
    try {
      Application application = applicationRepository.findOne(id);
      if (application == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      } else {
        deployer.delete(application);
        applicationRepository.delete(id);
      }
    } catch (CloudezzDeployException e) {
      log.error("Failed during delete app cfg", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

  }

  LinkedList<FileMeta> files = new LinkedList<FileMeta>();
  FileMeta fileMeta = null;

  @RequestMapping(value = "/rest/upload", method = RequestMethod.POST)
  public @ResponseBody
  LinkedList<FileMeta> upload(MultipartHttpServletRequest request, HttpServletResponse response) {
    Iterator<String> itr = request.getFileNames();
    MultipartFile mpf = null;
    while (itr.hasNext()) {
      mpf = request.getFile(itr.next());
      System.out.println((mpf.getOriginalFilename() + "uploaded!" + files.size()));
      if (files.size() >= 10)
        files.poll();
      fileMeta = new FileMeta();
      fileMeta.setFileName(mpf.getOriginalFilename());
      fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
      fileMeta.setFileType(mpf.getContentType());
      try {
        fileMeta.setBytes(mpf.getBytes());
        FileCopyUtils.copy(mpf.getBytes(),
            new FileOutputStream(System.getProperty("java.io.tmpdir") + mpf.getOriginalFilename()));
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      files.add(fileMeta);
    }
    return files;
  }

}
