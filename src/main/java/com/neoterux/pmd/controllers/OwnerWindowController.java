package com.neoterux.pmd.controllers;

import com.neoterux.pmd.components.PlayerLifeContainer;
import com.neoterux.pmd.components.TextHolder;
import com.neoterux.pmd.components.WordHolder;
import com.neoterux.server.api.Client;
import com.neoterux.server.api.ServerManager;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class OwnerWindowController implements Initializable, Runnable {
    
    public static final ThreadPoolExecutor workers = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private static final Logger log = LoggerFactory.getLogger(OwnerWindowController.class);
    @SuppressWarnings ("deprecation")
    private final ServerManager manager = ServerManager.getInstance();
    /**
     * The list of letters that were inserted by the keyboard
     */
    private final Deque<String> word;
    private final ObservableList<Client> players;
    private final BooleanProperty gameStartedProperty;
    private final Thread decoderThread;
    private final Map<Integer, PlayerLifeContainer> playersMap;
    @FXML
    private Button startButton;
    @FXML
    private Button cancelButton;
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
    private VBox pStatusContainer;
    private Client client;
    private boolean gameStarted;
    
    
    public OwnerWindowController () {
        playersMap = new HashMap<>();
        gameStartedProperty = new SimpleBooleanProperty(false);
        players = FXCollections.observableArrayList();
        word = new LinkedList<>();
        gameStartedProperty.addListener(this::onGameStartedListener);
        decoderThread = new Thread(this, "Decoder-Owner");
    }
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        gameStarted = false;
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
        workers.execute(() -> configureServer(a));
        Optional<ButtonType> result = a.showAndWait();
        connect();
        decoderThread.start();
    }
    
    private void onGameStartedListener (Observable o, boolean old, boolean isStarting) {
//        client.sendCommand(isStarting ? "start" : "stop");
        if (isStarting) {
            client.sendCommand("setup");
            client.sendCommand(getWord().trim());
        }
    }
    
    
    public void configureServer (Alert waitAlert) {
        try {
            manager.initalize();
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
    
    @Override
    public void run () {
        String input;
        try {
            while ((input = client.awaitCommand()) != null) {
                switch (input) {
                    case "new-client" -> {
                        log.info("Registering new Player");
                        try {
                            String[] params = client.awaitCommand().split(",");
                            String name = params[0].split(":")[1].trim();
                            String id = params[1].split(":")[1].trim();
                            Platform.runLater(() -> {
                                PlayerLifeContainer player = new PlayerLifeContainer(name);
                                this.playersMap.put(Integer.parseInt(id), player);
                                this.pStatusContainer.getChildren().add(player);
                            });
                        } catch (IndexOutOfBoundsException iof) {
                            log.error("Invalid new-client command issued");
                        }
                    }
                    case "winner-detected" -> {
                        String id = client.awaitCommand().split(":")[1];
                        log.info("Winner detected");
                    }
                    case "damage" -> {
                        String id = client.awaitCommand().split(":")[1].trim();
                        PlayerLifeContainer life = this.playersMap.getOrDefault(Integer.parseInt(id), null);
                        if (life == null)
                            log.error("Unknown id provided for damaged");
                        else
                            life.makeDamage();
                    }
                    case "remove" -> {
                        String id = client.awaitCommand().split(":")[1].trim();
                        Platform.runLater(() -> {
                            PlayerLifeContainer c = playersMap.get(Integer.parseInt(id));
                            if (c != null) {
                                this.pStatusContainer.getChildren().remove(c);
                            } else {
                                log.error("This couldn't happen");
                            }
                        });
                        log.info("Disconnecting player with id: {}", id);
                    }
                }
            }
        } catch (IOException ioe) {
            log.error("Error while fetching commands", ioe);
            onWindowClosed(new WindowEvent(this.cancelButton.getScene().getWindow(),
                                           WindowEvent.WINDOW_CLOSE_REQUEST
            ));
        }
    }
    
    private String getWord () {
        return String.join("", word);
    }
    
    private void connect () {
//        manager.registerOwner(this::onClientConnected);
        this.client = manager.localClient();
    }
    
    public void onClientConnected (Client c) {
        players.add(c);
        TitledPane pane = new TitledPane();
        pane.setExpanded(true);
        pane.setText(c.getHostname());
        FlowPane fp = new FlowPane();
        pane.setContent(fp);
        pStatusContainer.getChildren().add(pane);
        
    }
    
    public void onClientResponse (Client c) {
        log.info("New connection detected {}", c);
        
    }
    
    public synchronized void cancelWorkers () {
        if (workers.getActiveCount() != 0) {
            workers.getQueue().forEach(workers::remove);
        }
    }
    
    @FXML
    public void onKeyActioned (ActionEvent actionEvent) {
        Button keyButton = (Button) actionEvent.getSource();
        String str = keyButton.getText();
        switch (str) {
            case "Espacio" -> addSpace();
            case "<-" -> delLastWord();
            default -> {
                wordContainer.getChildren().add(new TextHolder(str));
                word.offer(str);
            }
        }
        log.debug("Word formed: {}", getWord());
    }
    
    private void delLastWord () {
        if (!wordContainer.getChildren().isEmpty()) {
            wordContainer.getChildren().remove(wordContainer.getChildren().size() - 1);
            word.pollLast();
        }
    }
    
    private void addSpace () {
        wordContainer.getChildren().add(WordHolder.emptyWordHolder());
        word.offer(" ");
    }
    
    public void delKeyDetect (KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        if (keyEvent.getCharacter().equalsIgnoreCase(" ")) {
            addSpace();
            return;
        }
        log.debug("Detected keycode: {}", code);
        switch (keyEvent.getCode()) {
            case UNDEFINED -> keyEvent.consume();
            case BACK_SPACE -> delLastWord();
            default -> keyboard.getChildren().stream()
                               .filter(n -> n instanceof Button
                                            && ((Button) n).getText().equalsIgnoreCase(code.toString()))
                               .findFirst()
                               .map(it -> (Button) it)
                               .ifPresent(Button::fire);
        }
    }
    
    public void startGame (ActionEvent actionEvent) {
        if (word.stream().filter(it -> !it.equals(" ")).count() <= 2) {
            Alert sg = new Alert(Alert.AlertType.WARNING, "El juego debe tener mínimo 3 letras");
            sg.setTitle("No se puede empezar el juego");
            sg.show();
            return;
        }
        log.debug("Owner sending start game signal");
        this.gameStartedProperty.set(true);
        log.debug("socket status isconnected:{}", client.getConnectionSocket().isConnected());
        cancelButton.setDisable(false);
        startButton.setDisable(true);
        keyboard.setDisable(true);
        gameStarted = true;
        log.info("Game started by the Owner");
    }
    
    public void cancelGame (ActionEvent actionEvent) {
        Alert cg = new Alert(Alert.AlertType.WARNING, "Se va a parar el Juego, desea continuar?", ButtonType.YES,
                             ButtonType.NO
        );
        cg.setTitle("Desea cancelar el juego?");
        Optional<ButtonType> res = cg.showAndWait();
        res.ifPresent(buttonType -> {
            if (buttonType == ButtonType.YES) {
                this.gameStartedProperty.set(false);
                gameStarted = false;
                keyboard.setDisable(false);
                cancelButton.setDisable(true);
                startButton.setDisable(false);
            }
        });
    }
    
    private void closeAll () {
        client.sendCommand("shutdown");
        client.close();
        decoderThread.interrupt();
    }
    
    public void exit (ActionEvent actionEvent) {
        closeAll();
        ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
    }
    
    public void onWindowClosed (WindowEvent event) {
        closeAll();
    }
}
