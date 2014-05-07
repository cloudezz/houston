package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cloudezz.houston.domain.Team;

/**
 * Spring Data JPA repository for the Team entity.
 */
public interface TeamRepository extends JpaRepository<Team, String> {

  /**
   * Returns the Team by teamId
   * 
   * @param teamId
   * @return
   */
  @Query
  Team findByTeamId(String teamId);

}
