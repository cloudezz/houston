package com.cloudezz.houston.web.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudezz.firework.domain.AppScript;
import com.cloudezz.firework.domain.Node;
import com.cloudezz.firework.executor.ExecutionListener;
import com.cloudezz.firework.service.ExecutionService;

import com.cloudezz.houston.deployer.DeployerService;
import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.deployer.docker.client.CloudezzException;
import com.cloudezz.houston.domain.AppImageCfg;
import com.cloudezz.houston.domain.Application;
import com.cloudezz.houston.domain.ClusterConfig;
import com.cloudezz.houston.domain.DeploymentScript;
import com.cloudezz.houston.domain.ExposedService;
import com.cloudezz.houston.domain.ServiceImageCfg;
import com.cloudezz.houston.domain.User;
import com.cloudezz.houston.repository.AppImageCfgRepository;
import com.cloudezz.houston.repository.ApplicationRepository;
import com.cloudezz.houston.repository.DeploymentScriptRepository;
import com.cloudezz.houston.repository.DockerHostMachineRepository;
import com.cloudezz.houston.repository.ServiceImageConfigRepository;
import com.cloudezz.houston.repository.UserRepository;
import com.cloudezz.houston.security.SecurityUtils;
import com.cloudezz.houston.service.ImageService;
import com.cloudezz.houston.util.RepositoryUtils;
import com.cloudezz.houston.util.SecretKeyUtil;
import com.cloudezz.houston.web.rest.dto.AppImageCfgDTO;
import com.cloudezz.houston.web.rest.dto.ApplicationDTO;
import com.cloudezz.houston.web.rest.dto.ServiceImageCfgDTO;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * REST controller for managing AppImageCfg.
 */
@RestController
@RequestMapping("/app")
public class ApplicationResource {

  private final Logger log = LoggerFactory.getLogger(ApplicationResource.class);

  @Inject
  private ApplicationRepository applicationRepository;

  @Inject
  private UserRepository userRepository;

  @Inject
  private DockerHostMachineRepository dockerHostMachineRepository;

  @Autowired
  private DeployerService deployer;

  @Autowired
  private ImageService imageService;

/*  @Autowired
  private ExecutionService executionService;*/

  @Inject
  private DeploymentScriptRepository deploymentScriptRepository;

  @Inject
  private AppImageCfgRepository appImageCfgRepository;

  @Inject
  private ServiceImageConfigRepository serviceImageConfigRepository;

  @Value("${deploymentScript.filePath}")
  private String filePath;

  /**
   * POST /rest/application -> Create a new application.
   */
  @RequestMapping(value = "/rest/application", method = RequestMethod.POST,
      produces = "application/json", consumes = "application/json")
  @Timed
  @Transactional
  public void create(@RequestBody ApplicationDTO appDto) {
    log.debug("REST request to save AppImageCfg : {}", appDto);

    try {
      Application application = createApplication(appDto);

      // set owner
      String email = SecurityUtils.getCurrentLogin();
      User currentUser = userRepository.getOne(email);
      application.setOwner(currentUser);

      application = applicationRepository.saveAndFlush(application);
      log.debug("Saved the Application : " + application.getAppName());

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private Application createApplication(ApplicationDTO appDto) throws CloudezzDeployException {
    Application application = new Application();
    application = createOrUpdateApplicationFromDto(appDto, application, false);
    return application;
  }

  private Application createOrUpdateApplicationFromDto(ApplicationDTO appDto,
      Application application, boolean isUpdate) throws CloudezzDeployException {

    application.setAppName(appDto.getAppName());

    ClusterConfig clusterConfig = new ClusterConfig();
    clusterConfig.setId(RepositoryUtils.generateBigId());
    clusterConfig.setClusterKey(SecretKeyUtil.getSerfSecretKey());
    clusterConfig.setName(appDto.getAppName());
    application.setClusterConfig(clusterConfig);

    Set<AppImageCfg> appImgCfgs = new HashSet<AppImageCfg>();
    appImgCfgs.addAll(application.getAppImageCfgs());
    application.getAppImageCfgs().clear();

    if (isUpdate) {
      for (AppImageCfgDTO appCfgDto : appDto.getAppImages()) {
        String groupName = appCfgDto.getGroupName();
        List<AppImageCfg> configs = getAppConfigsWithGroupName(appImgCfgs, groupName);
        int gpConfigCount = 0;
        if (configs != null && !configs.isEmpty()) {
          for (AppImageCfg appCfg : configs) {
            setValuesFromDTO(appCfgDto, appCfg);
            application.getAppImageCfgs().add(appCfg);
            gpConfigCount++;
            if (gpConfigCount >= appCfgDto.getNoOfInstance()) {
              break;
            }
          }
          if (gpConfigCount < appCfgDto.getNoOfInstance()) {
            int additionalInstanceCount = appCfgDto.getNoOfInstance() - gpConfigCount;
            appCfgDto.setNoOfInstance(additionalInstanceCount);
            addNewConfig(application, appCfgDto);
          }
        } else {
          addNewConfig(application, appCfgDto);
        }
      }
    } else {
      for (AppImageCfgDTO appCfgDto : appDto.getAppImages()) {
        addNewConfig(application, appCfgDto);
      }
    }
    application.setDesc(appDto.getDesc());
    application.setTags(appDto.getTags());

    if (appDto.getServiceImages() != null) {
      Set<ServiceImageCfg> serviceImgCfgs = new HashSet<ServiceImageCfg>();
      serviceImgCfgs.addAll(application.getServiceImageCfgs());
      application.getServiceImageCfgs().clear();

      if (isUpdate && serviceImgCfgs != null) {
        for (ServiceImageCfgDTO serviceCfgDto : appDto.getServiceImages()) {
          String groupName = serviceCfgDto.getGroupName();
          List<ServiceImageCfg> serviceConfigs =
              getServiceConfigsWithGroupName(serviceImgCfgs, groupName);
          int gpConfigCount = 0;
          if (serviceConfigs != null) {
            for (ServiceImageCfg serviceImageCfg : serviceConfigs) {
              setServiceConfigValuesFromDTO(serviceImageCfg, serviceCfgDto);
              imageService.setExposedPorts(serviceImageCfg, serviceCfgDto.getImageName());
              application.getServiceImageCfgs().add(serviceImageCfg);
              gpConfigCount++;
              if (gpConfigCount >= serviceCfgDto.getNoOfInstance()) {
                break;
              }
            }
            if (gpConfigCount < serviceCfgDto.getNoOfInstance()) {
              int additionalInstanceCount = serviceCfgDto.getNoOfInstance() - gpConfigCount;
              serviceCfgDto.setNoOfInstance(additionalInstanceCount);
              addNewServiceConfig(application, serviceCfgDto);
            }
          } else {
            addNewServiceConfig(application, serviceCfgDto);
          }
        }
      } else {
        for (ServiceImageCfgDTO serviceCfgDto : appDto.getServiceImages()) {
          addNewServiceConfig(application, serviceCfgDto);
        }
      }

    }
    return application;
  }

  private void addNewServiceConfig(Application application, ServiceImageCfgDTO serviceCfgDto)
      throws CloudezzDeployException {
    ServiceImageCfg serviceImageCfg = new ServiceImageCfg();
    setServiceConfigValuesFromDTO(serviceImageCfg, serviceCfgDto);
    imageService.setExposedPorts(serviceImageCfg, serviceCfgDto.getImageName());
    application.addServiceImageCfgs(serviceImageCfg, serviceCfgDto.getNoOfInstance());
  }

  private List<ServiceImageCfg> getServiceConfigsWithGroupName(Set<ServiceImageCfg> serviceImgCfgs,
      String groupName) {
    List<ServiceImageCfg> configsWithGpName = new ArrayList<ServiceImageCfg>();
    Iterator<ServiceImageCfg> serviceImgCfgsItr = serviceImgCfgs.iterator();
    while (serviceImgCfgsItr.hasNext()) {
      ServiceImageCfg serCfg = serviceImgCfgsItr.next();
      if (serCfg.getGroupName() != null && serCfg.getGroupName().equals(groupName)) {
        configsWithGpName.add(serCfg);
      }
    }
    return configsWithGpName;
  }


  private void addNewConfig(Application application, AppImageCfgDTO appCfgDto)
      throws CloudezzDeployException {
    AppImageCfg appCfg = new AppImageCfg();
    setValuesFromDTO(appCfgDto, appCfg);
    application.addAppImageCfgs(appCfg, appCfgDto.getNoOfInstance(), application.getAppName());
  }

  private List<AppImageCfg> getAppConfigsWithGroupName(Set<AppImageCfg> appImgCfgs, String groupName) {
    List<AppImageCfg> configsWithGpName = new ArrayList<AppImageCfg>();
    Iterator<AppImageCfg> appImgCfgsItr = appImgCfgs.iterator();
    while (appImgCfgsItr.hasNext()) {
      AppImageCfg appConfig = appImgCfgsItr.next();
      if (appConfig.getGroupName() != null && appConfig.getGroupName().equals(groupName)) {
        configsWithGpName.add(appConfig);
      }
    }
    return configsWithGpName;
  }

  private void setServiceConfigValuesFromDTO(ServiceImageCfg serviceImageCfg,
      ServiceImageCfgDTO serviceImageCfgDTO) {
    serviceImageCfg.setServiceName(serviceImageCfgDTO.getAppName());
    serviceImageCfg.setCpuShares(serviceImageCfgDTO.getCpuShares());
    serviceImageCfg.setDaemon(serviceImageCfgDTO.getDaemon());
    serviceImageCfg.setImageName(serviceImageCfgDTO.getImageName());
    serviceImageCfg.setDomainName(serviceImageCfgDTO.getDomainName());
    serviceImageCfg.setEnvironmentMapping(serviceImageCfgDTO.getEnvironmentMapping());
    serviceImageCfg.setHostName(serviceImageCfgDTO.getHostName());
    serviceImageCfg.setMemory(serviceImageCfgDTO.getMemory());
    serviceImageCfg.setMemorySwap(serviceImageCfgDTO.getMemorySwap());
    serviceImageCfg.setPorts(serviceImageCfg.getPorts());
    serviceImageCfg.setGroupName(serviceImageCfgDTO.getGroupName());
    serviceImageCfg.setDockerHostMachine(dockerHostMachineRepository.getOne("127.0.0.1"));
  }

  private void setValuesFromDTO(AppImageCfgDTO appimagecfgDto, AppImageCfg appCfg) {
    appCfg.setCpuShares(appimagecfgDto.getCpuShares());
    appCfg.setDaemon(appimagecfgDto.getDaemon());
    appCfg.setImageName(appimagecfgDto.getImageName());
    appCfg.setDomainName(appimagecfgDto.getDomainName());
    appCfg.setEnvironmentMapping(appimagecfgDto.getEnvironmentMapping());
    appCfg.setHostName(appimagecfgDto.getHostName());
    appCfg.setMemory(appimagecfgDto.getMemory());
    appCfg.setMemorySwap(appimagecfgDto.getMemorySwap());
    appCfg.setInitScript(appimagecfgDto.getInitScript());
    appCfg.setGroupName(appimagecfgDto.getGroupName());
    appCfg.setDockerHostMachine(dockerHostMachineRepository.getOne("127.0.0.1"));
    imageService.setExposedPorts(appCfg, appimagecfgDto.getImageName());
  }

  @RequestMapping(value = "/rest/application/start/{id}", method = RequestMethod.POST)
  @Timed
  public boolean start(@PathVariable String id, HttpServletResponse response) {
    Application application = applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    boolean success = false;
    try {
      success = deployer.start(application);
      if (!success)
        return false;

      application.setRunning(true);
      // set the exposed service after start of the image
      try {
        Set<ExposedService> exposedService = imageService.getExposedService(application);
        application.setExposedServices(exposedService);
      } catch (CloudezzException e) {
        log.error(e.getMessage());
      }
      application = applicationRepository.save(application);

    } catch (CloudezzDeployException e) {
      log.error(e.getMessage(), e);
    }
    return success;
  }


  @RequestMapping(value = "/rest/application/stop/{id}", method = RequestMethod.POST)
  @Timed
  public boolean stop(@PathVariable String id, HttpServletResponse response) {
    Application application = applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    boolean success = false;
    try {
      success = deployer.stop(application);
      if (success) {
        application.setRunning(false);
        applicationRepository.save(application);
      }
    } catch (CloudezzDeployException e) {
      log.error(e.getMessage(), e);
    }
    return success;
  }


  /**
   * GET /rest/application -> get all the application.
   */
  @RequestMapping(value = "/rest/application", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public List<Application> getAllForCurrentUser() {
    log.debug("REST request to get all Application");
    try {
      User currentLoggedInUser = userRepository.getOne(SecurityUtils.getCurrentLogin());
      return applicationRepository.getAllForUser(currentLoggedInUser);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }



  /**
   * GET /rest/application/:id -> get the "id" application.
   */
  @RequestMapping(value = "/rest/application/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public Application get(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get Application : {}", id);
    Application application = applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    return application;
  }

  /**
   * PUT /rest/application/:id -> get the "id" application.
   */
  @RequestMapping(value = "/rest/application/{id}", method = RequestMethod.POST,
      produces = "application/json")
  @Timed
  public Application update(@PathVariable String id, HttpServletResponse response,
      @RequestBody ApplicationDTO cfgDto) {
    Application application = applicationRepository.findOne(id);
    application = updateApplication(cfgDto, application);
    log.debug("REST request to get Application : {}", id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      applicationRepository.saveAndFlush(application);
    }
    return application;
  }

  private Application updateApplication(ApplicationDTO cfgDto, Application application) {
    try {
      application = createOrUpdateApplicationFromDto(cfgDto, application, true);
    } catch (CloudezzDeployException e) {
      e.printStackTrace();
    }
    return application;
  }


  /**
   * GET /rest/application/:id -> get the "id" application.
   */
  @RequestMapping(value = "/rest/application/{id}/service", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public Set<ExposedService> getServiceExposed(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get Application : {}", id);
    Application application = applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    try {
      return imageService.getExposedService(application);
    } catch (CloudezzException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    return null;

  }


  /**
   * DELETE /rest/application/:id -> delete the "id" application.
   */
  @RequestMapping(value = "/rest/application/{id}", method = RequestMethod.DELETE,
      produces = "application/json")
  @Timed
  public void delete(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to delete application : {}", id);
    try {
      Application application = applicationRepository.findOne(id);
      if (application == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      } else {
        deployer.delete(application);
        applicationRepository.delete(id);
      }
    } catch (CloudezzDeployException e) {
      log.error("Failed during delete app cfg", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

  }

  /**
   * Get DTO for application.
   */
  @RequestMapping(value = "/rest/application/dto/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public ApplicationDTO getDTO(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get DTO for application : {}", id);
    Application application = applicationRepository.findOne(id);
    if (application == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      ApplicationDTO appCfgDTO = getDTOForApplication(application);
      return appCfgDTO;
    }
    return null;
  }


  /**
   * POST /rest/application/runScript
   */
  @RequestMapping(value = "/rest/application/runScript", method = RequestMethod.POST)
  @Timed
  public void runScript(@RequestParam("id") String id, HttpServletResponse response,
      @RequestParam("appImageConfigs") List<String> appImageConfigs,
      @RequestParam("serviceImageConfigs") List<String> serviceImageConfigs,
      @RequestParam("scriptId") String scriptId, @RequestParam("command") String command) {

    try {
      if(command != null && !command.equals("")){
        // TODO
      } else {
        
        Application application = applicationRepository.findOne(id);
        DeploymentScript deploymentScript = deploymentScriptRepository.findOne(scriptId);

        List<AppImageCfg> appImageCfgs = new ArrayList<AppImageCfg>();
        List<ServiceImageCfg> serviceImageCfgs = new ArrayList<ServiceImageCfg>();

        appImageCfgs = appImageCfgRepository.getByIds(appImageConfigs);
        serviceImageCfgs = serviceImageConfigRepository.getByIds(serviceImageConfigs);
        if (application != null && deploymentScript != null && appImageCfgs != null
            && serviceImageCfgs != null) {

          List<Node> nodes = new ArrayList<Node>();


          for (Iterator<AppImageCfg> iterator = appImageCfgs.iterator(); iterator.hasNext();) {
            Node node = imageService.getNodeForImageConfig(iterator.next());
            nodes.add(node);
          }

          for (Iterator<ServiceImageCfg> iterator = serviceImageCfgs.iterator(); iterator.hasNext();) {
            Node node = imageService.getNodeForImageConfig(iterator.next());
            nodes.add(node);
          }

          File ymlFile =
              new File(filePath + File.separator + deploymentScript.getScriptId() + File.separator
                  + deploymentScript.getYmlFileName());
          InputStream ymlFileStream;

          ymlFileStream = new FileInputStream(ymlFile);

          ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
          AppScript appScript = mapper.readValue(ymlFileStream, AppScript.class);
          appScript.setAppScriptYMLDirectory(ymlFile.getParent());
          OutputStream outputStream = response.getOutputStream();

/*          executionService.execute(appScript, nodes, new ExecutionListener() {

            @Override
            public void onStdOut(Node node, String output) {
              System.out.println(output);
            }

            @Override
            public void onStdErr(Node node, String err) {
              System.out.println(err);
            }

            @Override
            public void onExecutionFailure(Throwable throwable) {
              throwable.printStackTrace();
            }
          });*/
        } else {
          // TODO
        }
        
      }
    } catch (Exception e) {
      log.error("Failed run script", e);
    }
  }


  private ApplicationDTO getDTOForApplication(Application application) {

    ApplicationDTO appDto = new ApplicationDTO();
    appDto.setAppName(application.getAppName());

    List<AppImageCfgDTO> appImgs = new ArrayList<AppImageCfgDTO>();
    Map<String, Integer> addedAppImages = new HashMap<String, Integer>();

    if (application.getAppImageCfgs() != null) {
      for (AppImageCfg appImgCfgs : application.getAppImageCfgs()) {
        if (!addedAppImages.containsKey(appImgCfgs.getGroupName())) {
          addedAppImages.put(appImgCfgs.getGroupName(), 1);
          AppImageCfgDTO appImageCfgDTO = new AppImageCfgDTO();
          appImageCfgDTO.setAppName(appImgCfgs.getAppName());
          appImageCfgDTO.setId(appImgCfgs.getId());
          appImageCfgDTO.setCpuShares(appImgCfgs.getCpuShares());
          appImageCfgDTO.setDaemon(appImgCfgs.isDaemon());
          appImageCfgDTO.setImageName(appImgCfgs.getImageName());
          appImageCfgDTO.setDomainName(appImgCfgs.getDomainName());
          appImageCfgDTO.setEnvironmentMapping(appImgCfgs.getEnvironmentMapping());
          appImageCfgDTO.setHostName(appImgCfgs.getHostName());
          appImageCfgDTO.setMemory(appImgCfgs.getMemory());
          appImageCfgDTO.setMemorySwap(appImgCfgs.getMemorySwap());
          appImageCfgDTO.setInitScript(appImgCfgs.getInitScript());
          appImageCfgDTO.setGroupName(appImgCfgs.getGroupName());
          appImgs.add(appImageCfgDTO);
        } else {
          int newCount = addedAppImages.get(appImgCfgs.getGroupName()).intValue() + 1;
          addedAppImages.put(appImgCfgs.getGroupName(), newCount);
        }
      }
      for (AppImageCfgDTO appImageCfgDTO : appImgs) {
        appImageCfgDTO.setNoOfInstance(addedAppImages.get(appImageCfgDTO.getGroupName()));
      }
      appDto.setAppImages(appImgs);
    }
    appDto.setDesc(application.getDesc());
    appDto.setTags(application.getTags());

    List<ServiceImageCfgDTO> serImgs = new ArrayList<ServiceImageCfgDTO>();
    Map<String, Integer> addedImages = new HashMap<String, Integer>();

    if (application.getServiceImageCfgs() != null) {
      for (ServiceImageCfg servImg : application.getServiceImageCfgs()) {
        if (!addedImages.containsKey(servImg.getGroupName())) {
          addedImages.put(servImg.getGroupName(), 1);
          ServiceImageCfgDTO serviceImageCfgDTO = new ServiceImageCfgDTO();
          serviceImageCfgDTO.setAppName(servImg.getServiceName());
          serviceImageCfgDTO.setId(servImg.getId());
          serviceImageCfgDTO.setCpuShares(servImg.getCpuShares());
          serviceImageCfgDTO.setDaemon(servImg.isDaemon());
          serviceImageCfgDTO.setImageName(servImg.getImageName());
          serviceImageCfgDTO.setDomainName(servImg.getDomainName());
          serviceImageCfgDTO.setEnvironmentMapping(servImg.getEnvironmentMapping());
          serviceImageCfgDTO.setHostName(servImg.getHostName());
          serviceImageCfgDTO.setMemory(servImg.getMemory());
          serviceImageCfgDTO.setMemorySwap(servImg.getMemorySwap());
          serviceImageCfgDTO.setGroupName(servImg.getGroupName());
          serImgs.add(serviceImageCfgDTO);
        } else {
          Integer count = addedImages.get(servImg.getGroupName());
          addedImages.put(servImg.getGroupName(), count++);
        }
      }
      for (ServiceImageCfgDTO serviceImageCfgDTO : serImgs) {
        serviceImageCfgDTO.setNoOfInstance(addedImages.get(serviceImageCfgDTO.getGroupName()));
      }
      appDto.setServiceImages(serImgs);
    }
    return appDto;
  }
}
