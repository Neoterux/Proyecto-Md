package com.neoterux.server.api.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

/**
 * <h1>Connection Wrapper</h1>
 * This class hold and provide a simple way to write/read
 * into socket, at the same time that is a container for the
 * socket.
 */
public final class ConnectionWrapper {
    
    /**
     * The output writer of the Connection
     */
    private final BufferedWriter out;
    
    /**
     * The input reader of the Connection
     */
    private final BufferedReader in;
    
    private final DataOutputStream dout;
    
    /**
     * The Socket of the connection
     */
    private final Socket socket;
    
    /**
     * Create a new Connection wrapper for a socket and the given
     * Reader/Writter.
     *
     * @param sock   The socket that this class Wrap
     * @param reader The reader of the socket
     * @param writer The writter of the Socket
     *
     * @throws IOException if something goes wrong
     */
    public ConnectionWrapper (Socket sock, BufferedReader reader, BufferedWriter writer) throws IOException {
        this.dout = new DataOutputStream(sock.getOutputStream());
        this.socket = sock;
        this.in = reader;
        this.out = writer;
    }
    
    /**
     * Write the given string to the socket with a new-line
     * termination.
     *
     * @param str The string to write
     *
     * @throws IOException if is there an error while writting to the
     *                     socket
     */
    public void writeLine (String str) throws IOException {
        out.write(str);
        out.newLine();
        out.flush();
    }
    
    /**
     * Gets the Reader of the Wrapper.
     *
     * @return the reader
     */
    public BufferedReader getIn () {
        return in;
    }
    
    /**
     * Gets the Writer of the Wrapper.
     *
     * @return the writer
     */
    public BufferedWriter getOut () {
        return out;
    }
    
    /**
     * Gets the socket of this Wrapper.
     *
     * @return the socket
     */
    public Socket getSocket () {
        return socket;
    }
    
    public DataOutputStream getDout () {
        return dout;
    }
    
    public boolean isAlive() {
        return this.getSocket().isConnected();
    }
    
    /**
     * Compare a given Socket or a Wrapper object.
     * If wrap the same connection.
     *
     * @param o The object to compare
     *
     * @return true if the given object is the same
     * connection/wrapper.
     */
    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o != null && o == this.socket) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionWrapper that = (ConnectionWrapper) o;
        return Objects.equals(socket, that.socket);
    }
}
