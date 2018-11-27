package main;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class Controller {
    private GUI _gui;
    private StateManager _stateManager;
    private Stack<State> _history;
    private boolean _gameOver;

    public enum Type {
        RED,
        BLACK,
        INFO
    }

    public Controller(GUI gui, StateManager stateManager) {
        this._gui = gui;
        this._stateManager = stateManager;
        this._history = new Stack<State>();
        this._gameOver = false;
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
            successors = this._stateManager.getState().getSuccessors();
        }

        this._gameOver = false;
        if(this._stateManager.getState().isGoalState(!this._stateManager.getState().getTurn(), successors)) {
            this._gui.gameOver(this._stateManager.getState(), previousState, this);
            this._gameOver = true;
        }

        this._updateGUI(successors);

        // Make AI move
        if(!this._stateManager.getState().getTurn() && !this.isGameOver()) {
            try {
                TimeUnit.SECONDS.sleep((long) 0.3);
            }
            catch(Exception e) {}

            // Pick move to make
            Move optimalAIMove = this._getAIMove();

            // Make move
            this.updateState(optimalAIMove.getNext(), null, true);
        }
    }

    private Move _getAIMove() {
        ArrayList<Move> optimalMoves = new ArrayList<Move>();
        this._minimax(this._stateManager.getState(), 5, optimalMoves);

        return optimalMoves.get(0);
    }

    private int _minimax(State state, int depth, ArrayList<Move> optimalMoves) {
        ArrayList<Move> successors = state.getSuccessors();

        if(depth == 0 || successors.isEmpty()) {
            return StateManager.getStateValue(state);
        }
        else if(state.getTurn()) {
            // Human
            int bestValue = Integer.MIN_VALUE;

            for(Move m: successors) {
                int eval = this._minimax(m.getNext(), --depth, null);

                if(eval > bestValue) {
                    if(optimalMoves != null) {
                        optimalMoves.clear();
                        optimalMoves.add(m);
                    }

                    return eval;
                }
                else {
                    return bestValue;
                }
            }
        }
        else {
            // AI
            int bestValue = Integer.MAX_VALUE;

            for(Move m: successors) {
                int eval = this._minimax(m.getNext(), --depth, null);

                if(eval < bestValue) {
                    if(optimalMoves != null) {
                        optimalMoves.clear();
                        optimalMoves.add(m);
                    }

                    return eval;
                }
                else {
                    return bestValue;
                }
            }
        }

        return 0;
    }

    private void _addToHistory(State state) {
        this._history.add(state);
    }

    public boolean isGameOver() {
        return this._gameOver;
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
