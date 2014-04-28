package com.cloudezz.houston.web.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.repository.DockerHostMachineRepository;
import com.cloudezz.houston.security.AuthoritiesConstants;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing DockerHostMachine.
 */
@RestController
@RequestMapping("/app")
public class DockerHostMachineResource {

    private final Logger log = LoggerFactory.getLogger(DockerHostMachineResource.class);

    @Inject
    private DockerHostMachineRepository dockerhostmachineRepository;

    /**
     * POST  /rest/dockerhostmachines -> Create a new dockerhostmachine.
     */
    @RequestMapping(value = "/rest/dockerhostmachines",
            method = RequestMethod.POST,
            produces = "application/json")
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public void create(@RequestBody DockerHostMachine dockerhostmachine) {
        log.debug("REST request to save DockerHostMachine : {}", dockerhostmachine);
        dockerhostmachineRepository.save(dockerhostmachine);
    }

    /**
     * GET  /rest/dockerhostmachines -> get all the dockerhostmachines.
     */
    @RequestMapping(value = "/rest/dockerhostmachines",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<DockerHostMachine> getAll() {
        log.debug("REST request to get all DockerHostMachines");
        return dockerhostmachineRepository.findAll();
    }

    /**
     * GET  /rest/dockerhostmachines/:id -> get the "id" dockerhostmachine.
     */
    @RequestMapping(value = "/rest/dockerhostmachines/{id}",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public DockerHostMachine get(@PathVariable String id, HttpServletResponse response) {
        log.debug("REST request to get DockerHostMachine : {}", id);
        DockerHostMachine dockerhostmachine = dockerhostmachineRepository.findOne(id);
        if (dockerhostmachine == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return dockerhostmachine;
    }

    /**
     * DELETE  /rest/dockerhostmachines/:id -> delete the "id" dockerhostmachine.
     */
    @RequestMapping(value = "/rest/dockerhostmachines/{id}",
            method = RequestMethod.DELETE,
            produces = "application/json")
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable String id, HttpServletResponse response) {
        log.debug("REST request to delete DockerHostMachine : {}", id);
        dockerhostmachineRepository.delete(id);
    }
}
