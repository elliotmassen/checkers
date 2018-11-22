package main;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Controller {
    private GUI _gui;
    private StateManager _stateManager;

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
    }

    public void setup() {
        this._gui.setup();
        this._gui.render(this._stateManager.getState(), this._stateManager.getSuccessors(), this);
    }

    public void onPieceClick(MouseEvent event, String historyMessage) {
        HBox item = this._gui.createHistoryItem(Controller.Type.INFO, historyMessage);
        this._gui.history.getChildren().add(item);
        item.toBack();

        ((Circle) event.getTarget()).setFill(Color.rgb(255, 255, 255));
    }
}
