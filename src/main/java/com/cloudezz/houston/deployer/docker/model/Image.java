package com.cloudezz.houston.deployer.docker.model;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Image {

	@JsonProperty("RepoTags")
	public String[] repoTags;

	@JsonProperty("Repository")
	public String repository;

	@JsonProperty("Tag")
	public String tag;

	@JsonProperty("Id")
	public String id;

	@JsonProperty("Created")
	public long created;

	@JsonProperty("Size")
	public long size;

	@JsonProperty("VirtualSize")
	public long virtualSize;

	@JsonProperty("ParentId")
	public String parentId;

	@Override
	public String toString() {
		return "Image{" + "repoTags='" + Arrays.toString(repoTags) + '\'' + ", repository='" + repository + '\''
				+ ", tag='" + tag + '\'' + ", id='" + id + '\'' + ", created=" + created + ", size=" + size
				+ ", parentId='" + parentId + '\'' + ", virtualSize=" + virtualSize + '}';
	}

}
