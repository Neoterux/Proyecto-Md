package com.neoterux.server.api;

import com.neoterux.server.api.exceptions.ServerRunningException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

final class Server {

    /**
     * The logger for the Server class
     */
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    /**
     * The instance of the current server.
     */
    volatile static private Server instance;

    private ServerSocket socket;

    private Server() {
        try {
            this.socket = new ServerSocket(0);
            log.info("Starting Servet at port: {}", socket.getLocalPort());
        } catch (IOException ioe) {
            log.error("Error while starting server socket", ioe);
        }
    }


    public static void startServer() {
        if (instance == null) {
            instance = new Server();
            return;
        }
        throw new ServerRunningException("server already running");
    }

    public int getPort() {
        if (this.socket == null)
            return -1;
        else
            return this.socket.getLocalPort();
    }

    public boolean shutdown() {
        if (this.socket == null)
            return false;
        try {
            socket.close();
        } catch (IOException se) {
            log.error("Error while trying to close socket", se);
        }
        return socket.isClosed();
    }
}
