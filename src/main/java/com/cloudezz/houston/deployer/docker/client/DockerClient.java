package com.cloudezz.houston.deployer.docker.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.cloudezz.houston.deployer.docker.client.utils.CompressArchiveUtil;
import com.cloudezz.houston.deployer.docker.model.ChangeLog;
import com.cloudezz.houston.deployer.docker.model.CommitConfig;
import com.cloudezz.houston.deployer.docker.model.Container;
import com.cloudezz.houston.deployer.docker.model.ContainerConfig;
import com.cloudezz.houston.deployer.docker.model.ContainerCreateResponse;
import com.cloudezz.houston.deployer.docker.model.ContainerInspectResponse;
import com.cloudezz.houston.deployer.docker.model.ContainerWaitResponse;
import com.cloudezz.houston.deployer.docker.model.HostConfig;
import com.cloudezz.houston.deployer.docker.model.Image;
import com.cloudezz.houston.deployer.docker.model.ImageInspectResponse;
import com.cloudezz.houston.deployer.docker.model.Info;
import com.cloudezz.houston.deployer.docker.model.SearchItem;
import com.cloudezz.houston.deployer.docker.model.Version;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

public class DockerClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(DockerClient.class);

  private RestTemplate restTemplate;

  private String dockerDeamonUrl;

  // info and version return ContentType text/plain which is ignored by the
  // MJHMC by default.
  private RestTemplate textRestTemplate;

  public DockerClient() {
    this("http://localhost:4243");
  }

  public DockerClient(String serverUrl) {
    dockerDeamonUrl = serverUrl;
    restTemplate = new RestTemplate();
    List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    List<MediaType> supportedMediaTypes =
        new ArrayList<MediaType>(converter.getSupportedMediaTypes());
    supportedMediaTypes.add(new MediaType("text", "plain"));
    supportedMediaTypes.add(new MediaType("application", "json"));
    messageConverters.add(converter);
    converter.setSupportedMediaTypes(supportedMediaTypes);
    converter.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public void setDockerDeamonUrl(String dockerDeamonUrl) {
    LOGGER.info("Changing docker daemon URL to '{}'", dockerDeamonUrl);
    this.dockerDeamonUrl = dockerDeamonUrl;
  }


  public boolean isDockerHostAvailable() {
    ResponseEntity<Version> response =
        restTemplate.getForEntity(dockerDeamonUrl + "/version", Version.class);
    HttpStatus status = response.getStatusCode();
    if (status.value() == DockerConstant.STATUS_OK) {
      return true;
    } else {
      return false;
    }
  }

  /**
   ** MISC API
   **/

  public Info info() throws DockerClientException {
    return textRestTemplate.getForObject(dockerDeamonUrl + "/info", Info.class);
  }

  public Version version() throws DockerClientException {
    return textRestTemplate.getForObject(dockerDeamonUrl + "/version", Version.class);
  }

  /**
   ** IMAGES API
   **/

  public void pull(String repository) throws DockerClientException {
    this.pull(repository, null, null);
  }

  public void pull(String repository, String tag) throws DockerClientException {
    this.pull(repository, tag, null);
  }

  public void pull(String repository, String tag, String registry) throws DockerClientException {
    Preconditions.checkNotNull(repository, "Repository was not specified");

    if (StringUtils.countMatches(repository, ":") == 1) {
      String repositoryTag[] = StringUtils.split(repository);
      repository = repositoryTag[0];
      tag = repositoryTag[1];

    }

    Map<String, String> params = new HashMap<String, String>();
    params.put("tag", tag);
    params.put("fromImage", repository);
    params.put("registry", registry);

    restTemplate.exchange(dockerDeamonUrl
        + "/images/create?tag={tag}&fromImage={fromImage}&registry={registry}", HttpMethod.POST,
        null, String.class, params);
  }

  public List<SearchItem> search(String search) throws DockerClientException {
    SearchItem[] response =
        restTemplate.getForObject(dockerDeamonUrl + "/images/search?term={search}",
            SearchItem[].class, search);
    return Arrays.asList(response);
  }

  public void removeImage(String imageId) throws DockerClientException {
    Preconditions.checkState(!StringUtils.isEmpty(imageId), "Image ID can't be empty");

    try {
      restTemplate.delete(dockerDeamonUrl + "/images/{imageId}", imageId);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        LOGGER.warn("Ignoring deletion of non existing image {}", imageId);
        return;
      }
      throw e;
    }
  }

  public void removeImages(List<String> images) throws DockerClientException {
    Preconditions.checkNotNull(images, "List of images can't be null");

    for (String imageId : images) {
      removeImage(imageId);
    }
  }

  public String getVizImages() throws DockerClientException {
    return restTemplate.getForObject(dockerDeamonUrl + "/images/viz", String.class);
  }

  public List<Image> getImages() throws DockerClientException {
    return this.getImages(null, false);
  }

  public List<Image> getImages(boolean allContainers) throws DockerClientException {
    return this.getImages(null, allContainers);
  }


  public List<Image> getImages(String name) throws DockerClientException {
    return this.getImages(name, false);
  }

  public List<Image> getImages(String name, boolean allImages) throws DockerClientException {
    Map<String, String> params = new HashMap<String, String>();
    params.put("filter", name);
    params.put("all", allImages ? "1" : "0");

    Image[] response =
        restTemplate.getForObject(dockerDeamonUrl + "/images/json?filter={filter}&all={all}",
            Image[].class, params);
    return Arrays.asList(response);
  }

  public List<String> getAllImages() throws DockerClientException {

    Image[] response =
        restTemplate.getForObject(dockerDeamonUrl + "/images/json?all=0", Image[].class);

    List<String> imagetags = new ArrayList<String>();
    for (int i = 0; i < response.length; i++) {
      String[] repoTags = response[i].repoTags;
      String[] tagName = repoTags[0].split(":");
      imagetags.add(tagName[0]);
    }
    return imagetags;
  }

  public ImageInspectResponse inspectImage(String imageId) throws DockerClientException {
    return restTemplate.getForObject(dockerDeamonUrl + "/images/{imageId}/json",
        ImageInspectResponse.class, imageId);
  }

  /**
   ** CONTAINERS API
   **/

  public List<Container> listContainers(boolean listAll) {
    Container[] response =
        restTemplate.getForObject(dockerDeamonUrl + "/containers/json?all={all}",
            Container[].class, listAll);
    return Arrays.asList(response);
  }

  /**
   ** CONTAINERS API
   **/

  public int getContainersSize() {
    Container[] response =
        restTemplate.getForObject(dockerDeamonUrl + "/containers/json?all={all}",
            Container[].class, true);

    if (response == null)
      return 0;

    return response.length;

  }


  public ContainerInspectResponse createAndGetContainer(ContainerConfig containerConfig)
      throws DockerClientException {
    ContainerCreateResponse containerCreateResponse = createContainer(containerConfig, null);
    if (containerCreateResponse.getId() != null) {
      ContainerInspectResponse containerInspectResponse =
          inspectContainer(containerCreateResponse.getId());
      return containerInspectResponse;
    }
    return null;
  }

  public ContainerCreateResponse createContainer(ContainerConfig containerConfig)
      throws DockerClientException {
    return createContainer(containerConfig, null);
  }

  public ContainerCreateResponse createContainer(ContainerConfig containerConfig,
      String containerName) throws DockerClientException {
    final HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    final HttpEntity<ContainerConfig> requestEntity =
        new HttpEntity<ContainerConfig>(containerConfig, requestHeaders);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      new ObjectMapper().writeValue(outputStream, containerConfig);
      LOGGER.debug("Creating a container with the following configuration: {}.", new String(
          outputStream.toByteArray()));
    } catch (JsonGenerationException ex) {
      ex.printStackTrace();
    } catch (JsonMappingException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    String containerParameter = "";
    if (containerName != null) {
      containerParameter = "?name=" + containerName;
    }
    String response =
        restTemplate.postForObject(dockerDeamonUrl + "/containers/create" + containerParameter,
            requestEntity, String.class);
    try {
      return new ObjectMapper().readValue(response, ContainerCreateResponse.class);
    } catch (JsonParseException e) {
      throw new IllegalStateException(e);
    } catch (JsonMappingException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public boolean startContainer(String containerId) throws DockerClientException {
    return this.startContainer(containerId, null);
  }

  public boolean startContainer(String containerId, HostConfig hostConfig)
      throws DockerClientException {
    // restTemplate.postForLocation(dockerDeamonUrl +
    // "/containers/{containerId}/start", hostConfig, containerId);

    // to be tested
    ResponseEntity<String> response =
        restTemplate.postForEntity(dockerDeamonUrl + "/containers/{containerId}/start", hostConfig,
            String.class, containerId);
    HttpStatus status = response.getStatusCode();
    if (status.value() == DockerConstant.STATUS_NO_ERROR) {
      return true;
    } else {
      return false;
    }

  }


  public ContainerInspectResponse inspectContainer(String containerId) throws DockerClientException {
    return restTemplate.getForObject(dockerDeamonUrl + "/containers/{containerId}/json",
        ContainerInspectResponse.class, containerId);
  }

  public boolean removeContainer(String container) throws DockerClientException {
    return this.removeContainer(container, false);
  }

  public boolean removeContainer(String containerId, boolean removeVolumes)
      throws DockerClientException {
    Preconditions.checkState(!StringUtils.isEmpty(containerId), "Container ID can't be empty");

    try {
      restTemplate.delete(dockerDeamonUrl + "/containers/{containerId}?v={removeVolumes}",
          containerId, removeVolumes ? "1" : "0");
    } catch (HttpClientErrorException e) {
      throw new DockerClientException(e);
    }

    return true;
  }

  public void removeContainers(List<String> containers, boolean removeVolumes)
      throws DockerClientException {
    Preconditions.checkNotNull(containers, "List of containers can't be null");

    for (String containerId : containers) {
      removeContainer(containerId, removeVolumes);
    }
  }

  public ContainerWaitResponse waitContainer(String containerId) throws DockerClientException {
    return restTemplate.postForObject(dockerDeamonUrl + "/containers/{containerId}/wait", null,
        ContainerWaitResponse.class, containerId);
  }

  public InputStream logContainer(String containerId,
      final StreamResponseListener streamResponseListener) throws DockerClientException {
    return logContainer(containerId, false, streamResponseListener);
  }

  public InputStream logContainerStream(String containerId,
      final StreamResponseListener streamResponseListener) throws DockerClientException {
    return logContainer(containerId, true, streamResponseListener);
  }

  private InputStream logContainer(String containerId, boolean stream,
      final StreamResponseListener streamResponseListener) throws DockerClientException {

    ResponseExtractor<InputStream> responseExtractor = new ResponseExtractor<InputStream>() {
      @Override
      public InputStream extractData(ClientHttpResponse response) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()));
        String line;
        while ((line = reader.readLine()) != null) {
          streamResponseListener.recieve(line);
        }
        String result = IOUtils.toString(response.getBody());
        return new ByteArrayInputStream(result.getBytes());
      }
    };

    Map<String, String> params = new HashMap<String, String>();
    params.put("containerId", containerId);
    params.put("logs", "1");
    params.put("stdout", "1");
    params.put("stderr", "1");
    params.put("stream", stream ? "1" : "0"); // this parameter keeps stream
    // open indefinitely

    return restTemplate
        .execute(
            dockerDeamonUrl
                + "/containers/{containerId}/attach?logs={logs}&stdout={stdout}&stderr={stderr}&stream={stream}",
            HttpMethod.POST, null, responseExtractor, params);
  }

  public InputStream getEvents(Long sinceTime) throws DockerClientException {

    ResponseExtractor<InputStream> responseExtractor = new ResponseExtractor<InputStream>() {
      @Override
      public InputStream extractData(ClientHttpResponse response) throws IOException {
        String result = IOUtils.toString(response.getBody());
        return new ByteArrayInputStream(result.getBytes());
      }
    };

    Map<String, String> params = new HashMap<String, String>();
    if (sinceTime != null) {
      params.put("sinceTime", "" + sinceTime);
    }

    // open indefinitely
    return restTemplate.execute(dockerDeamonUrl + "/events", HttpMethod.GET, null,
        responseExtractor);
  }

  public List<ChangeLog> containterDiff(String containerId) throws DockerClientException {
    ChangeLog[] response =
        restTemplate.getForObject(dockerDeamonUrl + "/containers/{containerId}/changes",
            ChangeLog[].class, containerId);
    return Arrays.asList(response);
  }

  public boolean stopContainer(String containerId) throws DockerClientException {
    return this.stopContainer(containerId, 10);
  }

  public boolean stopContainer(String containerId, int timeout) throws DockerClientException {
    ResponseEntity<?> response =
        restTemplate.postForEntity(dockerDeamonUrl + "/containers/{containerId}/stop?t={timeout}",
            null, null, containerId, timeout);
    HttpStatus status = response.getStatusCode();
    if (status.value() == DockerConstant.STATUS_NO_ERROR) {
      return true;
    } else {
      return false;
    }

  }

  public void kill(String containerId) throws DockerClientException {
    restTemplate.postForLocation(dockerDeamonUrl + "/containers/{containerId}/kill", null,
        containerId);
  }

  public void restart(String containerId, int timeout) throws DockerClientException {
    restTemplate.postForLocation(dockerDeamonUrl + "/containers/{containerId}/restart", null,
        containerId);
  }

  private static class CommitResponse {

    @JsonProperty("Id")
    public String id;

    @Override
    public String toString() {
      return "CommitResponse{" + "id=" + id + '}';
    }

  }

  public String commit(CommitConfig commitConfig) throws DockerClientException {
    Preconditions.checkNotNull(commitConfig.container, "Container ID was not specified");

    Map<String, String> params = new HashMap<String, String>();
    params.put("container", commitConfig.container);
    params.put("repo", commitConfig.repo);
    params.put("tag", commitConfig.tag);
    params.put("m", commitConfig.message);
    params.put("author", commitConfig.author);
    params.put("run", commitConfig.run);

    String response =
        restTemplate
            .postForObject(
                dockerDeamonUrl
                    + "/commit?container={container}&repo={repo}&tag={tag}&m={m}&author={author}&run={run}",
                null, String.class, params);

    try {
      return new ObjectMapper().readValue(response, CommitResponse.class).id;
    } catch (JsonParseException e) {
      throw new IllegalStateException(e);
    } catch (JsonMappingException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public InputStream build(File dockerFolder) throws DockerClientException {
    return this.build(dockerFolder, null);
  }

  public InputStream build(File dockerFolder, String tag) throws DockerClientException {
    Preconditions.checkNotNull(dockerFolder, "Folder is null");
    Preconditions.checkArgument(dockerFolder.exists(), "Folder %s doesn't exist", dockerFolder);
    Preconditions.checkState(new File(dockerFolder, "Dockerfile").exists(),
        "Dockerfile doesn't exist in " + dockerFolder);

    // ARCHIVE TAR
    String archiveNameWithOutExtension = UUID.randomUUID().toString();

    File dockerFolderTar = null;
    File tmpDockerContextFolder = null;

    try {
      File dockerFile = new File(dockerFolder, "Dockerfile");

      List<String> dockerFileContent = FileUtils.readLines(dockerFile);

      if (dockerFileContent.size() <= 0) {
        throw new DockerClientException(String.format("Dockerfile %s is empty", dockerFile));
      }

      // Create tmp docker context folder
      // tmpDockerContextFolder = new
      // File(FileUtils.getTempDirectoryPath(), "docker-java-build" +
      // archiveNameWithOutExtension);
      tmpDockerContextFolder = new File("/tmp", "docker-java-build" + archiveNameWithOutExtension);

      FileUtils.copyFileToDirectory(dockerFile, tmpDockerContextFolder);

      for (String cmd : dockerFileContent) {
        if (StringUtils.startsWithIgnoreCase(cmd.trim(), "ADD")) {
          String addArgs[] = StringUtils.split(cmd, " \t");
          if (addArgs.length != 3) {
            throw new DockerClientException(String.format("Wrong format on line [%s]", cmd));
          }

          File src = new File(addArgs[1]);
          if (!src.isAbsolute()) {
            src = new File(dockerFolder, addArgs[1]).getCanonicalFile();
          }

          if (!src.exists()) {
            throw new DockerClientException(String.format("Source file %s doesnt' exist", src));
          }
          if (src.isDirectory()) {
            FileUtils.copyDirectory(src, tmpDockerContextFolder);
          } else {
            FileUtils.copyFileToDirectory(src, tmpDockerContextFolder);
          }
        }
      }

      dockerFolderTar =
          CompressArchiveUtil.archiveTARFiles(tmpDockerContextFolder, archiveNameWithOutExtension);

    } catch (IOException ex) {
      FileUtils.deleteQuietly(dockerFolderTar);
      FileUtils.deleteQuietly(tmpDockerContextFolder);
      throw new DockerClientException("Error occurred while preparing Docker context folder.", ex);
    }

    final HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set("Content-Type", "application/tar");
    HttpEntity<byte[]> requestEntity;
    try {
      FileInputStream openInputStream = FileUtils.openInputStream(dockerFolderTar);
      byte[] byteArray = IOUtils.toByteArray(openInputStream);
      requestEntity = new HttpEntity<byte[]>(byteArray, requestHeaders);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    final ResponseEntity<String> response =
        restTemplate.exchange(dockerDeamonUrl + "/build?t={tag}", HttpMethod.POST, requestEntity,
            String.class, tag);

    return new ByteArrayInputStream(response.getBody().getBytes());
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }

}
