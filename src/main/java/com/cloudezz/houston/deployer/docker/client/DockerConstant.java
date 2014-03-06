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
  
  public static final String DEFAULT_SSH_PORT = "22";
  
  public static final String DEFAULT_WEB_SHELL_PORT = "4022";



}
