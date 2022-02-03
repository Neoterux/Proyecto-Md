package com.neoterux.pmd.controllers;

import com.neoterux.pmd.MainApp;
import com.neoterux.pmd.utils.Controller;
import com.neoterux.pmd.utils.FXMLStageBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public final class MainController extends Controller {
    
    private static Stage selectionWindow;
    
    @Override
    protected void afterInstanciate () {
        if(selectionWindow != null) return;
        selectionWindow = new FXMLStageBuilder(MainApp.class, "server_selection_window.fxml")
                .setMinSize(300, 200)
                .setMaxSize(300, 275)
                .setStageConfigurator(stage -> {
                    stage.initOwner(this.window);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setAlwaysOnTop(true);
                })
                .build();
    }
    
    
    @FXML
    void newGame (ActionEvent event) throws IOException, NullPointerException {
        this.window.hide();
        new FXMLStageBuilder(MainApp.class, "owner_window.fxml")
                .setStageConfigurator(stage -> {
                    stage.initOwner(this.window);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setMaxHeight(500);
                })
                .setPrefSize(900, 475)
                .setMinSize(850, 475)
                .build()
                .showAndWait();
        this.window.show();
    }
    
    @FXML
    void joinGame (ActionEvent event) {
        selectionWindow.show();
    }
    
}
