package com.cloudezz.houston.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cloudezz.houston.domain.ServiceImageCfg;

/**
 * Spring Data JPA repository for the ServiceImageConfig entity.
 */
public interface ServiceImageConfigRepository extends JpaRepository<ServiceImageCfg, String> {
  
  @Query("SELECT serviceImageCfgs FROM ServiceImageCfg serviceImageCfgs WHERE serviceImageCfgs.id IN :ids")
  public List<ServiceImageCfg> getByIds(@Param("ids") List<String> ids);
  
}
