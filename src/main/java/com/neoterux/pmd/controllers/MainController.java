package com.neoterux.pmd.controllers;

import com.neoterux.pmd.utils.JfxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
            Scene selectionScene = new Scene(sparent, 300, 200);
            selectionWindow = new Stage();
            selectionWindow.setMinWidth(300);
            selectionWindow.setMinHeight(200.0);
            selectionWindow.setMaxWidth(300.0);
            selectionWindow.setMaxHeight(275.0);
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
    void newGame (ActionEvent event) throws IOException, NullPointerException {
        FXMLLoader loader = JfxUtils.getLoaderOf("owner_window.fxml");
        Stage rootWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(loader.load(), 900, 400);
        Stage nwindow = new Stage();
        OwnerWindowController controller = loader.getController();
        nwindow.setOnShown(controller::onWindowLoaded);
        nwindow.setScene(scene);
        nwindow.initOwner(rootWindow);
        nwindow.setOnCloseRequest(controller::onWindowClosed);
        nwindow.setMinWidth(850.0);
        nwindow.setHeight(475);
        nwindow.setMinHeight(475);
        nwindow.setMaxHeight(500);
        nwindow.initModality(Modality.WINDOW_MODAL);
        rootWindow.hide();
        nwindow.showAndWait();
        rootWindow.show();
    }
    
    @FXML
    void joinGame (ActionEvent event) {
        selectionWindow.show();
    }
    
}
