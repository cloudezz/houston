package com.cloudezz.houston.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.repository.ServiceImageCfgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing ServiceImageCfg.
 */
@RestController
@RequestMapping("/app")
public class ServiceImageCfgResource {

    private final Logger log = LoggerFactory.getLogger(ServiceImageCfgResource.class);

    @Inject
    private ServiceImageCfgRepository serviceimagecfgRepository;

    /**
     * POST  /rest/serviceimagecfgs -> Create a new serviceimagecfg.
     */
    @RequestMapping(value = "/rest/serviceimagecfgs",
            method = RequestMethod.POST,
            produces = "application/json")
    @Timed
    public void create(@RequestBody ServiceImageCfg serviceimagecfg) {
        log.debug("REST request to save ServiceImageCfg : {}", serviceimagecfg);
        serviceimagecfgRepository.save(serviceimagecfg);
    }

    /**
     * GET  /rest/serviceimagecfgs -> get all the serviceimagecfgs.
     */
    @RequestMapping(value = "/rest/serviceimagecfgs",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    public List<ServiceImageCfg> getAll() {
        log.debug("REST request to get all ServiceImageCfgs");
        return serviceimagecfgRepository.findAll();
    }

    /**
     * GET  /rest/serviceimagecfgs/:id -> get the "id" serviceimagecfg.
     */
    @RequestMapping(value = "/rest/serviceimagecfgs/{id}",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    public ServiceImageCfg get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get ServiceImageCfg : {}", id);
        ServiceImageCfg serviceimagecfg = serviceimagecfgRepository.findOne(id);
        if (serviceimagecfg == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return serviceimagecfg;
    }

    /**
     * DELETE  /rest/serviceimagecfgs/:id -> delete the "id" serviceimagecfg.
     */
    @RequestMapping(value = "/rest/serviceimagecfgs/{id}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @Timed
    public void delete(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to delete ServiceImageCfg : {}", id);
        serviceimagecfgRepository.delete(id);
    }
}
