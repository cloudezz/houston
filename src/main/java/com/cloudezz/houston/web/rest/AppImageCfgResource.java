package com.cloudezz.houston.web.rest;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.deployer.Deployer;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.repository.AppImageCfgRepository;
import com.cloudezz.houston.repository.DockerHostMachineRepository;
import com.cloudezz.houston.web.rest.dto.AppImageCfgDTO;
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
  private DockerHostMachineRepository dockerHostMachineRepository;

  @Autowired
  private Deployer deployer;

  /**
   * POST /rest/appimagecfgs -> Create a new appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs", method = RequestMethod.POST,
      produces = "application/json", consumes = "application/json")
  @Timed
  public void create(@RequestBody AppImageCfgDTO appimagecfgDto) {
    log.debug("REST request to save AppImageCfg : {}", appimagecfgDto);
    
    AppImageCfg appImageCfg = createAppImageCfg(appimagecfgDto);
    
    if (appImageCfg.getDockerHostMachine() == null) {
      DockerHostMachine dockerHostMachine = dockerHostMachineRepository.getOne("127.0.0.1");
      appImageCfg.setDockerHostMachine(dockerHostMachine);
    }
    appImageCfg.setDockerImageName("cloudezz/tomcat7");
    
    appimagecfgRepository.save(appImageCfg);
  }


  private AppImageCfg createAppImageCfg(AppImageCfgDTO appimagecfgDto) {
    AppImageCfg cfg = new AppImageCfg();
    cfg.setAppName(appimagecfgDto.getAppName());
    cfg.setCpuShares(appimagecfgDto.getCpuShares());
    cfg.setDaemon(appimagecfgDto.getDaemon());
    cfg.setDockerImageName(appimagecfgDto.getImageName());
    cfg.setDomainName(appimagecfgDto.getDomainName());
    cfg.setEnvironmentMapping(appimagecfgDto.getEnvironmentMapping());
    cfg.setGitURL(appimagecfgDto.getGitURL());
    cfg.setHostName(appimagecfgDto.getHostName());
    cfg.setMemory(appimagecfgDto.getMemory());
    cfg.setMemorySwap(appimagecfgDto.getMemorySwap());
    cfg.setPorts(appimagecfgDto.getPorts());
    cfg.setUser(appimagecfgDto.getUser());
    return cfg;
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
        appimagecfgRepository.save(appImageCfg);
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
   * DELETE /rest/appimagecfgs/:id -> delete the "id" appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs/{id}", method = RequestMethod.DELETE,
      produces = "application/json")
  @Timed
  public void delete(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to delete AppImageCfg : {}", id);
    appimagecfgRepository.delete(id);
  }
}
