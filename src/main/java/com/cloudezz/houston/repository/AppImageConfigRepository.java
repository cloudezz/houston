package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudezz.houston.domain.ApplicationImageConfig;

/**
 * Spring Data JPA repository for the ApplicationImageConfig entity.
 */
public interface AppImageConfigRepository extends JpaRepository<ApplicationImageConfig, String> {

}
