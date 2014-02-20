package com.cloudezz.houston.deployer.docker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContainerWaitResponse {

	@JsonProperty("StatusCode")
	private int statusCode;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
