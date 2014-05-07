package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cloudezz.houston.domain.Organisation;

/**
 * Spring Data JPA repository for the Organisation entity.
 */
public interface OrganisationRepository extends JpaRepository<Organisation, String> {

  /**
   * Returns the Organisation by orgId
   * 
   * @param orgId
   * @return
   */
  @Query
  Organisation findByOrgId(String orgId);

}
