package com.cloudezz.houston.domain;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * App image info object that hold information related to application like
 * tomcat web app or php app or wordpress app and would be having zero or more
 * service images linked to it for DB or cache etc service . These images
 * objects represent how the images will be build and run as containers in
 * docker
 * 
 * @author Thanneer
 * 
 */
public class ApplicationImageConfig extends BaseCloudezzImageConfig {

	protected static final String TCP_FORWARD = "TCP_FORWARD";

	protected static final String GIT_URL = "gitURL";

	private String gitURL;
	
	private String appName;

	private List<ServiceImageConfig> serviceImages = new LinkedList<ServiceImageConfig>();

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the gitURL
	 */
	public String getGitURL() {
		return gitURL;
	}

	/**
	 * @param gitURL
	 *            the gitURL to set
	 */
	public void setGitURL(String gitURL) {
		this.gitURL = gitURL;
	}

	/**
	 * @return the serviceImages
	 */
	public List<ServiceImageConfig> getServiceImages() {
		return serviceImages;
	}

	/**
	 * @param serviceImages
	 *            the serviceImages to set
	 */
	public void setServiceImages(List<ServiceImageConfig> serviceImages) {
		Preconditions
				.checkArgument(serviceImages != null && serviceImages.size() > 0, "Service Images cannot be empty");
		this.serviceImages = serviceImages;
	}

	/**
	 * @param serviceImages
	 *            the serviceImages to set
	 */
	public void addServiceImages(ServiceImageConfig serviceImage) {
		Preconditions.checkNotNull(serviceImage, "Service Image cannot be null");
		this.serviceImages.add(serviceImage);

	}

}
