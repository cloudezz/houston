package com.cloudezz.houston.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.avis.AvisClient;
import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.repository.PersistentTokenRepository;
import com.cloudezz.houston.service.UserService;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
public class SignUpResourceTest extends BaseApplicationContextLoader{

    @Inject
    private UserService userService;

    private MockMvc restUserMockMvc;
    
    
    @Autowired
    private AvisClient avis;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Autowired
    private VelocityEngine velocityEngine;
    

    @Before
    public void setup() {
        SignUpResource signUpResource = new SignUpResource();
        ReflectionTestUtils.setField(signUpResource, "userService", userService);
        ReflectionTestUtils.setField(signUpResource, "avis", avis);
        ReflectionTestUtils.setField(signUpResource, "persistentTokenRepository", persistentTokenRepository);
        ReflectionTestUtils.setField(signUpResource, "userService", userService);
        ReflectionTestUtils.setField(signUpResource, "velocityEngine", velocityEngine);
        ReflectionTestUtils.setField(signUpResource, "queueName", "cloudezz.email.queue");
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(signUpResource).build();
    }

    @Test
    public void testSignUp() throws Exception {
        restUserMockMvc.perform(get("/app/rest/signup")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
