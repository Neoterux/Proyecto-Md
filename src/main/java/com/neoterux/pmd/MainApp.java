package com.neoterux.pmd;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.neoterux.pmd.utils.JfxUtils.loadFxml;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene mainScene = new Scene(loadFxml("main_window"));
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
