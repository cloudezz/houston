package com.cloudezz.houston.deployer.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageInspectResponse {

    @JsonProperty("id") public String id;
    @JsonProperty("parent") public String parent;
    @JsonProperty("created") public String created;
    @JsonProperty("container") public String container;
    @JsonProperty("container_config") public ContainerConfig containerConfig;
    @JsonProperty("Size") public int size;
    @JsonProperty("docker_version") public String dockerVersion;
    @JsonProperty("config") public ContainerConfig config;
    @JsonProperty("architecture") public String arch;
    @JsonProperty("comment") public String comment;
    @JsonProperty("author") public String author;


    @Override
    public String toString() {
        return "ImageInspectResponse{" +
                "id='" + id + '\'' +
                ", parent='" + parent + '\'' +
                ", created='" + created + '\'' +
                ", container='" + container + '\'' +
                ", containerConfig=" + containerConfig +
                ", size=" + size +
                ", dockerVersion='" + dockerVersion + '\'' +
                ", config=" + config +
                ", arch='" + arch + '\'' +
                ", comment='" + comment + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
