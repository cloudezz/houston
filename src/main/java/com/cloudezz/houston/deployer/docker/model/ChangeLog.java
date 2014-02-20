package com.cloudezz.houston.deployer.docker.model;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ChangeLog {

    @JsonProperty("Path")
    public String path;

    @JsonProperty("Kind")
    public int kind;

    @Override
    public String toString() {
        return "ChangeLog{" +
                "path='" + path + '\'' +
                ", kind=" + kind +
                '}';
    }
}
