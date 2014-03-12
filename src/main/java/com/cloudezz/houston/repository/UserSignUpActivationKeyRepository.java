package com.cloudezz.houston.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudezz.houston.domain.UserSignUpActivationKey;

/**
 * Spring Data JPA repository for the UserSignUpActivationKey entity.
 */
public interface UserSignUpActivationKeyRepository extends JpaRepository<UserSignUpActivationKey, String> {

}
