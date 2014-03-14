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

import com.cloudezz.houston.deployer.Deployer;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.CloudezzException;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.domain.ExposedService;
import com.cloudezz.houston.domain.FileMeta;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.AppImageCfgRepository;
import com.cloudezz.houston.repository.DockerHostMachineRepository;
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
  private AppImageCfgRepository appimagecfgRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private DockerHostMachineRepository dockerHostMachineRepository;

  @Autowired
  private Deployer deployer;

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
      AppImageCfg appImageCfg = createAppImageCfg(appimagecfgDto);
      
      // set owner
      String email = SecurityUtils.getCurrentLogin();
      User currentUser = userRepository.getOne(email);
      appImageCfg.setOwner(currentUser);

      if (appImageCfg.getDockerHostMachine() == null) {
        DockerHostMachine dockerHostMachine = dockerHostMachineRepository.getOne("127.0.0.1");
        appImageCfg.setDockerHostMachine(dockerHostMachine);

        appImageCfg = appimagecfgRepository.saveAndFlush(appImageCfg);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

  }


  private AppImageCfg createAppImageCfg(AppImageCfgDTO appimagecfgDto)
      throws CloudezzDeployException {
    AppImageCfg appCfg = new AppImageCfg();
    appCfg.setAppName(appimagecfgDto.getAppName());
    appCfg.setCpuShares(appimagecfgDto.getCpuShares());
    appCfg.setDaemon(appimagecfgDto.getDaemon());
    appCfg.setDockerImageName(appimagecfgDto.getImageName());
    appCfg.setDomainName(appimagecfgDto.getDomainName());
    appCfg.setEnvironmentMapping(appimagecfgDto.getEnvironmentMapping());
    appCfg.setHostName(appimagecfgDto.getHostName());
    appCfg.setMemory(appimagecfgDto.getMemory());
    appCfg.setMemorySwap(appimagecfgDto.getMemorySwap());
    appCfg.setPorts(appimagecfgDto.getPorts());

    if (appimagecfgDto.getServiceImages() != null) {
      for (ServiceImageCfgDTO serviceImageCfgDTO : appimagecfgDto.getServiceImages()) {
        ServiceImageCfg serviceImageCfg = new ServiceImageCfg();
        serviceImageCfg.setServiceName(serviceImageCfgDTO.getAppName());
        serviceImageCfg.setCpuShares(serviceImageCfgDTO.getCpuShares());
        serviceImageCfg.setDaemon(serviceImageCfgDTO.getDaemon());
        serviceImageCfg.setDockerImageName(serviceImageCfgDTO.getImageName());
        serviceImageCfg.setDomainName(serviceImageCfgDTO.getDomainName());
        serviceImageCfg.setEnvironmentMapping(serviceImageCfgDTO.getEnvironmentMapping());
        serviceImageCfg.setHostName(serviceImageCfgDTO.getHostName());
        serviceImageCfg.setMemory(serviceImageCfgDTO.getMemory());
        serviceImageCfg.setMemorySwap(serviceImageCfgDTO.getMemorySwap());
        serviceImageCfg.setPorts(appimagecfgDto.getPorts());
        serviceImageCfg.setApplicationImageConfig(appCfg);
        // add the service images to the main app cfg
        appCfg.addServiceImages(serviceImageCfg);
      }
    }

    return appCfg;
  }


  @RequestMapping(value = "/rest/appimagecfgs/start/{id}", method = RequestMethod.POST)
  @Timed
  public boolean start(@PathVariable String id, HttpServletResponse response) {
    AppImageCfg appImageCfg = appimagecfgRepository.findOne(id);
    if (appImageCfg == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    boolean success = false;
    try {
      success = deployer.start(appImageCfg);
      if (success) {
        appImageCfg.setRunning(true);
        // set the exposed service after start of the image
        try {
          ExposedService exposedService = imageService.getExposedService(appImageCfg);
          appImageCfg.setExposedService(exposedService);
        } catch (CloudezzException e) {
          log.error(e.getMessage());
        }
        appImageCfg = appimagecfgRepository.save(appImageCfg);
      }

    } catch (CloudezzDeployException e) {
      log.error(e.getMessage());
    }
    return success;
  }


  @RequestMapping(value = "/rest/appimagecfgs/stop/{id}", method = RequestMethod.POST)
  @Timed
  public boolean stop(@PathVariable String id, HttpServletResponse response) {
    AppImageCfg appImageCfg = appimagecfgRepository.findOne(id);
    if (appImageCfg == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    boolean success = false;
    try {
      success = deployer.stop(appImageCfg);
      if (success) {
        appImageCfg.setRunning(false);
        appimagecfgRepository.save(appImageCfg);
      }
    } catch (CloudezzDeployException e) {
      log.error(e.getMessage());
    }
    return success;
  }


  /**
   * GET /rest/appimagecfgs -> get all the appimagecfgs.
   */
  @RequestMapping(value = "/rest/appimagecfgs", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public List<AppImageCfg> getAll() {
    log.debug("REST request to get all AppImageCfgs");
    try {
      return appimagecfgRepository.findAll();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }



  /**
   * GET /rest/appimagecfgs/:id -> get the "id" appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public AppImageCfg get(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get AppImageCfg : {}", id);
    AppImageCfg appimagecfg = appimagecfgRepository.findOne(id);
    if (appimagecfg == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return appimagecfg;
  }


  /**
   * GET /rest/appimagecfgs/:id -> get the "id" appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs/{id}/service", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public ExposedService getServiceExposed(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get AppImageCfg : {}", id);
    AppImageCfg appImgCfg = appimagecfgRepository.findOne(id);
    if (appImgCfg == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    try {
      return imageService.getExposedService(appImgCfg);
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
      AppImageCfg appImgCfg = appimagecfgRepository.findOne(id);
      if (appImgCfg == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }
      deployer.delete(appImgCfg);
    } catch (CloudezzDeployException e) {
      log.error("Failed during delete app cfg", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    appimagecfgRepository.delete(id);
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
        FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream(
            "C:/Users/Deeps/AppData/Local/Temp/" + mpf.getOriginalFilename()));
      } catch (IOException e) {
        e.printStackTrace();
      }
      files.add(fileMeta);
    }
    return files;
  }

}
