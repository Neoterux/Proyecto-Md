package com.neoterux.server.api;

import java.net.Socket;
import java.util.Objects;

/**
 * <h1>Client</h1>
 * <p>
 * This class represent a client connected to the server.
 * </p>
 */
public class Client {
    /**
     * The hostname of the client
     */
    private final String hostname;
    /**
     * The ip of the client
     */
    private final String ip;
    
    /**
     * The connection socket of the client.
     */
    private final Socket connectionSocket;
    
    /**
     * Check if the current client is the Owner of the game
     */
    private final boolean isOwner;
    
    /**
     * Create a simple client, with no owner permissions.
     *
     * @param connectionSocket The connection socket of the client
     *
     * @throws NullPointerException if the given {@literal connectionSocket} is null
     */
    Client (Socket connectionSocket) {
        this(connectionSocket, false);
    }
    
    /**
     * Create a full custom client,
     *
     * @param connectionSocket The connection socket of the client
     * @param isOwner          The owner permission flag
     *
     * @throws NullPointerException if the given  {@literal connectionSocket} is null
     */
    Client (Socket connectionSocket, boolean isOwner) {
        Objects.requireNonNull(connectionSocket);
        this.connectionSocket = connectionSocket;
        this.ip = connectionSocket.getInetAddress().getHostAddress();
        this.hostname = connectionSocket.getInetAddress().getHostName();
        this.isOwner = isOwner;
    }
    
    public Socket getConnectionSocket () {
        return connectionSocket;
    }
    
    public String getHostname () {
        return hostname;
    }
    
    public String getIp () {
        return ip;
    }
    
    public boolean isOwner () {
        return isOwner;
    }
}
