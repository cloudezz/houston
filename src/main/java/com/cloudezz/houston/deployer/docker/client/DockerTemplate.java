package com.cloudezz.houston.deployer.docker.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import com.cloudezz.houston.deployer.docker.model.ContainerConfig;
import com.cloudezz.houston.deployer.docker.model.ContainerCreateResponse;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse;
import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.deployer.docker.model.HostPortBinding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DockerTemplate implements DockerOperations {

	private static final Logger LOG = LoggerFactory.getLogger(DockerTemplate.class);

	private DockerClient dockerClient;

	public DockerTemplate(DockerClient dockerClient) {
		this.dockerClient = dockerClient;
	}

	@Override
	public String build(String tag, String dockerFolderName) {
		LOG.info("Building new image '{}' from direcotry '{}'", tag, dockerFolderName);
		File dockerFolder = new File(dockerFolderName);
		try {
			InputStream buildOutput = this.dockerClient.build(dockerFolder, tag);
			try {
				String buildLog = IOUtils.toString(buildOutput);
				LOG.info("New image '{}' successfully build from direcotry '{}'", tag, dockerFolderName);
				LOG.info("Build Log:\n" + buildLog);
				return buildLog;
			} catch (IOException e) {
				LOG.error("Failed to capture the build log.", e);
			}
		} catch (CloudezzDeployException e) {
			throw new RuntimeException("Failed to build new image.", e);
		}
		return "Failed to capture the build log. Please consult the logfiles for more information.";
	}

	@Override
	public String create(String imageTag, int containerPort) {
		return create(imageTag, null, containerPort);
	}

	private String createPortBindingKey(int containerPort) {
		return Integer.toString(containerPort) + "/tcp";
	}

	public String create(String imageTag, String containerName, int containerPort) {
		LOG.info("Creating new container from image '{}'...", imageTag);
		ContainerConfig containerConfig = new ContainerConfig();
		containerConfig.setHostName("localhost");
		containerConfig.setImage(imageTag);
		containerConfig.getExposedPorts().put(createPortBindingKey(containerPort), null);
		try {
			ContainerCreateResponse response = dockerClient.createContainer(containerConfig, containerName);
			LOG.info("Create container finished with: {}", response);
			return response.getId();
		} catch (CloudezzDeployException e) {
			throw new RuntimeException("Failed to run new container.", e);
		}
	}

	@Override
	public void start(String containerId, int hostPort, int containerPort) {
		LOG.info("Starting container '{}' with portmapping {}:{}", containerId, hostPort, containerPort);
		try {
			HostConfig hostConfig = new HostConfig();
			Map<String, HostPortBinding[]> portBindings = hostConfig.getPortBindings();
			HostPortBinding[] portBindingForContainerPort = new HostPortBinding[1];
			portBindingForContainerPort[0] = new HostPortBinding("0.0.0.0", Integer.toString(hostPort));
			portBindings.put(createPortBindingKey(containerPort), portBindingForContainerPort);
			LOG.debug("Using host config: {}", hostConfig);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
				new ObjectMapper().writeValue(outputStream, hostConfig);
				LOG.debug("Using host config (JSON): {}.", new String(outputStream.toByteArray()));
			} catch (JsonGenerationException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			dockerClient.startContainer(containerId, hostConfig);
			LOG.info("Container start successfully triggered.");
		} catch (CloudezzDeployException e) {
			throw new RuntimeException("Failed to start container '" + containerId + "'.", e);
		}
	}

	@Override
	public boolean containerExists(String containerName) {
		try {
			dockerClient.inspectContainer(containerName);
			return true;
		} catch (CloudezzDeployException e) {
			throw new RuntimeException("Failed to query the docker daemon.");
		} catch (HttpClientErrorException e) {
			return false;
		}
	}

	@Override
	public boolean isRunning(String containerName) {
		ContainerInspectResponse response;
		try {
			response = dockerClient.inspectContainer(containerName);
			return response.state.running;
		} catch (CloudezzDeployException e) {
			throw new RuntimeException("Failed to query the docker daemon.");
		} catch (HttpClientErrorException e) {
			throw new IllegalArgumentException("Container '" + containerName + "' does not exist.");
		}
	}

}
