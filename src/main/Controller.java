package main;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Controller {
    private GUI _gui;
    private StateManager _stateManager;
    private boolean _turn;

    public enum Type {
        RED,
        RED_KING,
        BLACK,
        BLACK_KING,
        INFO
    }

    public Controller(GUI gui, StateManager stateManager) {
        this._gui = gui;
        this._stateManager = stateManager;
        this._turn = true;
    }

    public boolean getTurn() {
        return this._turn;
    }

    public void endTurn() {
        this._turn = !this._turn;
    }

    public void setup() {
        this._gui.setup();
        this._gui.render(this._stateManager.getState(), this._stateManager.getSuccessors(this.getTurn()), this);
    }

    public void onOptionClick(MouseEvent event, String message) {
        Type type;
        if(this.getTurn()) {
            type = Type.BLACK;
        }
        else {
            type = Type.RED;
        }

        HBox item = this._gui.createHistoryItem(type, message);
        this._gui.history.getChildren().add(item);
        item.toBack();

        ((Circle) event.getTarget()).setFill(Color.rgb(255, 255, 255));
    }

    public void onPieceClick(MouseEvent event, int x, int y, ArrayList<Circle> options) {
        options.forEach((Circle c) -> { c.getStyleClass().remove("option--visible"); });

        // TODO: Is there a nice filter function available for this?
        options.forEach((Circle c) -> {
            if(c.getStyleClass().contains("" + x + y)) {
                c.getStyleClass().add("option--visible");
            }
        });
    }
}
