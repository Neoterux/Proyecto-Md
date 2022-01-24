package com.neoterux.pmd.components;

import javafx.scene.control.Label;

public non-sealed class TextHolder extends WordHolder<Label> {
    
    public TextHolder (String letter) {
        this(new Label(letter));
    }
    
    protected TextHolder (Label graphic) {
        super(graphic, "text-holder");
    }
    
    @Override
    protected void configure () {
        
        getGraphic().setStyle("-fx-font-size: 30px;" +
                              "-fx-font-weight: bold;" +
                              "-fx-text-fill: black;");
    }
}
