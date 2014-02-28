package com.cloudezz.houston.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloudezz.houston.deployer.Deployer;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.repository.AppImageCfgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing AppImageCfg.
 */
@RestController
@RequestMapping("/app")
public class AppImageCfgResource {

  private final Logger log = LoggerFactory.getLogger(AppImageCfgResource.class);

  @Inject
  private AppImageCfgRepository appimagecfgRepository;

  @Autowired
  private Deployer deployer;

  /**
   * POST /rest/appimagecfgs -> Create a new appimagecfg.
   */
  @RequestMapping(value = "/rest/appimagecfgs", method = RequestMethod.POST,
      produces = "application/json", consumes = "application/json")
  @Timed
  public void create(@RequestBody AppImageCfg appimagecfg) {
    log.debug("REST request to save AppImageCfg : {}", appimagecfg);
    appimagecfgRepository.save(appimagecfg);
  }


  @RequestMapping(value = "/rest/appimagecfgs/:id", method = RequestMethod.POST,
      produces = "application/json")
  @Timed
  public boolean start(@PathVariable String id, HttpServletResponse response) {
    AppImageCfg appImageCfg = appimagecfgRepository.findOne(id);
    if (appImageCfg == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    boolean status = false;
    try {
      status = deployer.start(appImageCfg);
    } catch (CloudezzDeployException e) {
      log.error(e.getMessage());
    }
    return status;
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
