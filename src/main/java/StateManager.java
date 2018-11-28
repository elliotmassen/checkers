import java.util.ArrayList;
import java.util.HashSet;

public class StateManager {
    private State _state;

    public StateManager() {
        this.setState(null);
    }

    public StateManager(State state) {
        this.setState(state);
    }

    public State getState() {
        return this._state;
    }

    public void setState(State newState) {
        this._state = newState;
    }

    /**
     * create2DGrid
     * Creates a 2D integer array that represents the game state. Each tile is represented (indexed by location) as follows:
     * - 0: empty tile
     * - 1: red piece
     * - 2: red king piece
     * - 3: black piece
     * - 4: black king piece
     * @param state
     * @return 2D integer array
     */
    public static int[][] create2DGrid(ArrayList<PieceState> state) {
        int[][] grid = new int[8][8];
        for(int i = 0; i < state.size(); i++) {
            int x = state.get(i).getX();
            int y = state.get(i).getY();
            boolean isKing = state.get(i).isKing();

            // If pieces are still active
            if(x >= 0 && y >= 0) {
                if(i < 12) {
                    // Red
                    grid[x][y] = 1;

                    if(isKing) {
                        grid[x][y] = 2;
                    }
                }
                else {
                    // Black
                    grid[x][y] = 3;

                    if(isKing) {
                        grid[x][y] = 4;
                    }
                }
            }
        }

        return grid;
    }

    public static State createInitialState() {
        // TODO: Use for loops (and modulo) to reduce lines here.
        return new State(new ArrayList<PieceState>() {{
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
        }}, true);
    }

    public static State createTestState() {
        return new State(new ArrayList<PieceState>() {{
            // Red pieces
            add(new PieceState(-1, -1, false));
            add(new PieceState(-1, -1, false));
            add(new PieceState(-1, -1, false));
            add(new PieceState(-1, -1, false));
            add(new PieceState(-1, -1, false));
            add(new PieceState(-1, -1, false));
            add(new PieceState(-1, -1, false));
            add(new PieceState(-1, -1, false));
            add(new PieceState(-1, -1, false));
            add(new PieceState(2, 1, false));
            add(new PieceState(4, 1, false));
            add(new PieceState(2, 3, false));

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
        }}, true);
    }

    public static State createNewState(State state, PieceState find, PieceState replace, boolean endTurn) {
        // This is just a shallow copy, so the PieceState references will remain the same - this is good for detect
        // equality (or lack thereof)
        ArrayList<PieceState> tempPieces = (ArrayList<PieceState>) state.getPieces().clone();
        int index = state.getPieces().indexOf(find);

        if(index >= 0) {
            tempPieces.set(index, replace);
        }

        boolean turn = endTurn ? !state.getTurn() : state.getTurn();

        return new State(tempPieces, turn);
    }

    public static int getStateValue(State state) {
        int value = 0;

        if(state.isGoalState(state.getTurn(), state.getSuccessors())) {
            if(state.getTurn()) {
                value = 1;
            }
            else {
                value = -1;
            }
        }

        return value;
    }
}
