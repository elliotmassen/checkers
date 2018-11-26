package main;

import java.util.ArrayList;
import java.util.HashSet;

public class StateManager {
    private ArrayList<PieceState> _state;

    public StateManager(ArrayList<PieceState> state) {
        this.setState(state);
    }

    public ArrayList<PieceState> getState() {
        return this._state;
    }

    // TODO: The fact this function needs to exist may mean my state representation isn't efficient, although this should
    // be weighted against the lesser complexity of the isGoalState method.
    public PieceState getPieceByLocation(int x, int y) {
        PieceState found = null;

        for(PieceState p: this.getState()) {
            if(p.getX() == x && p.getY() == y) {
                found = p;
                break;
            }
        }

        return found;
    }

    public void setState(ArrayList<PieceState> newState) {
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

    /**
     * getSuccessors
     * This method returns a list of Moves. Each move contains a current and next state, as well as a potential
     * following move (for jumps).
     * @param turn
     * @return A list of Moves.
     */
    // TODO: Should turn actually not be in the state representation?
    public ArrayList<Move> getSuccessors(boolean turn) {
        // Convert state rep into a 2d grid
        int[][] grid = StateManager.create2DGrid(this.getState());

        // For each piece in our turn (half of the rep) create Move's if there is an adjacent tile that is empty
        // or jump-able, and has a direction that is possible (king/not-king). For jumps we must explore the further
        // jump possibilities.

        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<Move> jumps = new ArrayList<Move>();
        int index = 0;
        for(PieceState p: this.getState()) {
            if(index < 12 != turn && p.isActive()) {
                int x = p.getX();
                int y = p.getY();

                // Top
                if (turn || p.isKing()) {
                    // Empty adjacent tile
                    if (x - 1 >= 0) {
                        // Left
                        if (y - 1 >= 0) {
                            // Top left is empty
                            if (grid[x - 1][y - 1] == 0) {
                                moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x - 1, y - 1, p.isKing())), null));
                            }
                        }

                        // Right
                        if (y + 1 < 8) {
                            // Bottom left is empty
                            if (grid[x - 1][y + 1] == 0) {
                                moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x - 1, y + 1, p.isKing())), null));
                            }
                        }
                    }
                }

                // Bottom
                if(!turn || p.isKing()) {
                    if (x + 1 < 8) {
                        // Left
                        if (y - 1 >= 0) {
                            // Top right is empty
                            if (grid[x + 1][y - 1] == 0) {
                                moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x + 1, y - 1, p.isKing())), null));
                            }
                        }

                        // Right
                        if (y + 1 < 8) {
                            // Bottom right is empty
                            if (grid[x + 1][y + 1] == 0) {
                                moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x + 1, y + 1, p.isKing())), null));
                            }
                        }
                    }
                }

                jumps.addAll(this._detectJumps(this.getState(), turn, p, null, null));
            }

            index++;
        }

        // If there are any jumps, in accordance with the rules they should be the only options to the user
        if(jumps.size() > 0) {
            moves = jumps;
        }

        return moves;
    }

    public boolean isGoalState() {
        return false;
    }

    public static ArrayList<PieceState> createInitialState() {
        // TODO: Use for loops (and modulo) to reduce lines here.
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

    public static ArrayList<PieceState> createTestState() {
        return new ArrayList<PieceState>() {{
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
        }};
    }

    public static ArrayList<PieceState> createNewState(ArrayList<PieceState> state, PieceState find, PieceState replace) {
        // This is just a shallow copy, so the PieceState references will remain the same - this is good for detect
        // equality (or lack thereof)
        ArrayList<PieceState> temp = (ArrayList<PieceState>) state.clone();
        int index = state.indexOf(find);

        if(index >= 0) {
            temp.set(index, replace);
        }

        return temp;
    }

    private ArrayList<Move> _detectJumps(ArrayList<PieceState> state, boolean turn, PieceState piece, Move previousMove, int[][] grid) {
        ArrayList<Move> moves = new ArrayList<Move>();

        // If there is no grid (eg. this is the initial call) then create it
        if(grid == null) {
            grid = StateManager.create2DGrid(state);
        }

        // Top left
        moves.addAll(this._detectJumpInDirection(-2, -2, state, turn, piece, previousMove, grid));

        // Top right
        moves.addAll(this._detectJumpInDirection(-2, 2, state, turn, piece, previousMove, grid));

        // Bottom left
        moves.addAll(this._detectJumpInDirection(2, -2, state, turn, piece, previousMove, grid));

        // Bottom right
        moves.addAll(this._detectJumpInDirection(2, 2, state, turn, piece, previousMove, grid));

        return moves;
    }

    private ArrayList<Move> _detectJumpInDirection(int xChange, int yChange, ArrayList<PieceState> state, boolean turn, PieceState piece, Move previousMove, int[][] grid) {
        ArrayList<Move> moves = new ArrayList<Move>();
        HashSet<Integer> enemies = new HashSet<Integer>();

        if(turn) {
            enemies.add(1);
            enemies.add(2);
        }
        else {
            enemies.add(3);
            enemies.add(4);
        }

        // Determine if the player is allowed to move up and/or down
        boolean canMoveTop = turn || piece.isKing();
        boolean canMoveBottom = !turn || piece.isKing();
        int xDirection = (int) Math.signum(xChange);

        // If player has permission to move in that direction and the space is valid
        if((xDirection == -1 && canMoveTop || xDirection == 1 && canMoveBottom)
                && Math.min(0, piece.getX() + xChange) == 0
                && Math.max(7, piece.getX() + xChange) == 7
                && Math.min(0, piece.getY() + yChange) == 0
                && Math.max(7, piece.getY() + yChange) == 7) {
            // Compute x & y changes (relative to piece) of the tile to be jumped over
            int inbetweenXChange = StateManager._computeInBetweenChange(xChange);
            int inbetweenYChange = StateManager._computeInBetweenChange(yChange);

            // If there is an empty space 2 tiles away and in between is an emeny (aka. a potential jump)
            if(grid[piece.getX() + xChange][piece.getY() + yChange] == 0
                    && enemies.contains(grid[piece.getX() + inbetweenXChange][piece.getY() + inbetweenYChange])) {
                PieceState newPiece = new PieceState(piece.getX() + xChange, piece.getY() + yChange, piece.isKing());
                ArrayList<PieceState> nextState = StateManager.createNewState(state, piece, newPiece);
                PieceState jumpedOver = this.getPieceByLocation(piece.getX() + inbetweenXChange, piece.getY() + inbetweenYChange);
                nextState = StateManager.createNewState(nextState, jumpedOver, new PieceState(-1, -1, jumpedOver.isKing()));

                Move newMove = new Move(state, nextState, previousMove);
                ArrayList<Move> followingMoves = this._detectJumps(nextState, turn, newPiece, newMove, null);

                // If there were no following moves, then newMove is a completeMove and should be returned
                if(followingMoves.size() < 1) {
                    moves.add(newMove);
                }
                // If there were following moves, then newMove is just a semiMove and shouldn't be returned
                else {
                    moves.addAll(followingMoves);
                }
            }
        }

        return moves;
    }

    private static int _computeInBetweenChange(int x) {
        return x + (-1 * (int) Math.signum(x));
    }
}
