package com.neoterux.pmd;

import com.neoterux.pmd.controllers.OwnerWindowController;
import com.neoterux.pmd.utils.FXMLStageBuilder;
import com.neoterux.pmd.utils.exceptions.StageBuildException;
import com.neoterux.server.api.ServerManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

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
            new FXMLStageBuilder(getClass(), "main_window.fxml")
                    .setStageConfigurator(stage -> {
                        stage.setTitle("Bienvenido al juego");
                        System.out.println("This is executing?");
                    })
                    .applyTo(primaryStage);
            primaryStage.show();
        }catch (StageBuildException sbe) {
            System.err.println("Error while building Stage");
            sbe.printStackTrace();
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
