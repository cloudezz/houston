package com.cloudezz.houston.deployer.docker.client;

public class DockerConstant {

  public static final int STATUS_NO_ERROR = 204;
  
  public static final int STATUS_NOT_MODIFIED = 304;

  public static final int STATUS_OK = 200;

  public static final int STATUS_CONFLICT = 409;

  public static final int STATUS_NO_SUCH_ENTITY = 404;

  public static final int STATUS_SERVER_ERROR = 500;

  public static final String ENV_TCP_FORWARD = "TCP_FORWARD";

  public static final String ENV_SERF_HOST_PORT = "SERF_HOST_PORT";

  public static final String ENV_SERF_CLUSTER_KEY = "SERF_CLUSTER_KEY";

  public static final String ENV_SERF_CLUSTER_ID = "SERF_CLUSTER_ID";

  public static final String ENV_SERF_ROLE = "SERF_ROLE";

  public static final String ENV_IS_SERVICE = "IS_SERVICE";

  public static final String ENV_DEFAULT_PORT_TO_EXPOSE = "DEFAULT_PORT_TO_EXPOSE";

  public static final String ENV_HOST_IP = "HOST_IP";

  public static final String ENV_DEFAULT_HOST_PORT_TO_EXPOSE = "DEFAULT_HOST_PORT_TO_EXPOSE";

  public static final String ENV_HOUSTON_HOST_IP = "HOUSTON_HOST_IP";

  public static final String ENV_HOUSTON_PORT = "HOUSTON_PORT";
  
  public static final String ENV_HOUSTON_PROTOCOL = "HOUSTON_PROTOCOL";
  
  public static final String ENV_HOUSTON_PROTOCOL_VALUE = "http";

  public static final String ENV_SSH_ROOT_PASSWORD = "ROOT_SSH_PASSWD";

  public static final String ENV_RUN_BUILD_PACK = "RUN_BUILD_PACK";

  public static final String ENV_WEB_SHELL = "WEB_SHELL";

  public static final String GIT_URL = "gitURL";

  public static final String DEFAULT_USER = "root";

  public static final Integer SSH_PORT = 22;
  
  public static final String DEFAULT_SSH_PORT = "22/tcp";

  public static final String SSH_SERVICE_NAME = "SSH";

  public static final String WEB_SHELL_SERVICE_NAME = "Web Shell";

  public static final String DEFAULT_WEB_SHELL_PORT = "4022/tcp";

  public static final String DEFAULT_SERF_PORT = "7946";

  public static final String FOLDER_DATA_DOCKER_SIDE = "/cloudezz/data";

  public static final String FOLDER_APP_DOCKER_SIDE = "/cloudezz/app";

  public static final String FOLDER_APP_MAIN_DOCKER_HOST_SIDE = "/cloudezz/instance/%s";

  public static final String FOLDER_APP_DOCKER_HOST_SIDE = FOLDER_APP_MAIN_DOCKER_HOST_SIDE
      + "/app";

  public static final String FOLDER_DATA_DOCKER_HOST_SIDE = FOLDER_APP_MAIN_DOCKER_HOST_SIDE
      + "/data";

  public static final String FOLDER_CLOUDEZZ_CONFIG = "/cloudezz-config";

  public static final String FILE_CLOUDEZZ_INIT_SH = "cloudezz-init.sh";

  public static final String VOL_APP_FOLDER_MAPPING_XML_VAR = "$app_folder";

  public static final String VOL_DATA_FOLDER_MAPPING_XML_VAR = "$data_folder";
  
  public static final String PROP_HOUSTON_PORT = "server.port";
  
  public static final String PROP_HOUSTON_IP = "server.address";



}
