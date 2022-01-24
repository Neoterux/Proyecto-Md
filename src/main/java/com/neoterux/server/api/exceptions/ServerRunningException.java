package com.neoterux.server.api.exceptions;

public class ServerRunningException extends RuntimeException {

    public ServerRunningException(String msg) {
        super(msg);
    }
}
