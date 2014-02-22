package com.cloudezz.houston.deployer.docker.client;



@SuppressWarnings("serial")
public class DockerImageStartException extends RuntimeException {

    public DockerImageStartException() {
    }

    public DockerImageStartException(String message) {
        super(message);
    }

    public DockerImageStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public DockerImageStartException(Throwable cause) {
        super(cause);
    }
}
