package com.cloudezz.houston.repository;

import com.cloudezz.houston.domain.ClusterConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the ClusterConfig entity.
 */
public interface ClusterConfigRepository extends JpaRepository<ClusterConfig, Long> {

}
