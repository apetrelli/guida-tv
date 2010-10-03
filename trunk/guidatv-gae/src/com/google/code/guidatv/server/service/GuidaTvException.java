package com.google.code.guidatv.server.service;

public class GuidaTvException extends RuntimeException {

    private static final long serialVersionUID = -2872887889226150063L;

    public GuidaTvException() {
    }

    public GuidaTvException(String message) {
        super(message);
    }

    public GuidaTvException(Throwable cause) {
        super(cause);
    }

    public GuidaTvException(String message, Throwable cause) {
        super(message, cause);
    }

}
