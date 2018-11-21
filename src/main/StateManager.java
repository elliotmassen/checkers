package main;

import java.util.ArrayList;

public class StateManager {
    private ArrayList<PieceState> _state;

    public StateManager(ArrayList<PieceState> state) {
        this.setState(state);
    }

    public ArrayList<PieceState> getState() {
        return this._state;
    }

    public void setState(ArrayList<PieceState> newState) {
        this._state = newState;
    }

    public ArrayList<ArrayList<PieceState>> getSuccessors() {
        return new ArrayList<ArrayList<PieceState>>();
    }

    public boolean isGoalState() {
        return false;
    }

    public static ArrayList<PieceState> createInitialState() {
        return new ArrayList<PieceState>() {{
            // Red pieces
            add(new PieceState(0, 1, false));
            add(new PieceState(0, 3, false));
            add(new PieceState(0, 5, false));
            add(new PieceState(0, 7, false));
            add(new PieceState(1, 0, false));
            add(new PieceState(1, 2, false));
            add(new PieceState(1, 4, false));
            add(new PieceState(1, 6, false));
            add(new PieceState(2, 1, false));
            add(new PieceState(2, 3, false));
            add(new PieceState(2, 5, false));
            add(new PieceState(2, 7, false));

            // Black pieces
            add(new PieceState(5, 0, false));
            add(new PieceState(5, 2, false));
            add(new PieceState(5, 4, false));
            add(new PieceState(5, 6, false));
            add(new PieceState(6, 1, false));
            add(new PieceState(6, 3, false));
            add(new PieceState(6, 5, false));
            add(new PieceState(6, 7, false));
            add(new PieceState(7, 0, false));
            add(new PieceState(7, 2, false));
            add(new PieceState(7, 4, false));
            add(new PieceState(7, 6, false));
        }};
    }
}
