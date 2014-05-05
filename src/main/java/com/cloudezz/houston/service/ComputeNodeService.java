package com.cloudezz.houston.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.springframework.stereotype.Service;

import com.cloudezz.houston.domain.cloud.CloudConstants;
import com.cloudezz.houston.domain.cloud.CloudProvider;
import com.cloudezz.houston.domain.cloud.ComputeNode;
import com.cloudezz.houston.domain.cloud.MachineConfig;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Service
public class ComputeNodeService {

  public Set<ComputeNode> add(CloudProvider provider, int nodeCount, MachineConfig machineConfig)
      throws Exception {
    ComputeService compute = initComputeService(provider);
    TemplateBuilder templateBuilder = compute.templateBuilder();

  
    templateBuilder.os64Bit(true).osFamily(OsFamily.UBUNTU).minCores(machineConfig.getCpu())
        .minRam(machineConfig.getRam()).hardwareId(getHardwareConfig(machineConfig, provider));
    Set<? extends NodeMetadata> nodes =
        compute.createNodesInGroup(CloudConstants.GROUP_NAME, 1, templateBuilder.build());
    NodeMetadata node = nodes.iterator().next();
    System.out.printf("<< node %s: %s%n", node.getId(),
        (node.getPrivateAddresses() + " --- " + node.getPublicAddresses()));

    return convert(nodes, provider);

  }

  public void delete(Set<ComputeNode> nodes) {

  }

  private String getHardwareConfig(MachineConfig machineConfig ,CloudProvider provider){
    String hardware="";
    if(MachineConfig.MICRO.equals(machineConfig))
      hardware = InstanceType.T1_MICRO;
    
    return hardware;
  }
  
  private Set<ComputeNode> convert(Set<? extends NodeMetadata> nodes, CloudProvider provider) {
    Set<ComputeNode> computeNodes = new LinkedHashSet<>();

    for (NodeMetadata nodeMetadata : nodes) {
      computeNodes.add(convert(nodeMetadata, provider));
    }

    return computeNodes;
  }

  private ComputeNode convert(NodeMetadata node, CloudProvider provider) {
    ComputeNode computeNode = new ComputeNode();
    computeNode.setCloudProvider(provider);
    computeNode.setId(node.getId());
    computeNode.setIpAddress(node.getPublicAddresses().iterator().next());
    computeNode.setName(node.getName());
    computeNode.setSudo(true);
    computeNode.setUsername(node.getCredentials().getUser());
    computeNode.setPassword(node.getCredentials().getPassword());
    computeNode.setHostName(node.getHostname());
    computeNode.setKey(node.getCredentials().getPrivateKey());
    computeNode.setSshPort(node.getLoginPort());
    return computeNode;
  }

  private ComputeService initComputeService(CloudProvider provider) {
    ContextBuilder builder =
        ContextBuilder.newBuilder(provider.getName())
            .credentials(provider.getPrincipal(), provider.getSecret())
            .modules(ImmutableSet.<Module>of(new SshjSshClientModule()));

    System.out.printf(">> initializing %s%n", builder.getApiMetadata());

    return builder.buildView(ComputeServiceContext.class).getComputeService();
  }


 

}
