package com.cloudezz.houston.deployer.docker.client;



@SuppressWarnings("serial")
public class DockerImageStopException extends RuntimeException {

    public DockerImageStopException() {
    }

    public DockerImageStopException(String message) {
        super(message);
    }

    public DockerImageStopException(String message, Throwable cause) {
        super(message, cause);
    }

    public DockerImageStopException(Throwable cause) {
        super(cause);
    }
}
