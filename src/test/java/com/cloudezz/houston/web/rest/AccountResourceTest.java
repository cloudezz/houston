package com.cloudezz.houston.web.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.domain.Authority;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.security.AuthoritiesConstants;
import com.cloudezz.houston.service.UserService;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see UserService
 */
public class AccountResourceTest extends BaseApplicationContextLoader{

    @Inject
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private MockMvc restUserMockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AccountResource accountResource = new AccountResource();
        ReflectionTestUtils.setField(accountResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(accountResource, "userService", userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
    }

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/app/rest/authenticate")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/app/rest/authenticate")
                .with(new RequestPostProcessor() {
                    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                        request.setRemoteUser("test");
                        return request;
                    }
                })
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(authority);

        User user = new User();
        user.setLogin("test");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail("john.doe@jhipter.com");
        user.setAuthorities(authorities);
        when(userService.getUserWithAuthorities()).thenReturn(user);

        restUserMockMvc.perform(get("/app/rest/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.firstName").value("john"))
                .andExpect(jsonPath("$.lastName").value("doe"))
                .andExpect(jsonPath("$.email").value("john.doe@jhipter.com"))
                .andExpect(jsonPath("$.roles.ROLE_ADMIN").value(true));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(null);

        restUserMockMvc.perform(get("/app/rest/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
