package com.cloudezz.houston.web.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.houston.domain.Cloud;
import com.cloudezz.houston.repository.CloudRepository;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class CloudResource {

  private final Logger log = LoggerFactory.getLogger(CloudResource.class);

  @Inject
  private CloudRepository cloudRepository;

  /**
   * GET /rest/cloud -> get the cloud.
   */
  @RequestMapping(value = "/rest/cloud/{id}", method = RequestMethod.GET, produces = "application/json")
  @Timed
  public Cloud getCloud(@PathVariable("id") String id) {
    Cloud cloud = cloudRepository.findOne(id);
    return cloud;
  }

  /**
   * POST /rest/cloud -> post the cloud.
   */
  @RequestMapping(value = "/rest/cloud", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
  @Timed
  public Cloud saveCloud(@RequestBody Cloud cloud) {
    cloud = cloudRepository.save(cloud);
    return cloud;
  }
  
  /**
   * GET /rest/cloud -> Get all cloud.
   */
  @RequestMapping(value = "/rest/cloud", method = RequestMethod.GET, produces = "application/json")
  @Timed
  public List<Cloud> getAll() {
    List<Cloud> clouds = cloudRepository.findAll();
    return clouds;
  }

}
