package com.neoterux.pmd;

import com.neoterux.pmd.controllers.MainController;
import com.neoterux.pmd.controllers.OwnerWindowController;
import com.neoterux.server.api.ServerManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import static com.neoterux.pmd.utils.JfxUtils.getLoaderOf;

public class MainApp extends Application {
    
    /**
     * The root window of the Application
     */
    private static Stage rootStage;
    
    public static void main (String[] args) {
        launch(args);
    }
    
    /**
     * Gets the root Stage of teh Javafx Application
     *
     * @return The window
     */
    public static Stage getRootStage () {
        return rootStage;
    }
    
    @Override
    public void start (Stage primaryStage) {
        rootStage = primaryStage;
        try {
            rootStage.getIcons().add(new Image(getClass().getResource("icon/icon_32.png").toExternalForm()));
            rootStage.setTitle("Bienvenido al Juego");
            var loader = getLoaderOf("main_window.fxml");
            loader.setControllerFactory(param -> new MainController(primaryStage));
            Scene mainScene = new Scene(loader.load());
            primaryStage.setScene(mainScene);
            primaryStage.show();
        } catch (NullPointerException | IOException npe) {
            System.out.println("This should not execute!!");
            npe.printStackTrace();
        } catch (Exception e ){
            System.out.println("Unknown exception");
            e.printStackTrace();
        }
        System.out.println("Start method end:");
    }
    
    @Override
    public void stop () {
        System.out.println("Closing application");
        OwnerWindowController.workers.shutdownNow();
        ServerManager.closeInstances();
        System.out.println("Threads closed");
        Platform.exit();
        System.exit(0);
    }
}
