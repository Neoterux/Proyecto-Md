package com.neoterux.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <h1>Client</h1>
 * <p>
 * This class represent a client connected to the server.
 * </p>
 */
public class Client {
    /**
     * The buffer size of the client Buffered Writter/Reader
     */
    public static final int BUFFER_SIZE = 10240;
    /**
     * The logger for the Client Class
     */
    private static final Logger log = LoggerFactory.getLogger(Client.class);
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
     * The stream of input bytes form the socket
     */
//    private BufferedReader input;
    private static final String OS_SEPARATOR = System.getProperty("line.separator");

    private BufferedInputStream inputStream;
    
    /**
     * The stream of output bytes to the socket
     */
    private BufferedWriter output;
    private Runnable onForceQuitListener;
    
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
        configureStreams(connectionSocket);
    }
    
    public static Client connectTo (String hostname, int port) throws IOException {
        Socket sck = new Socket(hostname, port);
        return new Client(sck);
    }
    
    public void setOnForceQuitListener (Runnable listener) {
        this.onForceQuitListener = listener;
    }
    
    private void configureStreams (Socket sck) {
        try {
            this.inputStream = new BufferedInputStream(sck.getInputStream(), BUFFER_SIZE);
            this.output = new BufferedWriter(new OutputStreamWriter(sck.getOutputStream(), StandardCharsets.UTF_8),
                                             BUFFER_SIZE
            );
        } catch (IOException ioe) {
            log.error(String.format("[%s] IOException while configuring streams", getHostname()), ioe);
        }
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
    
    private String readline () throws IOException {
        StringBuilder str = new StringBuilder();
        char c;
        do{
            c = (char) this.inputStream.read();
            if (c > 255)
                log.warn("<readline> Maybe invalid character obtained {}", c);
            str.append(c);
        }while (this.inputStream.available() > 0);
        String rawOut = str.toString();
        log.debug("Readed line [{}]: {}",rawOut.length(), rawOut);
        return rawOut.replace(OS_SEPARATOR, "");
    }
    
    public String awaitCommand () throws EOFException {
        try {
            log.debug("waiting command...");
            String cmd = readline();
            if (onForceQuitListener != null && cmd.equalsIgnoreCase("$force-close$")) {
                log.warn("Force quit executed");
                onForceQuitListener.run();
            }
            log.debug("Command fetched: {}", cmd);
            return cmd;
        } catch (SocketException se) {
            if (onForceQuitListener != null)
                onForceQuitListener.run();
        } catch (EOFException e) {
            throw e;
        } catch (IOException ioe) {
            log.error("Unknown IOException", ioe);
        }
        return null;
    }
    
    /**
     * Reads a block of bytes from the socket, with a max size of {@literal size}.
     *
     * @param size the max size of the bytes block
     *
     * @return the byte block read from the socket
     */
    public byte[] getBytes (int size) {
        log.debug("Reading bytes [{}]", size);
        byte[] buffer = new byte[size];
        try {
            int res = this.inputStream.read(buffer, 0, size);
            if (res < 0) {
                log.error("Cannot receive all bytes");
            }
        } catch (IOException ioe) {
            log.error("Error while reading bytes", ioe);
            return new byte[0];
        }
        return buffer;
    }
    
    /**
     * Read an integer from the socket.
     *
     * @return an int from the socket
     *
     * @throws NumberFormatException if the socket doesn't contain an integer
     * @throws IOException           If there an error while reading
     */
    public int awaitInt () throws NumberFormatException, IOException {
        return Integer.parseInt(awaitCommand());
    }
    
    @SuppressWarnings ("UnusedReturnValue")
    public boolean sendCommand (String cmd) {
        log.debug("Sending command: {}", cmd);
        try {
            this.output.write(cmd);
            this.output.newLine();
            this.output.flush();
        } catch (SocketException se) {
            if (onForceQuitListener != null)
                onForceQuitListener.run();
        } catch (IOException ioe) {
            log.error("Cannot send the command to server", ioe);
            return false;
        }
        return true;
    }
    
    public void close () {
        try {
            sendCommand("unattached");
            this.output.close();
            this.inputStream.close();
            this.connectionSocket.close();
        } catch (IOException ioe) {
            log.error("Error while closing client", ioe);
        }
    }
    
    @Override
    public String toString () {
        return "Client{" +
               "address=" + connectionSocket.getInetAddress() +
               ", port=" + connectionSocket.getPort() +
               ", local-port=" + connectionSocket.getLocalPort() +
               ", isOwner=" + isOwner +
               "}";
    }
}
