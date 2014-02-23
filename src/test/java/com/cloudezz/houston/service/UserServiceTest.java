package com.cloudezz.houston.service;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.domain.PersistentToken;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.PersistentTokenRepository;
import com.cloudezz.houston.repository.UserRepository;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
public class UserServiceTest extends BaseApplicationContextLoader{

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Test
    public void testRemoveOldPersistentTokens() {
        assertEquals(0, persistentTokenRepository.findAll().size());
        User admin = userRepository.findOne("admin");
        generateUserToken(admin, "1111-1111", new LocalDate());
        LocalDate now = new LocalDate();
        generateUserToken(admin, "2222-2222", now.minusDays(32));
        assertEquals(2, persistentTokenRepository.findAll().size());
        userService.removeOldPersistentTokens();
        assertEquals(1, persistentTokenRepository.findAll().size());
    }

    private void generateUserToken(User user, String tokenSeries, LocalDate localDate) {
        PersistentToken token = new PersistentToken();
        token.setSeries(tokenSeries);
        token.setUser(user);
        token.setTokenValue(tokenSeries + "-data");
        token.setTokenDate(localDate);
        token.setIpAddress("127.0.0.1");
        token.setUserAgent("Test agent");
        persistentTokenRepository.saveAndFlush(token);
    }
}
