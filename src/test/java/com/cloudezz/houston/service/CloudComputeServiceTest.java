package com.cloudezz.houston.service;

import javax.inject.Inject;

import org.junit.Test;

import com.cloudezz.houston.BaseApplicationContextLoader;
import com.cloudezz.houston.domain.cloud.CloudProvider;
import com.cloudezz.houston.domain.cloud.MachineConfig;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
public class CloudComputeServiceTest extends BaseApplicationContextLoader{

    @Inject
    private ComputeNodeService computeNodeService;

    

    @Test
    public void startEC2Machine() throws Exception {
      CloudProvider cloudProvider = new CloudProvider();
      cloudProvider.setName("aws-ec2");
      cloudProvider.setId("aws-ec2");
      cloudProvider.setLocation("us-west-2c");
      cloudProvider.setPrincipal("AKIAI56LAACD2BQC7IOA");
      cloudProvider.setSecret("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");
      
      computeNodeService.add(cloudProvider, 1, MachineConfig.MICRO);
      
    }

   
}
