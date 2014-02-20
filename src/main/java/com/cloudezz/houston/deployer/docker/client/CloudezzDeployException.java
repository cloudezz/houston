package com.cloudezz.houston.deployer.docker.client;



@SuppressWarnings("serial")
public class CloudezzDeployException extends Exception {

    public CloudezzDeployException() {
    }

    public CloudezzDeployException(String message) {
        super(message);
    }

    public CloudezzDeployException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudezzDeployException(Throwable cause) {
        super(cause);
    }
}
