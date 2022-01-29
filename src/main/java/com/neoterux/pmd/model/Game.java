package com.neoterux.pmd.model;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * <h1>Game</h1>
 * This class represents the basic information for a game.
 * This is used as a quick access to previous saved game server
 */
public record Game(String name, String hostname, int port) implements Serializable {
    
    /**
     * @return true if the query with the server goes well.
     */
    public boolean isOnline () {
        boolean isActive = true;
        try {
            InetSocketAddress address = new InetSocketAddress(hostname, port);
            Socket socket = new Socket();
            socket.connect(address, 1);
            socket.close();
        } catch (IOException ioe) {
            isActive = false;
        }
        return isActive;
    }
    
    public String getHostname () {
        return hostname;
    }
    
    public String getFullHost () {
        return hostname;
    }
    
    public String getName () {
        return name;
    }
    
    public int getPort () {
        return port;
    }
}
