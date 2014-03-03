package com.cloudezz.houston.service;

import javax.el.MethodNotFoundException;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.repository.DockerHostMachineRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class DockerHostMachineService {

  private final Logger log = LoggerFactory.getLogger(DockerHostMachineService.class);


  @Inject
  private DockerHostMachineRepository dockerHostMachineRepository;

  /**
   * Returns the best available docker host machine based on available resource
   * 
   * @return
   */
  public DockerHostMachine getAvailableHostMachine() {
    log.error("Docker host machine based on available resource logic not yet implemented");
    throw new MethodNotFoundException(
        "Get docker host machine based on available resource logic not yet implemented");
  }


}
