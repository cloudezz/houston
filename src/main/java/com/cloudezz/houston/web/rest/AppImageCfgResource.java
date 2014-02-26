package com.cloudezz.houston.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.repository.AppImageCfgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    /**
     * POST  /rest/appimagecfgs -> Create a new appimagecfg.
     */
    @RequestMapping(value = "/rest/appimagecfgs",
            method = RequestMethod.POST,
            produces = "application/json")
    @Timed
    public void create(@RequestBody AppImageCfg appimagecfg) {
        log.debug("REST request to save AppImageCfg : {}", appimagecfg);
        appimagecfgRepository.save(appimagecfg);
    }

    /**
     * GET  /rest/appimagecfgs -> get all the appimagecfgs.
     */
    @RequestMapping(value = "/rest/appimagecfgs",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    public List<AppImageCfg> getAll() {
        log.debug("REST request to get all AppImageCfgs");
        return appimagecfgRepository.findAll();
    }

    /**
     * GET  /rest/appimagecfgs/:id -> get the "id" appimagecfg.
     */
    @RequestMapping(value = "/rest/appimagecfgs/{id}",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    public AppImageCfg get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get AppImageCfg : {}", id);
        AppImageCfg appimagecfg = appimagecfgRepository.findOne(id);
        if (appimagecfg == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return appimagecfg;
    }

    /**
     * DELETE  /rest/appimagecfgs/:id -> delete the "id" appimagecfg.
     */
    @RequestMapping(value = "/rest/appimagecfgs/{id}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @Timed
    public void delete(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to delete AppImageCfg : {}", id);
        appimagecfgRepository.delete(id);
    }
}
