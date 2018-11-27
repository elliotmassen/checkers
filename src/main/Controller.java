package main;

import java.util.ArrayList;
import java.util.EmptyStackException;
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

    public boolean canUndo() {
        // The history must be 2 or more, as we never want to remove the initial state
        return this._history.size() > 1;
    }

    public State undo(boolean shouldUpdateGUI) {
        State undoneState = null;

        if(this.canUndo()) {
            undoneState = this._history.pop();

            if (shouldUpdateGUI) {
                this.updateState(this._history.peek(), null, false);
            }
        }

        return undoneState;
    }

    public int undoStateTo(State state) {
        boolean removed = false;
        State popped = null;
        int count = 0;

        do {
            popped = this.undo(false);
            removed = popped == state;
            count++;
        } while(!removed && popped != null);

        this.updateState(this._history.peek(), null, false);

        return count;
    }

    private void _updateGUI(ArrayList<Move> successors) {
        this._gui.render(this._stateManager.getState(), successors, this);
    }
}
