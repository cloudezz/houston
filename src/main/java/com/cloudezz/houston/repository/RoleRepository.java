package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cloudezz.houston.domain.Role;

/**
 * Spring Data JPA repository for the Application entity.
 */
public interface RoleRepository extends JpaRepository<Role, String> {

}
