package com.cloudezz.houston.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.repository.AppImageCfgRepository;
import com.cloudezz.houston.service.UserService;

/**
 * Test class for the AccountResource REST controller.
 * 
 * @see UserService
 */
public class AppImageCfgResourceTest extends BaseApplicationContextLoader {

  @Inject
  private AppImageCfgRepository appimagecfgRepository;


  private MockMvc restUserMockMvc;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    AppImageCfgResource accountResource = new AppImageCfgResource();
    ReflectionTestUtils.setField(accountResource, "appimagecfgRepository", appimagecfgRepository);
    this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
  }

  @Test
  public void testNonAuthenticatedUser() throws Exception {
//    restUserMockMvc.perform(get("/app/rest/authenticate").accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk()).andExpect(content().string(""));
    restUserMockMvc.perform(get("/app/rest/appimagecfgs").accept(MediaType.APPLICATION_JSON))
    .andExpect(status().isOk());
    
//    restUserMockMvc.perform(post("/app/rest/appimagecfgs").accept(MediaType.APPLICATION_JSON))
//    .andExpect(status().isOk()).andExpect(content().string(""));


  }

  @Test
  public void testAuthenticatedUser() throws Exception {
    restUserMockMvc.perform(get("/app/rest/authenticate").with(new RequestPostProcessor() {
      public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        request.setRemoteUser("test");
        return request;
      }
    }).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(content().string("test"));
  }


}
