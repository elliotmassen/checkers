package main;

import java.util.ArrayList;
import java.util.Stack;

public class Controller {
    private GUI _gui;
    private StateManager _stateManager;
    private Stack<State> _history;

    public enum Type {
        RED,
        BLACK,
        INFO
    }

    public Controller(GUI gui, StateManager stateManager) {
        this._gui = gui;
        this._stateManager = stateManager;
        this._history = new Stack<State>();
    }

    public void setup() {
        State initialState = StateManager.createInitialState();
        this._gui.setup(initialState, this);
        this._addToHistory(initialState);
        this.updateState(initialState, null, false);
    }

    public void updateState(State newState, ArrayList<Move> successors, boolean shouldAddToHistory) {
        State previousState = this._stateManager.getState();
        this._stateManager.setState(newState);

        if(shouldAddToHistory) {
            this._addToHistory(this._stateManager.getState());
            this._gui.endOfTurn(this._stateManager.getState(), previousState, this);
        }

        if(successors == null) {
            successors = this._stateManager.getSuccessors();
        }

        if(this._stateManager.isGoalState(!this._stateManager.getState().getTurn(), successors)) {
            this._gui.gameOver(this._stateManager.getState(), previousState, this);
        }

        this._updateGUI(successors);
    }

    private void _addToHistory(State state) {
        this._history.add(state);
    }

    public void undoStateTo(State state) {
        boolean removed = false;
        while(!removed) {
            State popped = this._history.pop();
            removed = popped == state;
            System.out.println(popped + ", " + state);
        }

        this.updateState(this._history.peek(), null, false);
    }

    private void _updateGUI(ArrayList<Move> successors) {
        this._gui.render(this._stateManager.getState(), successors, this);
    }
}
