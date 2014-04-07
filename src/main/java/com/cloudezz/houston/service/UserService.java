package com.cloudezz.houston.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudezz.houston.domain.Authority;
import com.cloudezz.houston.domain.PersistentToken;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.PersistentTokenRepository;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.security.AuthoritiesConstants;
import com.cloudezz.houston.security.SecurityUtils;
import com.cloudezz.houston.util.RepositoryUtils;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    /**
     * Registers a new user
     * 
     * @param email
     * @return
     */
    @Transactional
    public User registerUser(String email) {
        User currentUser = new User();
        currentUser.setLogin(email);
        currentUser.setEmail(email);
        currentUser.setAccountId(RepositoryUtils.generateSmallId());
        currentUser.setActive(false);
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setAuthorityName(AuthoritiesConstants.USER);
        authorities.add(authority);
        
        currentUser.setAuthorities(authorities);
        userRepository.saveAndFlush(currentUser);
        log.debug("Created new  User: {}", currentUser);
        return currentUser;
    }

    
    public void updateUserInformation(String firstName, String lastName, String email) {
        User currentUser = userRepository.findOne(SecurityUtils.getCurrentLogin());
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
        userRepository.save(currentUser);
        log.debug("Changed Information for User: {}", currentUser);
    }

    public void changePassword(String password) {
        User currentUser = userRepository.findOne(SecurityUtils.getCurrentLogin());
        String encryptedPassword = passwordEncoder.encode(password);
        currentUser.setPassword(encryptedPassword);
        userRepository.save(currentUser);
        log.debug("Changed password for User: {}", currentUser);
    }

    
    public void changePassword(String email, String password) {
      User currentUser = userRepository.findOne(email);
      String encryptedPassword = passwordEncoder.encode(password);
      currentUser.setPassword(encryptedPassword);
      userRepository.save(currentUser);
      log.debug("Changed password for User: {}", currentUser);
  }
    
    
    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User currentUser = userRepository.findOne(SecurityUtils.getCurrentLogin());
        currentUser.getAuthorities().size(); // eagerly load the association
        return currentUser;
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = new LocalDate();
        List<PersistentToken> tokens = persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1));
        for (PersistentToken token : tokens) {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        }
    }
}
