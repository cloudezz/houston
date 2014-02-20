package com.cloudezz.houston.deployer;

import com.cloudezz.houston.domain.ApplicationImageConfig;

/**
 * Main interface to start cloudezz app instance along with the service instance
 * that is embedded inside the image
 * 
 * @author Thanneer
 * 
 */
public interface CloudezzDeployer {

	/**
	 * Deploy the app image config along with service images. The deploy creates
	 * containers and stores them inside image config and they are started
	 * 
	 * @param appImageConfig
	 * @return
	 */
	public boolean deploy(ApplicationImageConfig appImageConfig);

	/**
	 * Start the containers contained inside the app image config and service
	 * images that were created during deploy
	 * 
	 * @param appImageConfig
	 * @return
	 */
	public boolean start(ApplicationImageConfig appImageConfig);

	/**
	 * ReStart the containers contained inside the app image config and service
	 * images that were created during deploy
	 * 
	 * @param appImageConfig
	 * @return
	 */

	public boolean restart(ApplicationImageConfig appImageConfig);

	/**
	 * Stop the containers contained inside the app image config and service
	 * images that were created during deploy
	 * 
	 * @param appImageConfig
	 * @return
	 */
	public boolean stop(ApplicationImageConfig appImageConfig);

	/**
	 * Delete only the containers contained inside the app image config and
	 * service images that were created during deploy. The config is not deleted
	 * if the config has to be delete then call {@link CloudezzDeployer}
	 * .delete(ApplicationImageConfig appImageConfig) method.
	 * 
	 * @param appImageConfig
	 * @return
	 */
	public boolean deleteInstance(ApplicationImageConfig appImageConfig);

	/**
	 * If the instance is running then they are stopped and the containers are
	 * deleted then the config is deleted from account
	 * 
	 * @param appImageConfig
	 * @return
	 */
	public boolean delete(ApplicationImageConfig appImageConfig);

}
