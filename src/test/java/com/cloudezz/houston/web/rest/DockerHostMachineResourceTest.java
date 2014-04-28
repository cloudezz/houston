package com.cloudezz.houston.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cloudezz.houston.MainApp;
import com.cloudezz.houston.domain.DockerHostMachine;
import com.cloudezz.houston.repository.DockerHostMachineRepository;


/**
 * Test class for the DockerHostMachineResource REST controller.
 *
 * @see DockerHostMachineResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MainApp.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@ActiveProfiles("dev")
public class DockerHostMachineResourceTest {
	
    private static final Long DEFAULT_ID = new Long(1L);

    private static final LocalDate DEFAULT_SAMPLE_DATE_ATTR = new LocalDate(0L);

    private static final LocalDate UPD_SAMPLE_DATE_ATTR = new LocalDate();

    private static final String DEFAULT_SAMPLE_TEXT_ATTR = "sampleTextAttribute";

    private static final String UPD_SAMPLE_TEXT_ATTR = "sampleTextAttributeUpt";

    @Inject
    private DockerHostMachineRepository dockerhostmachineRepository;

    private MockMvc restDockerHostMachineMockMvc;
    
    private DockerHostMachine dockerhostmachine;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DockerHostMachineResource dockerhostmachineResource = new DockerHostMachineResource();
        ReflectionTestUtils.setField(dockerhostmachineResource, "dockerhostmachineRepository", dockerhostmachineRepository);

        this.restDockerHostMachineMockMvc = MockMvcBuilders.standaloneSetup(dockerhostmachineResource).build();

        dockerhostmachine = new DockerHostMachine();
//        dockerhostmachine.setId(DEFAULT_ID);
//    	dockerhostmachine.setSampleDateAttribute(DEFAULT_SAMPLE_DATE_ATTR);
//    	dockerhostmachine.setSampleTextAttribute(DEFAULT_SAMPLE_TEXT_ATTR);
    }

    @Test
    public void testCRUDDockerHostMachine() throws Exception {

    	// Create DockerHostMachine
//    	restDockerHostMachineMockMvc.perform(post("/app/rest/dockerhostmachines")
//    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(dockerhostmachine)))
//                .andExpect(status().isOk());

    	// Read DockerHostMachine
    	restDockerHostMachineMockMvc.perform(get("/app/rest/dockerhostmachines/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
    			.andExpect(jsonPath("$.sampleDateAttribute").value(DEFAULT_SAMPLE_DATE_ATTR.toString()))
    			.andExpect(jsonPath("$.sampleTextAttribute").value(DEFAULT_SAMPLE_TEXT_ATTR));

    	// Update DockerHostMachine
//    	dockerhostmachine.setSampleDateAttribute(UPD_SAMPLE_DATE_ATTR);
//    	dockerhostmachine.setSampleTextAttribute(UPD_SAMPLE_TEXT_ATTR);
  
//    	restDockerHostMachineMockMvc.perform(post("/app/rest/dockerhostmachines")
//    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(dockerhostmachine)))
//                .andExpect(status().isOk());

    	// Read updated DockerHostMachine
    	restDockerHostMachineMockMvc.perform(get("/app/rest/dockerhostmachines/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
    			.andExpect(jsonPath("$.sampleDateAttribute").value(UPD_SAMPLE_DATE_ATTR.toString()))
    			.andExpect(jsonPath("$.sampleTextAttribute").value(UPD_SAMPLE_TEXT_ATTR));

    	// Delete DockerHostMachine
//    	restDockerHostMachineMockMvc.perform(delete("/app/rest/dockerhostmachines/{id}", DEFAULT_ID)
//                .accept(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk());

    	// Read nonexisting DockerHostMachine
//    	restDockerHostMachineMockMvc.perform(get("/app/rest/dockerhostmachines/{id}", DEFAULT_ID)
//                .accept(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(status().isNotFound());

    }
}
