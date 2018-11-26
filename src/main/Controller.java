package main;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class Controller {
    private GUI _gui;
    private StateManager _stateManager;
    private boolean _turn;

    public enum Type {
        RED,
        BLACK,
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
        this._gui.setup(this._stateManager.getState(), this);
        this.updateGUI(this._stateManager.getSuccessors(this.getTurn()));
    }

    public void updateState(ArrayList<PieceState> newState, boolean isTurnEnd, ArrayList<Move> successors) {
        ArrayList<PieceState> previousState = this._stateManager.getState();
        this._stateManager.setState(newState);

        if (isTurnEnd) {
            this.endTurn();
        }

        if (successors == null) {
            successors = this._stateManager.getSuccessors(this.getTurn());
        }

        if(this._stateManager.isGoalState(!this.getTurn(), successors)) {
            Type type;
            if(!this.getTurn()) {
                type = Type.BLACK;
            }
            else {
                type = Type.RED;
            }

            HBox gameoverItem = this._gui.createHistoryItem(type, "won the game!", previousState, this);
            this._gui.history.getChildren().add(gameoverItem);
            gameoverItem.toBack();
        }

        this.updateGUI(successors);
    }

    public void updateGUI(ArrayList<Move> successors) {
        this._gui.render(this._stateManager.getState(), successors, this);
    }

    public void onSemiOptionClick(MouseEvent event, ArrayList<PieceState> newState, ArrayList<Move> restrictedMoves) {
        this.updateState(newState, false, restrictedMoves);
    }

    public void onOptionClick(MouseEvent event, String message, ArrayList<PieceState> state, ArrayList<PieceState> newState) {
        Type type;
        if(this.getTurn()) {
            type = Type.BLACK;
        }
        else {
            type = Type.RED;
        }

        HBox item = this._gui.createHistoryItem(type, message, state, this);
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

    public void onHistoryItemClick(MouseEvent event, ArrayList<PieceState> state) {
        if (state != null) {
            this.updateState(state, true, null);

            int i = 0;
            while(this._gui.history.getChildren().remove(i) != (HBox) ((Pane) event.getTarget()).getParent()) {}
        }
    }
}
