package main;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
        this.updateGUI(this._stateManager.getSuccessors(this.getTurn()));
    }

    public void updateState(ArrayList<PieceState> newState, boolean isTurnEnd, ArrayList<Move> successors) {
        this._stateManager.setState(newState);

        if (isTurnEnd) {
            this.endTurn();
        }

        if (successors == null) {
            this.updateGUI(this._stateManager.getSuccessors(this.getTurn()));
        } else {
            this.updateGUI(successors);
        }
    }

    public void updateGUI(ArrayList<Move> successors) {
        this._gui.render(this._stateManager.getState(), successors, this);
    }

    public void onSemiOptionClick(MouseEvent event, ArrayList<PieceState> newState, ArrayList<Move> restrictedMoves) {
        this.updateState(newState, false, restrictedMoves);
    }

    public void onOptionClick(MouseEvent event, String message, ArrayList<PieceState> newState) {
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

        this.updateState(newState, true, null);
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
