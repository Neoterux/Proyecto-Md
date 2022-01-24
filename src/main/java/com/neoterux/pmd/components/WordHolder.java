package com.neoterux.pmd.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * @param <G>
 */
public abstract sealed class WordHolder<G extends Node> extends VBox permits ImageHolder, TextHolder,
                                                                             WordHolder.EmptyHolder {
    
    private final Rectangle wordSub;
    
    private final ObjectProperty<Paint> subColor;
    
    private final G graphic;
    
    private final StringProperty wordAssignProperty;
    
    protected WordHolder (G graphic) {
        this(graphic, "word-holder");
    }
    
    protected WordHolder (G graphic, String cssclass) {
        getStyleClass().add(cssclass);
        this.wordAssignProperty = new SimpleStringProperty();
        this.graphic = graphic;
        subColor = new SimpleObjectProperty<>(Consts.RECT_COLOR);
        this.wordSub = new Rectangle(Consts.WIDTH, Consts.RECT_HEIGHT);
        this.wordSub.fillProperty().bind(subColor);
        this.wordSub.strokeProperty().bind(subColor);
        getChildren().addAll(graphic, wordSub);
        configure();
        setPadding(new Insets(5));
        setAlignment(Pos.CENTER);
        
    }
    
    public static WordHolder emptyWordHolder () {
        return new EmptyHolder();
    }
    
    public final G getGraphic () {
        return this.graphic;
    }
    
    public final Rectangle getWordSub () {
        return this.wordSub;
    }
    
    public final StringProperty getWordAssignProperty () {
        return this.wordAssignProperty;
    }
    
    public final ObjectProperty<Paint> getSubColorProperty () {
        return this.subColor;
    }
    
    /**
     * This function would configure the holder
     */
    protected abstract void configure ();
    
    public interface Consts {
        /**
         * The width of the {@link WordHolder}
         */
        double WIDTH = 50; //px
        double MIN_HEIGHT = 50; //px
        double RECT_HEIGHT = 10; // px
        Paint RECT_COLOR = Color.BLACK;
        Paint FALLBACK_COLOR = Color.TRANSPARENT;
    }
    
    
    protected static non-sealed class EmptyHolder extends WordHolder<Pane> {
        protected EmptyHolder () {
            super(new Pane(), "empty-holder");
        }
        
        @Override
        protected void configure () {
            getSubColorProperty().set(Color.TRANSPARENT);
        }
    }
}
