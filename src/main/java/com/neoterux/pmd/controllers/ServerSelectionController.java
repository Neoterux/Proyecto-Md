package com.neoterux.pmd.controllers;

import com.neoterux.pmd.components.GameCell;
import com.neoterux.pmd.model.Game;
import com.neoterux.pmd.utils.JfxUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerSelectionController implements Initializable {
    
    @FXML
    private ListView<Game> serverContainer;
    
    private ObservableList<Game> rooms;
    
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        rooms = FXCollections.observableArrayList();
        serverContainer.setCellFactory(l -> new GameCell());
        serverContainer.setItems(rooms);
        rooms.add(new Game("Test Game server", "255.255.255.255:65535"));
        System.out.println("Initialize the controller");
    }
    
    @FXML
    public void addServer (ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        Parent root = JfxUtils.loadFxml("add_game_window");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.initOwner(serverContainer.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(false);
        stage.show();
    }
    
    @FXML
    public void close (ActionEvent actionEvent) {
        Stage s = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        s.close();
    }
}
