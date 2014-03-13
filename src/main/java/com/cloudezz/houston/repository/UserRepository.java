package com.cloudezz.houston.repository;

import com.cloudezz.houston.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, String> {

  /**
   * Returns the user by account id
   * @param accountId
   * @return
   */
  @Query
  User findByAccountId(String accountId);
}
