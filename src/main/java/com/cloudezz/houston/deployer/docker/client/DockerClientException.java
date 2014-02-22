package com.cloudezz.houston.deployer.docker.client;



@SuppressWarnings("serial")
public class DockerClientException extends CloudezzDeployException {

    public DockerClientException() {
    }

    public DockerClientException(String message) {
        super(message);
    }

    public DockerClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public DockerClientException(Throwable cause) {
        super(cause);
    }
}
