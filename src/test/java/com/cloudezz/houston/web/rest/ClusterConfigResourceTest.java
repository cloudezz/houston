package com.cloudezz.houston.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.cloudezz.houston.Application;
import com.cloudezz.houston.domain.ClusterConfig;
import com.cloudezz.houston.repository.ClusterConfigRepository;


/**
 * Test class for the ClusterConfigResource REST controller.
 *
 * @see ClusterConfigResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@ActiveProfiles("dev")
public class ClusterConfigResourceTest {
	
    private static final String DEFAULT_ID = new String("id1");

    private static final LocalDate DEFAULT_SAMPLE_DATE_ATTR = new LocalDate(0L);

    private static final LocalDate UPD_SAMPLE_DATE_ATTR = new LocalDate();

    private static final String DEFAULT_SAMPLE_TEXT_ATTR = "sampleTextAttribute";

    private static final String UPD_SAMPLE_TEXT_ATTR = "sampleTextAttributeUpt";

    @Inject
    private ClusterConfigRepository clusterconfigRepository;

    private MockMvc restClusterConfigMockMvc;
    
    private ClusterConfig clusterconfig;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClusterConfigResource clusterconfigResource = new ClusterConfigResource();
        ReflectionTestUtils.setField(clusterconfigResource, "clusterconfigRepository", clusterconfigRepository);

        this.restClusterConfigMockMvc = MockMvcBuilders.standaloneSetup(clusterconfigResource).build();

        clusterconfig = new ClusterConfig();
        clusterconfig.setId(DEFAULT_ID);
    }

    @Test
    public void testCRUDClusterConfig() throws Exception {

    	// Create ClusterConfig
    	restClusterConfigMockMvc.perform(post("/app/rest/clusterconfigs")
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clusterconfig)))
                .andExpect(status().isOk());

    	// Read ClusterConfig
    	restClusterConfigMockMvc.perform(get("/app/rest/clusterconfigs/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID))
    			.andExpect(jsonPath("$.sampleDateAttribute").value(DEFAULT_SAMPLE_DATE_ATTR.toString()))
    			.andExpect(jsonPath("$.sampleTextAttribute").value(DEFAULT_SAMPLE_TEXT_ATTR));

    	// Update ClusterConfig
  
    	restClusterConfigMockMvc.perform(post("/app/rest/clusterconfigs")
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clusterconfig)))
                .andExpect(status().isOk());

    	// Read updated ClusterConfig
    	restClusterConfigMockMvc.perform(get("/app/rest/clusterconfigs/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID))
    			.andExpect(jsonPath("$.sampleDateAttribute").value(UPD_SAMPLE_DATE_ATTR.toString()))
    			.andExpect(jsonPath("$.sampleTextAttribute").value(UPD_SAMPLE_TEXT_ATTR));

    	// Delete ClusterConfig
    	restClusterConfigMockMvc.perform(delete("/app/rest/clusterconfigs/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

    	// Read nonexisting ClusterConfig
    	restClusterConfigMockMvc.perform(get("/app/rest/clusterconfigs/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
