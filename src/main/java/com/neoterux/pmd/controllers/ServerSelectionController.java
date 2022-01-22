package com.neoterux.pmd.controllers;

import com.neoterux.pmd.components.GameCell;
import com.neoterux.pmd.model.Game;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

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
    
    public void addServer (ActionEvent actionEvent) {
    
    }
    
    public void close (ActionEvent actionEvent) {
        Stage s = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        s.close();
    }
}
