package com.neoterux.pmd.model;

import java.io.Serializable;

public final class Game implements Serializable {
    
    private final String name;
    private final String hostname;
    
    
    public Game (String name, String hostname) {
        this.name = name;
        this.hostname = hostname;
    }
    
    /**
     * @return true if the query with the server goes well.
     */
    public boolean isOnline () {
        //TODO: make a connection via UDP and fetch data
        return false;
    }
    
    public String getHostname () {
        return hostname;
    }
    
    public String getName () {
        return name;
    }
    
    public String getPort () {
        try {
            return hostname.split(":")[1];
        } catch (IndexOutOfBoundsException e) {
            return "err";
        }
    }
}
