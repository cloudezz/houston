package com.cloudezz.houston.web.filter.gzip;

import javax.servlet.ServletException;

public class GzipResponseHeadersNotModifiableException extends ServletException {

  private static final long serialVersionUID = 4270831502526827753L;

    public GzipResponseHeadersNotModifiableException(String message) {
        super(message);
    }
}
