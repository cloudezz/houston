package com.cloudezz.houston.deployer.docker.client;

public class DockerConstant {

  public static final int STATUS_NO_ERROR = 204;

  public static final int STATUS_OK = 200;

  public static final int STATUS_NO_SUCH_ENTITY = 404;

  public static final int STATUS_SERVER_ERROR = 500;

  public static final String ENV_TCP_FORWARD = "TCP_FORWARD";

  public static final String ENV_SSH_ROOT_PASSWORD = "SSH_ROOT_PASSWORD";

  public static final String ENV_RUN_BUILD_PACK = "RUN_BUILD_PACK";

  public static final String GIT_URL = "gitURL";

  public static final String DEFAULT_USER = "root";
  
  public static final String DEFAULT_SSH_PORT = "22/tcp";
  
  public static final String SSH_SERVICE_NAME = "SSH";
  
  public static final String WEB_SHELL_SERVICE_NAME = "Web Shell";
  
  public static final String DEFAULT_WEB_SHELL_PORT = "4022/tcp";
  
  public static final String FOLDER_DATA_DOCKER_SIDE = "/cloudezz/data";
  
  public static final String FOLDER_APP_DOCKER_SIDE = "/cloudezz/app";
  
  public static final String FOLDER_APP_MAIN_DOCKER_HOST_SIDE = "/cloudezz/instance/%s";
  
  public static final String FOLDER_APP_DOCKER_HOST_SIDE = FOLDER_APP_MAIN_DOCKER_HOST_SIDE+"/app";
  
  public static final String FOLDER_DATA_DOCKER_HOST_SIDE = FOLDER_APP_MAIN_DOCKER_HOST_SIDE+"/data";
  
  public static final String FOLDER_CLOUDEZZ_CONFIG = "/cloudezz-config";
  
  public static final String FILE_CLOUDEZZ_INIT_SH = "cloudezz-init.sh";
  
  public static final String VOL_APP_FOLDER_MAPPING_XML_VAR = "$app_folder";
  
  public static final String VOL_DATA_FOLDER_MAPPING_XML_VAR = "$data_folder";
  

}
