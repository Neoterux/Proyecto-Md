package com.neoterux.pmd.controllers;

import com.neoterux.pmd.MainApp;
import com.neoterux.pmd.components.GameCell;
import com.neoterux.pmd.model.Game;
import com.neoterux.pmd.utils.JfxUtils;
import com.neoterux.server.api.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServerSelectionController implements Initializable {
    
    private static final Logger log = LoggerFactory.getLogger(ServerSelectionController.class);
    @FXML
    private ListView<Game> serverContainer;
    private ObservableList<Game> rooms;
    
    private void join (Client client) {
        Stage window = new Stage();
        Stage root = (Stage) serverContainer.getScene().getWindow();
        try {
            root.close();
            window.initModality(Modality.WINDOW_MODAL);
            window.initOwner(MainApp.getRootStage());
            FXMLLoader loader = JfxUtils.getLoaderOf("game_window.fxml");
            GameWindowController controller = new GameWindowController(client);
            loader.setControllerFactory(param -> controller);
            Scene sce = new Scene(loader.load());
            window.setOnHidden(controller::onWindowCloses);
            window.setOnShown(controller::onWindowShowed);
            window.setScene(sce);
            root.getOwner().hide();
            window.showAndWait();
            ((Stage) root.getOwner()).show();
        } catch (IOException ioe) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Cannot create new game, sorry");
            a.initOwner(root);
            a.show();
            log.error("Failed to create game window", ioe);
            root.show();
        }
        
    }
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        rooms = readServers();
        serverContainer.setCellFactory(new GameCell(this::onItemClick));
        serverContainer.setItems(rooms);
        
    }
    
    public void onItemClick (Game game) {
        log.info("Double click detected: {}", game);
        Client client;
        try {
            client = Client.connectTo(game.getHostname(), game.getPort());
        } catch (ConnectException ce) {
            new Alert(Alert.AlertType.ERROR, "Servidor no disponible.").show();
            return;
        } catch (IOException ioe) {
            log.error("IOException ocurred", ioe);
            return;
        }
        join(client);
        ((Stage) serverContainer.getScene().getWindow()).close();
    }
    
    @FXML
    public void addServer (ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = JfxUtils.getLoaderOf("add_game_window.fxml");
        Parent root = loader.load();
        ((AddGameController) loader.getController()).setOnGameAdded(game -> rooms.add(game));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.initOwner(serverContainer.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(false);
        stage.show();
    }
    
    @SuppressWarnings ("unchecked")
    private ObservableList<Game> readServers () {
        ObservableList<Game> list = FXCollections.observableArrayList();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("servers.dat"))) {
            list.addAll((List<Game>) ois.readObject());
        } catch (FileNotFoundException | ClassNotFoundException | InvalidClassException fnf) {
            return list;
        } catch (IOException ioe) {
            log.error("Unknown IOException ", ioe);
        }
        return list;
    }
    
    private void saveList () {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("servers.dat"))) {
            oos.writeObject(new ArrayList<>(rooms));
        } catch (IOException ioe) {
            log.error("Unknown IOException", ioe);
        }
    }
    
    @FXML
    public void close (ActionEvent actionEvent) {
        saveList();
        Stage s = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        s.close();
    }
}
