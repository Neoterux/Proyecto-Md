package com.neoterux.pmd.controllers;

import com.neoterux.pmd.components.TextHolder;
import com.neoterux.pmd.components.WordHolder;
import com.neoterux.server.api.ServerManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class OwnerWindowController implements Initializable {
    
    public static final ThreadPoolExecutor workers = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private static final Logger log = LoggerFactory.getLogger(OwnerWindowController.class);
    private final ServerManager manager = ServerManager.getInstance();
    
    @FXML
    private Circle statusCircle;
    @FXML
    private Label lblPort;
    @FXML
    private Label lblHost;
    @FXML
    private GridPane keyboard;
    @FXML
    private FlowPane wordContainer;
    @FXML
    private ScrollPane pStatusContainer;
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
    
        
    }
    
    public void onWindowLoaded (WindowEvent event) {
        Stage s = (Stage) event.getSource();
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Creando Juego", ButtonType.CANCEL);
        a.initOwner(s);
        a.setTitle("Creando juego");
        a.setHeaderText("Esperando Jugadores");
        ProgressIndicator p = new ProgressIndicator();
        p.setMaxHeight(30);
        a.setGraphic(p);
        workers.execute(() -> {
            configureServer(a);
        });
        Optional<ButtonType> result = a.showAndWait();
        //result.ifPresent(buttonType -> cancelWorkers());
    }
    
    
    public void configureServer (Alert waitAlert) {
        try {
            manager.forceReload();
//            Thread.sleep(1000);
            Platform.runLater(() -> {
                lblHost.setText("127.0.0.1");
                lblPort.setText(String.valueOf(manager.getPort()));
                waitAlert.close();
                statusCircle.setFill(Color.GREEN);
            });
        } catch (RuntimeException ie) {
            log.error("Configure server was interrupted abruptly", ie);
        }
    }
    
    public synchronized void cancelWorkers () {
        if (workers.getActiveCount() != 0) {
            workers.getQueue().forEach(workers::remove);
        }
    }
    
    @FXML
    public void onKeyActioned (ActionEvent actionEvent) {
        Button keybtn = (Button) actionEvent.getSource();
        String str = keybtn.getText();
        System.out.println("Key clicked: " + str);
        String id = keybtn.getId();
        
        switch (str) {
            case "Espacio":
                addSpace();
                break;
            case "<-":
                delLastWord();
                break;
            default:
                wordContainer.getChildren().add(new TextHolder(str));
                System.out.println("Added");
                System.out.println(wordContainer.getChildren());
        }
    }
    
    private void delLastWord () {
        if (!wordContainer.getChildren().isEmpty())
            wordContainer.getChildren().remove(wordContainer.getChildren().size() - 1);
    }
    
    private void addSpace () {
        wordContainer.getChildren().add(WordHolder.emptyWordHolder());
    }
    
    public void delKeyDetect (KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        switch (keyEvent.getCode()) {
            case BACK_SPACE -> delLastWord();
            case SPACE -> addSpace();
            default -> keyboard.getChildren().stream()
                               .filter(n -> n instanceof Button
                                            && ((Button) n).getText().equalsIgnoreCase(code.toString()))
                               .findFirst()
                               .map(it -> (Button) it)
                               .ifPresent(Button::fire);
        }
    }
}
