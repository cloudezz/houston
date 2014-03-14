package com.cloudezz.houston.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.User;

/**
 * Spring Data JPA repository for the AppImageCfg entity.
 */
public interface AppImageCfgRepository extends JpaRepository<AppImageCfg, String> {

  
  /**
   * Finds a all app cfg for the given user as owner
   * @param user
   * @return  A list of {@link AppImageCfg}
   */
  @Query("SELECT cgfs FROM AppImageCfg cgfs WHERE cgfs.owner = :user")
  public List<AppImageCfg> getAllForUser(@Param("user") User user);
}
