package com.neoterux.server.api;

import com.neoterux.server.api.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class ServerManager implements Runnable {

    /**
     * The looger of this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);
    /**
     * Image permitted extension
     */
    private static final String[] IMG_EXTS = {"png", "jpg", "jpeg", "webp", "gif"};
    /**
     * The path of the images
     */
    private static final String IMAGE_PATH = "images";
    /**
     * The server manager that handle
     */
    private volatile static ServerManager manager;
    /**
     * This map would contains the images bytes.
     * Always would contain 24 images due is one image for each letter
     */
    private final Map<String, byte[]> keyMap = new HashMap<>();
    /**
     * The client server that handles the new game
     */
    private Server attachedServer;
    private Socket connection;

    /**
     * Get the instance of the server Manager.
     *
     * @return the server manager of the client
     */
    public static ServerManager getInstance() {
        if (manager == null) {
            logger.info("Server manager is initalizing...");
            manager = new ServerManager();
            manager.initalize();
        }
        return manager;
    }


    /**
     * Would
     */
    void initalize() {
        loadImages(true);
        logger.info("");
    }

    /**
     * This method would reload all the images without lookup for running servers
     *
     * @return true if nothing failed
     */
    public boolean forceReload() {
        try {
            loadImages(false);
        } catch (RuntimeException rte) { // Catch any exception
            return false;
        }
        return true;
    }

    void loadImages(boolean lookup) {
        if (lookup && lookupForRunningServers())
            return;
        File imgPath = Paths.get(IMAGE_PATH).toFile();
        if (!imgPath.exists() && !imgPath.mkdir()) // this is to save code lines
            logger.error("Cannot create the image path.");
        File[] tfiles = imgPath.listFiles();
        if (tfiles == null) {
            logger.info("No images found");
            return;
        }
        for (File file : tfiles)
            if (FileUtils.hasExtension(file, IMG_EXTS))
                try {
                    String a = file.getName().split("\\.")[0].toLowerCase();
                    keyMap.put(a, Files.readAllBytes(file.toPath()));
                } catch (IOException ioe) {
                    logger.error("Error while reading file " + file.getName() + ": ", ioe);
                }
    }

    boolean lookupForRunningServers() {
        return false;
    }


    boolean joinToServer() {

        return false;
    }


    @Override
    public void run() {

    }
}
