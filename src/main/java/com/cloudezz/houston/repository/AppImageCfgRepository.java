package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudezz.houston.domain.AppImageCfg;

/**
 * Spring Data JPA repository for the AppImageCfg entity.
 */
public interface AppImageCfgRepository extends JpaRepository<AppImageCfg, String> {

}
