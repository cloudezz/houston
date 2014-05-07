package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cloudezz.houston.domain.Cloud;

/**
 * Spring Data JPA repository for the Application entity.
 */
public interface CloudRepository extends JpaRepository<Cloud, String> {

}
