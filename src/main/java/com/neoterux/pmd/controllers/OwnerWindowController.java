package com.neoterux.pmd.controllers;

import com.neoterux.pmd.components.TextHolder;
import com.neoterux.pmd.components.WordHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

public class OwnerWindowController {
    
    
    @FXML
    public FlowPane wordContainer;
    public StackPane pStatusContainer;
    
    @FXML
    public void onKeyActioned (ActionEvent actionEvent) {
        Button keybtn = (Button) actionEvent.getSource();
        String str = keybtn.getText();
        System.out.println("Key clicked: " + str);
        
        switch (str) {
            case "Espacio":
                wordContainer.getChildren().add(WordHolder.emptyWordHolder());
                break;
            case "<-":
                if (!wordContainer.getChildren().isEmpty())
                    wordContainer.getChildren().remove(wordContainer.getChildren().size() - 1);
                break;
            default:
                wordContainer.getChildren().add(new TextHolder(str));
                System.out.println("Added");
                System.out.println(wordContainer.getChildren());
//                lblWord.setText(lblWord.getText() + str);
        }
        
    }
}
