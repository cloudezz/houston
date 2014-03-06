package com.cloudezz.houston.deployer.docker.client;



@SuppressWarnings("serial")
public class CloudezzException extends Exception {

    public CloudezzException() {
    }

    public CloudezzException(String message) {
        super(message);
    }

    public CloudezzException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudezzException(Throwable cause) {
        super(cause);
    }
}
