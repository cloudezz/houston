package com.cloudezz.houston.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cloudezz.houston.domain.DeploymentScript;
import com.cloudezz.houston.domain.User;

/**
 * Spring Data JPA repository for the DeploymentScript entity.
 */
public interface DeploymentScriptRepository extends JpaRepository<DeploymentScript, String> {

  
  /**
   * Finds a all apps for the given user as owner
   * @param user
   * @return  A list of {@link DeploymentScript}
   */
  @Query("SELECT deploymentScripts FROM DeploymentScript deploymentScripts WHERE deploymentScripts.owner = :user")
  public List<DeploymentScript> getAllForUser(@Param("user") User user);
}
