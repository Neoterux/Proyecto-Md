package com.neoterux.pmd.controllers;

import com.neoterux.pmd.model.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    
    /**
     * This method would apply/return changes to the root window.
     *
     * @param actionEvent the action event.
     */
    public void accept (ActionEvent actionEvent) {
        errAlert.initOwner(txtPort.getScene().getWindow());
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
            Game game = new Game(name, String.format("%s:%s", hostname, port));
            System.out.println(game);
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
