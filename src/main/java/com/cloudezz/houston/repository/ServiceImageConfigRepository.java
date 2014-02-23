package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudezz.houston.domain.ServiceImageConfig;

/**
 * Spring Data JPA repository for the ServiceImageConfig entity.
 */
public interface ServiceImageConfigRepository extends JpaRepository<ServiceImageConfig, String> {

}
