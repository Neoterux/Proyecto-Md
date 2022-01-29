package com.neoterux.server.api;

import com.neoterux.server.api.exceptions.ServerRunningException;
import com.neoterux.server.api.utils.ConnectionWrapper;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Server</h1>
 * <p>The main server that administrate the resources of
 * a game</p>
 *
 * @author Neoterux
 * @version 0.5.9
 * @since 0.0.1
 */
final class Server implements Runnable {
    
    /**
     * The default port of a Game Room
     */
    public static final int DEF_PORT = 62329;
    
    /**
     * The Map of the loaded images files that would be
     * sent to the players
     */
    public static final ObservableMap<String, byte[]> images = FXCollections.observableHashMap();
    
    /**
     * The logger for the Server class
     */
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static final String LOCALHOST = "127.0.0.1";
    /**
     * The max quantity of connections that can be handled by the
     * Server
     */
    private static final int POOL_SIZE = 5;
    
    /**
     * The pool of threads that would handle the clients connections
     */
    private static final ThreadPoolExecutor tpool = (ThreadPoolExecutor) Executors.newFixedThreadPool(POOL_SIZE);
    
    /**
     * The instance of the current server.
     */
    volatile static private Server instance;
    
    static {
        tpool.setThreadFactory(new BasicThreadFactory.Builder().namingPattern("ServerClientPool-%d").build());
    }
    
    private final Deque<ConnectionWrapper> connectionList;
//    private final ObjectProperty<Socket> ownerSocketProperty;
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
    private Socket ownerSocket;
    private ConnectionWrapper ownerWrapper;
    
    private volatile String targetString;
    
    /**
     * Creates a new Server instance
     */
    Server () {
//        this.ownerSocketProperty = new SimpleObjectProperty<>();
//        ownerSocketProperty.addListener(this::ownerSocketChange);
        connectionList = new LinkedList<>();
        reload();
        try {
            try {
                this.socket = new ServerSocket(DEF_PORT);
                log.info("Successfully use the default port");
            } catch (IOException ioe) {
                log.error("Cannot use default port, fallback to auto port");
                this.socket = new ServerSocket(0);
            }
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
    
    /**
     * Starts the Singleton server instance
     */
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
    @SuppressWarnings ("SuspiciousMethodCalls")
    @Override
    public void run () {
        log.info("Starting server at port: {}", socket.getLocalPort());
        while (true) {
            if (socket.isClosed()) {
                log.info("Server closed");
                tpool.shutdownNow();
                break;
            }
            try {
                Socket connection = socket.accept();
                if (ownerSocket == null) {
                    ownerSocket = connection;
                }
                tpool.execute(() -> {
                    try {
                        this.attendClient(connection);
                    } catch (Exception e) {
                        log.error(String.format("An unhandled error occurred in client %s", connection), e);
                        connectionList.remove(connection);
                    }
                });
    
            } catch (SocketException se) {
                if (se.getMessage().contains("Socket closed")) {
                    log.warn("Server is closed");
                } else {
                    log.error("Socket error", se);
                }
                connectionList.clear();
            } catch (IOException ioe) {
                log.error("Error while reading Sockets", ioe);
                break;
            }
        }
    }
    
    public void attendClient (Socket sock) throws IOException {
        try (
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),
                                                                               StandardCharsets.UTF_8
                ));
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()))
        ) {
            ConnectionWrapper connection = new ConnectionWrapper(sock, in, out);
            if (sock == ownerSocket) {
                log.info("Game Owner attached");
                this.ownerWrapper = connection;
            }
            this.connectionList.offer(connection);
            log.info("Attending new client: {}", sock);
            String inputCmd;
            String author = (sock == ownerSocket) ? "Owner" : "Client";
            try {
                while ((inputCmd = in.readLine()) != null) {
                    log.debug("[{}] Received command: {}", author, inputCmd);
                    if (inputCmd.equals("disconnect"))
                        break;
                    if (sock == ownerSocket) {
                        handlePrivilegedCommand(inputCmd, connection);
                    } else {
                        handleCommand(inputCmd, connection);
                    }
                    if (inputCmd.equals("shutdown") && sock == ownerSocket) {
                        sock.close();
                        log.warn("Owner is shutdown server...");
                        shutdown();
                    }
                }
            } catch (SocketException se) {
                log.error(String.format("Socket closed %s", sock));
            }
            if (sock == ownerSocket) {
                ownerSocket = null;// No new owner
                ownerWrapper = null;
            }
            sock.close();
            this.connectionList.pollLast();
        }
        log.info("Connection Closed.");
    }
    
    private void handlePrivilegedCommand (String cmd, ConnectionWrapper sender) throws IOException {
        switch (cmd) {
            case "shutdown", "halt" -> shutdown();
            case "start" -> this.connectionList.forEach(connection -> {
                if (!connection.equals(sender))
                    try {
                        connection.writeLine("init");
                    } catch (IOException e) {
                        log.error("Error sending 'init' command", e);
                    }
            });
            case "reset" -> {
                log.info("Resetting games to clients");
                for (ConnectionWrapper connection : this.connectionList)
                    if (connection != sender)
                        connection.writeLine("reset-game");
            }
            case "stop" -> {
                for (ConnectionWrapper conn : this.connectionList)
                    // use `$` Symbol due is not allowed on a game setup
                    // the `$` act like a super directive, that cannot be
                    // ignored
                    if (!conn.equals(ownerWrapper))
                        conn.writeLine("$force-close$");
            }
            case "notify-winner" -> log.error("Not supported");
            case "setup" -> {
                this.targetString = sender.getIn().readLine();
                log.info("Setting up string as: {}", targetString);
            }
            default -> handleCommand(cmd, sender);
        }
        
    }
    
    private void handleCommand (String cmd, ConnectionWrapper sender) throws IOException {
        int port = sender.getSocket().getPort();
        switch (cmd) {
            case "register" -> {
                ownerWrapper.writeLine("new-client");
                String info = String.format("name: %s, id: %d", sender.getIn().readLine(), port);
                ownerWrapper.writeLine(info);
                OutputStream os = sender.getSocket().getOutputStream();
                images.forEach((key, image) -> {
                    try {
                        sender.writeLine(key);
                        sender.writeLine(String.format("size:%d", image.length));
                        String responseD = sender.getIn().readLine();
                        log.debug("requested: {}", responseD);
                        String[] response = responseD.split(":");
                        int byteBlock = Integer.parseInt(response[1]);
                        if (response[0].equals("send")) {
                            ByteArrayInputStream is = new ByteArrayInputStream(image);
                            byte[] buffer = new byte[byteBlock];
                            int bread;
                            while ((bread = is.read(buffer, 0, byteBlock)) > 0) {
                                sender.writeLine(String.valueOf(bread));
                                os.write(buffer, 0, bread);
                                log.debug("Sending {} bytes to socket", bread);
                            }
                            log.info("All file sent for key {}", key);
                        }
                    } catch (IOException ioe) {
                        log.error("Cannot send the images to the client", ioe);
                    }
                });
                sender.writeLine("end-images");
                log.info("All images sended to client");
            }
            case "notify-damage" -> {
                ownerWrapper.writeLine("damage");
                ownerWrapper.writeLine("id: " + port);
            }
            case "unattached" -> {
                ownerWrapper.writeLine("remove");
                ownerWrapper.writeLine("id: " + port);
            }
            case "winner" -> {
                ownerWrapper.writeLine("winner-detected");
                ownerWrapper.writeLine("id: " + port);
            }
            case "fetch-word" -> {
                // Wait until the target string is set by the owner
                log.debug("Client requested word: {}", targetString);
                //noinspection StatementWithEmptyBody
                while (this.ownerSocket != null && this.targetString == null) ;
                log.debug("Target String unlocked");
                sender.writeLine(this.targetString);
                log.info("word sent to client [{}]", this.targetString);
            }
            default -> log.warn("undefined-command [{}]", cmd);
        }
    }
    
    @Deprecated
    private void ownerSocketChange (Observable o, Socket old, Socket newState) {
        if (newState == null) {
            this.targetString = null;
            for (ConnectionWrapper conn : this.connectionList) {
                try {
                    conn.writeLine("stop");
                    conn.getSocket().close();
                } catch (IOException ioe) {
                    log.error("Cannot close connection: " + ioe.getMessage());
                }
            }
            this.ownerSocket = null;
            this.ownerWrapper = null;
            this.connectionList.clear();
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
    @SuppressWarnings ("all")
    public boolean shutdown () {
        if (this.socket == null)
            return false;
        try {
            forceDisconnection();
            socket.close();
            lookupSocket.close();
            tpool.shutdown();
            tpool.awaitTermination(1, TimeUnit.SECONDS);
            tpool.shutdownNow();
            isRunning = false;
        } catch (IOException | InterruptedException se) {
            log.error("Error while trying to close socket", se);
        }
        return socket.isClosed();
    }
    
    private void forceDisconnection () {
        while (!this.connectionList.isEmpty())
            try {
                this.connectionList.poll().getSocket().close();
            } catch (IOException ioe) {
                log.error("Error while trying to disconnect client", ioe);
            }
    }
    
    public boolean release () {
        return false;
    }
    
    /**
     * This function would reload the resources of the current session.
     *
     * @return true if has no problem with the resources
     */
    @SuppressWarnings ("UnusedReturnValue")
    public boolean reload () {
        try {
            if (isRunning) {
                log.info("Forced Reloading resources");
                images.clear();
                throw new IOException();
            } else
                log.info("Reloading resources.");
            
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }
    
    /**
     * @return true if the server is running.
     */
    @SuppressWarnings ("unused")
    public boolean isRunning () {
        return isRunning;
    }
}
