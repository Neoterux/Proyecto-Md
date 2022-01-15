package com.neoterux.server;

import com.neoterux.server.exceptions.ServerRunningException;

public final class Server {

    volatile static private Server instance;


    private Server() {
    }


    public static void startServer() {
        if (instance == null) {
            instance = new Server();
            return;
        }
        throw new ServerRunningException("server already running");
    }
}
