package com.neoterux.server.api;

import com.neoterux.server.api.exceptions.ServerRunningException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <h1>Server</h1>
 * <p>The main server that administrate the resources of
 * a game</p>
 *
 * @author Neoterux
 * @version 0.0.1
 * @since 0.0.1
 */
final class Server implements Runnable {
    
    /**
     * The logger for the Server class
     */
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    
    private static final String LOCALHOST = "127.0.0.1";
    
    private static final int POOL_SIZE = 5;
    private static final ThreadPoolExecutor tpool = (ThreadPoolExecutor) Executors.newFixedThreadPool(POOL_SIZE);
    /**
     * The instance of the current server.
     */
    volatile static private Server instance;
    private final List<Client> connectedClients;
    /**
     * The server socket to comunicate through games
     */
    private ServerSocket socket;
    /**
     * Sccket to allow the discovery.
     */
    private DatagramSocket lookupSocket;
    /**
     * This parameter is only to determine if the server is running
     */
    private boolean isRunning = false;
    
    private Server () {
        connectedClients = new ArrayList<>();
        try {
            this.socket = new ServerSocket(0);
            log.info("Starting Servet at port: {}", socket.getLocalPort());
            this.lookupSocket = new DatagramSocket(socket.getLocalPort());
            this.lookupSocket.setBroadcast(true);
            reload();
            isRunning = true;
        } catch (SocketException se) {
            log.error("Error while setting up lookup", se);
        } catch (IOException ioe) {
            log.error("Error while starting server socket", ioe);
        }
    }
    
    public static void startServer () {
        if (instance == null)
            instance = new Server();
        else if (instance.isRunning)
            throw new ServerRunningException("server already running");
        else {
            log.warn("local server would reload");
            instance.reload();
        }
    }
    
    /**
     * This would run until the server is shutdown.
     */
    @Override
    public void run () {
        while (isRunning) {
            try {
                Socket clnsock = socket.accept();
                boolean isLocal = clnsock.getInetAddress().getHostName().equals(LOCALHOST);
                boolean ownerExists = connectedClients.stream().anyMatch(Client::isOwner);
                connectedClients.add(new Client(clnsock, isLocal && !ownerExists));
            } catch (IOException ioe) {
                log.error("Error ");
            }
            
        }
    }
    
    public int getPort () {
        if (this.socket == null)
            return -1;
        else
            return this.socket.getLocalPort();
    }
    
    /**
     * This function is destructive, and would force shutdown of
     * the server.
     *
     * @return true, if the
     */
    public boolean shutdown () {
        if (this.socket == null)
            return false;
        try {
            socket.close();
            lookupSocket.close();
            isRunning = false;
        } catch (IOException se) {
            log.error("Error while trying to close socket", se);
        }
        return socket.isClosed();
    }
    
    public boolean release () {
        return false;
    }
    
    /**
     * This function would reload the resources of the current session.
     *
     * @return true if has no problem with the resources
     */
    public boolean reload () {
        if (isRunning)
            log.info("Forced Reloading resources");
        else
            log.info("Reloading resources.");
        return false;
    }
    
    /**
     * @return true if the server is running.
     */
    public boolean isRunning () {
        return isRunning;
    }
}
