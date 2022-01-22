package com.neoterux.pmd.controllers;

import com.neoterux.pmd.utils.JfxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public final class MainController implements Initializable {
    
    private static Stage selectionWindow;
    @FXML
    private Parent root;
    
    public MainController (Stage window) {
        Alert a = new Alert(Alert.AlertType.ERROR, "Error while loading fxml");
        a.setTitle("Debug Error");
        try {
            Parent sparent = JfxUtils.loadFxml("server_selection_window");
            Scene selectionScene = new Scene(sparent);
            selectionWindow = new Stage();
            selectionWindow.setScene(selectionScene);
            selectionWindow.initOwner(window);
            selectionWindow.initModality(Modality.WINDOW_MODAL);
            selectionWindow.setAlwaysOnTop(true);
        } catch (IOException | NullPointerException ioe) {
            ioe.printStackTrace();
            a.setGraphic(new TextArea(ioe.getMessage()));
            a.show();
        }
    }
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
    }
    
    @FXML
    void joinGame (ActionEvent event) throws IOException, NullPointerException {
        Stage rootWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(JfxUtils.loadFxml("owner_window"));
        Stage nwindow = new Stage();
        nwindow.setScene(scene);
        nwindow.initOwner(rootWindow);
        nwindow.initModality(Modality.WINDOW_MODAL);
        nwindow.show();
    }

    @FXML
    void newGame(ActionEvent event) {
        selectionWindow.show();
    }
    
}
