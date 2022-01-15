package com.neoterux.server.exceptions;

public class ServerRunningException extends RuntimeException {

    public ServerRunningException(String msg) {
        super(msg);
    }
}
