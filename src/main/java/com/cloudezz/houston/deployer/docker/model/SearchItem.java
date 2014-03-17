package com.cloudezz.houston.deployer.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchItem {

	@JsonProperty("star_count")
	public int starCount;

	@JsonProperty("is_official")
	public boolean isOfficial;

	public String name;

	@JsonProperty("is_trusted")
	public boolean isTrusted;

	public String description;

	@Override
	public String toString() {
		return "star_count='" + starCount + '\'' + ", is_official='" + isOfficial + '\'' + ", name='" + name + '\''
				+ ", is_trusted='" + isTrusted + '\'' + ", description='" + description + '\'' + '}';
	}
}
