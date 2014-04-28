package com.cloudezz.houston.deployer;

import com.cloudezz.houston.deployer.docker.client.CloudezzDeployException;
import com.cloudezz.houston.domain.Application;

/**
 * Main interface to start cloudezz app instance along with the service instance that is embedded
 * inside the image
 * 
 * @author Thanneer
 * 
 */
public interface DeployerService {

  /**
   * Start the Application config along with service images it, creates containers and stores
   * them inside image config and they are started
   * 
   * @param application
   * @return
   */
  public boolean start(Application application) throws CloudezzDeployException;

  /**
   * ReStart the containers contained inside the app image config and service images that were
   * created during deploy
   * 
   * @param application
   * @return
   */

  public boolean restart(Application application) throws CloudezzDeployException;

  /**
   * Stop the containers contained inside the app image config and service images that were created
   * during deploy
   * 
   * @param application
   * @return
   */
  public boolean stop(Application application) throws CloudezzDeployException;

  /**
   * Delete only the containers contained inside the app image config and service images that were
   * created during deploy. The config is not deleted .
   * 
   * @param application
   * @return
   */
  public boolean delete(Application application) throws CloudezzDeployException;


}
