package com.cloudezz.houston.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.repository.AppImageCfgRepository;
import com.cloudezz.houston.service.UserService;
import com.cloudezz.houston.web.rest.dto.AppImageCfgDTO;
import com.google.gson.Gson;

/**
 * Test class for the AccountResource REST controller.
 * 
 * @see UserService
 */
public class AppImageCfgConversionTest {
	@Test
	public void testAuthenticatedUser() throws Exception {
		AppImageCfgDTO dto = new AppImageCfgDTO();
		dto.setAppName("name");
		dto.setCpuShares(3);
		dto.setDaemon(true);
		dto.setDomainName("domain");
		Map<String, String> environmentMap = new HashMap<String, String>();
		environmentMap.put("mapVal1", "mapVal1");
		dto.setEnvironmentMapping(environmentMap);
		dto.setGitURL("url");
		dto.setHostName("hostnameaval");
		dto.setImageName("imagenameval");
		dto.setMemory(67868L);
		dto.setMemorySwap(4335345L);
		List<String> ports = new ArrayList<>();
		ports.add("port1");
		ports.add("port2");
		dto.setPorts(ports);
		List<ServiceImageCfg> serviceImages = new ArrayList<ServiceImageCfg>();
		dto.setServiceImages(serviceImages);
		dto.setUser("usern ame");
		
		Gson gso=new Gson();
		String str = gso.toJson(dto);
		System.out.println(str);

	}

}
