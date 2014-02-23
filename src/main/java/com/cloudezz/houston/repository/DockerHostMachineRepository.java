package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudezz.houston.domain.DockerHostMachine;

/**
 * Spring Data JPA repository for the DockerHostMachine entity.
 */
public interface DockerHostMachineRepository extends JpaRepository<DockerHostMachine, String> {

}
