package com.cloudezz.houston.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cloudezz.houston.domain.AppImageCfg;

/**
 * Spring Data JPA repository for the AppImageCfg entity.
 */
public interface AppImageCfgRepository extends JpaRepository<AppImageCfg, String> {

  @Query("SELECT appImageCfgs FROM AppImageCfg appImageCfgs WHERE appImageCfgs.id IN (:ids)")
  public List<AppImageCfg> getByIds(@Param("ids") List<String> ids);
}
