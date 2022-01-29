package com.neoterux.pmd.controllers;

import com.neoterux.pmd.components.ImageHolder;
import com.neoterux.pmd.components.PlayerLifeContainer;
import com.neoterux.pmd.components.TextHolder;
import com.neoterux.pmd.components.WordHolder;
import com.neoterux.server.api.Client;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class GameWindowController implements Runnable {
    
    private static final Logger log = LoggerFactory.getLogger(GameWindowController.class);
    
    private final Client client;
    private final ObservableMap<String, Image> images;
    private final StringProperty name;
    private final StringProperty wordProperty;
    private final Thread decoder;
    private final List<String> insertedLetter;
    @FXML
    private HBox lifeBox;
    @FXML
    private HBox wordContainer;
    @FXML
    private HBox statusBar;
    private PlayerLifeContainer lifeContainer;
    @FXML
    private GridPane keyboard;
    
    public GameWindowController (Client client) {
        this.images = FXCollections.observableHashMap();
        this.insertedLetter = new ArrayList<>();
        this.wordProperty = new SimpleStringProperty("");
        this.client = client;
        this.name = new SimpleStringProperty("NO NAME");
        this.decoder = new Thread(this, "Decoder-Client");
    }
    
    
    @FXML
    public void keyClicked (ActionEvent actionEvent) {
        Button key = (Button) actionEvent.getSource();
        String letter = key.getText();
        if (insertedLetter.contains(letter))
            return;
        if (wordProperty.get().contains(letter))
            completeWord(letter);
        else {
            
            if (this.lifeContainer.makeDamage())
                log.debug("Damage taken!!!");
//                onLooseGame();
        }
        if (wordContainer.getChildren().stream()
                         .noneMatch(it -> it instanceof TextHolder &&
                                          ((TextHolder) it).getAssignedWord().equals(" "))) {
            notifyWinner();
        }
    }
    
    public void completeWord (String letter) {
        log.debug("Completing word: {}", letter);
        this.insertedLetter.add(letter);
        String word = this.wordProperty.get();
        for (int i = 0; i < word.length(); i++)
            if (word.charAt(i) == letter.charAt(0))
                wordContainer.getChildren().set(i, genImageHolder(letter, true));
    }
    
    @SuppressWarnings ("unchecked")
    private <E extends Node> WordHolder<E> genImageHolder (String letter, boolean test) {
        if (test) {
            return (WordHolder<E>) new TextHolder(letter);
        } else {
            return (WordHolder<E>) new ImageHolder(images.get(letter));
        }
    }
    
    public void onLooseGame () {
        this.keyboard.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Has perdido.");
        alert.setTitle("Notificación");
        alert.setHeaderText("Lo sentimos, has perdido ;(");
        alert.show();
    }
    
    public void scramble () {
        String word = this.wordProperty.get();
        final int maxWords = (int) Math.ceil(0.3 * word.length());
        List<Character> s = new ArrayList<>(word.chars()
                                                .mapToObj(c -> (char) c).toList());
        int modcount = 0;
        Random rd = new Random();
        while ((maxWords + modcount) < s.size()) {
            char c;
            int index;
            do {
                index = rd.nextInt(s.size());
                c = s.get(index);
            } while (c == ' ' || c == '$');
            s.set(index, '$');
            modcount++;
        }
        log.debug("String to modify: {}", String.join("", s.stream().map(Object::toString).toList()));
        for (int i = 0; i < word.length(); i++) {
            String a = s.get(i).toString();
            Node n;
            if (a.equals(" "))
                n = WordHolder.emptyWordHolder();
            else if (word.contains(a))
                n = genImageHolder(a, true);
            else
                n = new TextHolder(" ");
            this.wordContainer.getChildren().add(n);
        }
    }
    
    public void notifyWinner () {
        client.sendCommand("winner");
        keyboard.setDisable(true);
    }
    
    @Override
    public void run () {
        log.info("Starting decoder thread for player");
        String inputCommand;
        try {
            while ((inputCommand = client.awaitCommand()) != null) {
                switch (inputCommand) {
                    case "reset" -> Platform.runLater(this::resetGame);
                    case "stop" -> {
                        Alert a = new Alert(Alert.AlertType.WARNING, "Juego cancelado por administrador");
                        a.setTitle("Juego Cancelado");
                        a.setHeaderText("Juego Cancelado");
                        Platform.runLater(() -> {
                            Stage window = ((Stage) keyboard.getScene().getWindow());
                            a.initOwner(window);
                            a.showAndWait();
                            window.close();
                        });
                    }
                }
            }
        } catch (IOException ioe) {
            log.error("Error while parsing command", ioe);
        }
    }
    
    private void resetGame () {
    
    }
    
    public void onDamage (Observable o, Number ol, Number n) {
        int old = ol.intValue();
        int newValue = n.intValue();
        if (newValue == 0) {
            onLooseGame();
            log.info("The game has loose");
        }
        if (newValue - old < 0) { // This means that loose life
            this.client.sendCommand("notify-damage");
        }
    }
    
    public void onWindowShowed (WindowEvent event) {
        keyboard.setDisable(true);
        Stage window = (Stage) event.getSource();
        window.titleProperty().bindBidirectional(name);
        Dialog<String> nameDialog = new Dialog<>();
        client.setOnForceQuitListener(() -> {
            nameDialog.setResult("");
            nameDialog.close();
            closeAll();
        });
        nameDialog.setTitle("Ingrese nombre de Jugador");
        nameDialog.initOwner(window);
        ButtonType acceptBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        nameDialog.getDialogPane().getButtonTypes().add(acceptBtn);
        nameDialog.getDialogPane().setMinSize(300, 100);
        VBox c = new VBox();
        c.setFillWidth(true);
        c.setAlignment(Pos.CENTER_RIGHT);
        TextField nameField = new TextField();
        c.getChildren().addAll(new Label("Nombre del Jugador:"), nameField);
        nameDialog.getDialogPane().setContent(c);
        nameDialog.setResultConverter(buttonType -> (buttonType == acceptBtn) ? nameField.getText() : "");
        String name = "";
        while (name.isBlank()) {
            Optional<String> o = nameDialog.showAndWait();
            if (o.isPresent())
                name = o.get();
        }
        this.name.setValue(name);
        log.info("Registering new player");
        client.sendCommand("register");
        client.sendCommand(name);
        parseImages();
        this.lifeContainer = new PlayerLifeContainer(this.name.get());
        this.lifeContainer.lifeCountProperty().addListener(this::onDamage);
        log.info("Fetching the word from server");
        this.wordProperty.setValue(fetchWord());
        this.lifeBox.getChildren().add(lifeContainer.getLifeContainer());
        log.debug("Word fetched from admin: '{}'", wordProperty.get());
        decoder.start();
        scramble();
        keyboard.setDisable(false);
    }
    
    public void parseImages () {
        String input;
        log.info("Starting Receiving Images");
        try {
            while ((input = client.awaitCommand()) != null && !input.equalsIgnoreCase("end-images")) {
                final int byteCount = Integer.parseInt(client.awaitCommand().split(":")[1]);
                client.sendCommand("send:10240");// Send in block of 10Kb
                int bytesReceived = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream(byteCount);
                while (bytesReceived < byteCount) {
                    int byterx = client.awaitInt();
                    log.debug("Server send: {}", byterx);
                    bos.write(client.getBytes(byterx));
                    bytesReceived += byterx;
                    log.debug("Receiving bytes {}", bytesReceived);
                }
                log.info("Readed all image bytes");
                this.images.put(input, new Image(new ByteArrayInputStream(bos.toByteArray())));
                bos.close();
            }
        } catch (IOException ioe) {
            log.error("An error ocurred while parsing images", ioe);
            return;
        }
        log.info("Finish parsing images");
        configButtons();
    }
    
    public void configButtons () {
        log.info("Configuring buttons");
        List<Button> btnlist = this.keyboard.getChildren().stream().map(it -> (Button) it).toList();
        for (Button btn : btnlist) {
            String key = btn.getText();
            Image img = this.images.getOrDefault(key, null);
            if (img != null) {
                ImageView iv = new ImageView(img);
                iv.setPreserveRatio(true);
                iv.setFitWidth(btn.getWidth());
                btn.setGraphic(iv);
            }
        }
    }
    
    
    public String fetchWord () {
        String result = null;
        client.sendCommand("fetch-word");
        try {
            log.debug("Consulting to server the word");
            result = client.awaitCommand();
            log.debug("game fetched the next string from server: {}", result);
            statusBar.setVisible(false);
        } catch (IOException ioe) {
            log.error("Cannot get the word", ioe);
        }
        return result;
    }
    
    
    public void onKeyActioned (ActionEvent actionEvent) {
    }
    
    private void closeAll () {
        Platform.runLater(() -> {
            ((Stage) this.keyboard.getScene().getWindow()).close();
            this.client.close();
            this.decoder.interrupt();
        });
    }
    
    public void onWindowCloses (WindowEvent windowEvent) {
        closeAll();
        log.warn("Forcefull closing window");
    }
}
