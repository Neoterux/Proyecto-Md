package com.neoterux.pmd;

import com.neoterux.pmd.controllers.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.neoterux.pmd.utils.JfxUtils.getLoaderOf;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            var loader = getLoaderOf("main_window.fxml");
            loader.setControllerFactory(param -> new MainController(primaryStage));
            //Scene mainScene = new Scene(loadFxml("main_window"));
            Scene mainScene = new Scene(loader.load());
            primaryStage.setScene(mainScene);
            primaryStage.show();
        } catch (NullPointerException npe) {
        
        }
    }
}
