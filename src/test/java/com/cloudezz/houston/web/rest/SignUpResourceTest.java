package com.cloudezz.houston.web.rest;

import java.security.SecureRandom;

import com.bbytes.avis.AvisClient;
import com.cloudezz.houston.Application;
import com.cloudezz.houston.repository.PersistentTokenRepository;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.service.UserService;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("dev")
//@ContextConfiguration(classes = Application.class, loader=SpringApplicationContextLoader.class)
public class SignUpResourceTest {

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
