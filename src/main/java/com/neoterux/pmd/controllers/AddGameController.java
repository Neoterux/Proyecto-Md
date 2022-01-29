package com.neoterux.pmd.controllers;

import com.neoterux.pmd.model.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public final class AddGameController {
    
    private static final Alert errAlert = new Alert(Alert.AlertType.ERROR);
    
    static {
        errAlert.setTitle("Error al aplicar cambios");
        errAlert.setHeaderText("Error al Registrar");
        
    }
    
    /**
     * The textfield with the name of the registered server
     */
    @FXML
    public TextField txtName;
    /**
     * The textfield with the server hostname(ip)
     */
    @FXML
    public TextField txtHostname;
    /**
     * The textfield with the server port
     */
    @FXML
    public TextField txtPort;
    
    public Consumer<Game> onGameAdded;
    
    public void setOnGameAdded (Consumer<Game> onGameAdded) {
        this.onGameAdded = onGameAdded;
    }
    
    /**
     * This method would apply/return changes to the root window.
     *
     * @param actionEvent the action event.
     */
    public void accept (ActionEvent actionEvent) {
//        if (!errAlert.isShowing())
//            errAlert.initOwner(txtPort.getScene().getWindow());
        String name = txtName.getText();
        String hostname = txtHostname.getText();
        int port = -1;
        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException nfe) {
            errAlert.setContentText("Puerto inválido ingrese de 1 - 65535.");
            errAlert.show();
        }
        if (name.isBlank() || hostname.isBlank()) {
            errAlert.setContentText("Datos incompletos, por favor rellenelos.");
            errAlert.show();
        } else {
            Game game = new Game(name, hostname, port);
            if (onGameAdded != null) {
                onGameAdded.accept(game);
            }
            ((Stage) txtPort.getScene().getWindow()).close();
        }
    }
    
    /**
     * Closes the current window, cancelling every action
     *
     * @param actionEvent the action event
     */
    public void close (ActionEvent actionEvent) {
        ((Stage) txtPort.getScene().getWindow()).close();
    }
}
