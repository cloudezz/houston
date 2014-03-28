package com.cloudezz.houston.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.cloudezz.houston.domain.ClusterConfig;
import com.cloudezz.houston.repository.ClusterConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing ClusterConfig.
 */
@RestController
@RequestMapping("/app")
public class ClusterConfigResource {

    private final Logger log = LoggerFactory.getLogger(ClusterConfigResource.class);

    @Inject
    private ClusterConfigRepository clusterconfigRepository;

    /**
     * POST  /rest/clusterconfigs -> Create a new clusterconfig.
     */
    @RequestMapping(value = "/rest/clusterconfigs",
            method = RequestMethod.POST,
            produces = "application/json")
    @Timed
    public void create(@RequestBody ClusterConfig clusterconfig) {
        log.debug("REST request to save ClusterConfig : {}", clusterconfig);
        clusterconfigRepository.save(clusterconfig);
    }

    /**
     * GET  /rest/clusterconfigs -> get all the clusterconfigs.
     */
    @RequestMapping(value = "/rest/clusterconfigs",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    public List<ClusterConfig> getAll() {
        log.debug("REST request to get all ClusterConfigs");
        return clusterconfigRepository.findAll();
    }

    /**
     * GET  /rest/clusterconfigs/:id -> get the "id" clusterconfig.
     */
    @RequestMapping(value = "/rest/clusterconfigs/{id}",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    public ClusterConfig get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get ClusterConfig : {}", id);
        ClusterConfig clusterconfig = clusterconfigRepository.findOne(id);
        if (clusterconfig == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return clusterconfig;
    }

    /**
     * DELETE  /rest/clusterconfigs/:id -> delete the "id" clusterconfig.
     */
    @RequestMapping(value = "/rest/clusterconfigs/{id}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @Timed
    public void delete(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to delete ClusterConfig : {}", id);
        clusterconfigRepository.delete(id);
    }
}
