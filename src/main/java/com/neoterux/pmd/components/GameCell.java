package com.neoterux.pmd.components;

import com.neoterux.pmd.model.Game;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 * <h1>GameCell</h1>
 * <p>
 * This class represents a cell for a game,
 * only is a template to how visualize the data of a
 * given {@link com.neoterux.pmd.model.Game}
 * </p>
 */
public final class GameCell extends ListCell<Game> {
    
    @Override
    protected void updateItem (Game item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (item == null) {
            setGraphic(null);
        } else
            setGraphic(createView(item));
    }
    
    private VBox createView (Game game) {
        // TODO: Maybe change by a fxml and set as controller this
        final VBox root = new VBox(5);
        final Circle state = new Circle(5);
        Label roomName = new Label(game.getName());
        BorderPane ctn = new BorderPane();
        root.setFillWidth(true);
        Label htn = new Label("ip: " + game.getHostname());
        roomName.setStyle("-fx-text-fill: black;" +
                          "-fx-font-size: 15px;");
        htn.setStyle("-fx-text-fill: #909090;" +
                     "-fx-font-size: 12px;");
        state.fillProperty().set(game.isOnline() ? Color.GREEN : Color.CRIMSON);
        ctn.setLeft(htn);
        ctn.setRight(state);
        root.getChildren().addAll(roomName, ctn);
        return root;
    }
    
}
