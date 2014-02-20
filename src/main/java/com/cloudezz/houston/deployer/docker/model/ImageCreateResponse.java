package com.cloudezz.houston.deployer.docker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageCreateResponse {

    @JsonProperty("status")
    public String status;

    @Override
    public String toString() {
        return "ImageCreateResponse{" +
                "status=" + status +
                '}';
    }
}
