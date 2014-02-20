package com.cloudezz.houston.deployer.docker.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ContainerCreateResponse {

	@JsonProperty("Id")
	private String id;

	@JsonProperty("Warnings")
	private String[] warnings;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getWarnings() {
		return warnings;
	}

	public void setWarnings(String[] warnings) {
		this.warnings = warnings;
	}

	@Override
	public String toString() {
		return "Container Created {" + "id='" + id + '\'' + ", warnings="
				+ Arrays.toString(warnings) + '}';
	}
}
