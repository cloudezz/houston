package com.cloudezz.houston.deployer;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.domain.AppImageCfg;

/**
 * Main interface to start cloudezz app instance along with the service instance that is embedded
 * inside the image
 * 
 * @author Thanneer
 * 
 */
public interface DeployerService {

  /**
   * Start the app image config along with service images it, creates containers and stores
   * them inside image config and they are started
   * 
   * @param appImageConfig
   * @return
   */
  public boolean start(AppImageCfg appImageConfig) throws CloudezzDeployException;

  /**
   * ReStart the containers contained inside the app image config and service images that were
   * created during deploy
   * 
   * @param appImageConfig
   * @return
   */

  public boolean restart(AppImageCfg appImageConfig) throws CloudezzDeployException;

  /**
   * Stop the containers contained inside the app image config and service images that were created
   * during deploy
   * 
   * @param appImageConfig
   * @return
   */
  public boolean stop(AppImageCfg appImageConfig) throws CloudezzDeployException;

  /**
   * Delete only the containers contained inside the app image config and service images that were
   * created during deploy. The config is not deleted .
   * 
   * @param appImageConfig
   * @return
   */
  public boolean delete(AppImageCfg appImageConfig) throws CloudezzDeployException;


}
