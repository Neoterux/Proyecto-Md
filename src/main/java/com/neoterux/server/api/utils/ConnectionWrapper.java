package com.neoterux.server.api.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public final class ConnectionWrapper {
    
    private final BufferedWriter out;
    
    private final BufferedReader in;
    
    private final Socket socket;
    
    public ConnectionWrapper (Socket sock, BufferedReader reader, BufferedWriter writer) {
        this.socket = sock;
        this.in = reader;
        this.out = writer;
    }
    
    public void writeLine (String str) throws IOException {
        out.write(str);
        out.newLine();
        out.flush();
    }
    
    public BufferedReader getIn () {
        return in;
    }
    
    public BufferedWriter getOut () {
        return out;
    }
    
    public Socket getSocket () {
        return socket;
    }
    
    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o != null && o == this.socket) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionWrapper that = (ConnectionWrapper) o;
        return Objects.equals(socket, that.socket);
    }
}
