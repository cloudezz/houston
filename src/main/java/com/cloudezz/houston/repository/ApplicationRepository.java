package com.cloudezz.houston.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.Application;
import com.cloudezz.houston.domain.User;

/**
 * Spring Data JPA repository for the Application entity.
 */
public interface ApplicationRepository extends JpaRepository<Application, String> {

  
  /**
   * Finds a all apps for the given user as owner
   * @param user
   * @return  A list of {@link AppImageCfg}
   */
  @Query("SELECT apps FROM Application apps WHERE apps.owner = :user")
  public List<Application> getAllForUser(@Param("user") User user);
}
