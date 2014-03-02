package com.cloudezz.houston.service;

import javax.annotation.PostConstruct;
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

//  @PostConstruct
  private void initDBWithLocahost() {
    if (dockerHostMachineRepository.count() == 0L) {
      DockerHostMachine dockerHostMachine = new DockerHostMachine();
      dockerHostMachine.setCloudProviderName("local");
      dockerHostMachine.setDockerPort("4243");
      dockerHostMachine.setHttps(false);
      dockerHostMachine.setId("127.0.0.1");
      dockerHostMachine.setIpAddress("127.0.0.1");
      dockerHostMachine.setName("localhost");
      dockerHostMachine.setPassword("");
      dockerHostMachine.setUsername("");

      dockerHostMachineRepository.save(dockerHostMachine);
    }
  }

}
