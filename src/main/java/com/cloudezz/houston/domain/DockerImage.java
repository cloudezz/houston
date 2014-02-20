package com.cloudezz.houston.domain;

/**
 * Docker Image is the one hold the map between the build back git url and
 * docker image name . This object is a jpa object to the table that holds the
 * map info
 * 
 * @author Thanneer
 * 
 */
public class DockerImage {

	private String imageName;

	private String buildPackGitURL;

	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName
	 *            the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * @return the buildPackGitURL
	 */
	public String getBuildPackGitURL() {
		return buildPackGitURL;
	}

	/**
	 * @param buildPackGitURL
	 *            the buildPackGitURL to set
	 */
	public void setBuildPackGitURL(String buildPackGitURL) {
		this.buildPackGitURL = buildPackGitURL;
	}

}
