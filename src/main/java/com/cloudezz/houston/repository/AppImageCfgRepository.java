package com.cloudezz.houston.repository;

import com.cloudezz.houston.domain.AppImageCfg;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the AppImageCfg entity.
 */
public interface AppImageCfgRepository extends JpaRepository<AppImageCfg, Long> {

}
