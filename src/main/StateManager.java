package main;

import java.lang.reflect.Array;
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

    public void setState(ArrayList<PieceState> newState) {
        this._state = newState;
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
        int[][] grid = new int[8][8];
        for(int i = 0; i < this.getState().size(); i++) {
            int x = this.getState().get(i).getX();
            int y = this.getState().get(i).getY();
            boolean isKing = this.getState().get(i).isKing();

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

        // For each piece in our turn (half of the rep) create Move's if there is an adjacent tile that is empty
        // or jump-able, and has a direction that is possible (king/not-king). For jumps we must explore the further
        // jump possibilities.

        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<Move> jumps = new ArrayList<Move>();
        int index = 0;
        for(PieceState p: this.getState()) {
            if(index < 12 != turn) {
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

                jumps.addAll(this._detectJumps(grid, p, turn, x, y));
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
        return new ArrayList<PieceState>() {{
            // Red pieces
//            add(new PieceState(0, 1, false));
//            add(new PieceState(0, 3, false));
//            add(new PieceState(0, 5, false));
//            add(new PieceState(0, 7, false));
//            add(new PieceState(1, 0, false));
//            add(new PieceState(1, 2, false));
//            add(new PieceState(1, 4, false));
//            add(new PieceState(1, 6, false));
//            add(new PieceState(2, 1, false));
//            add(new PieceState(2, 3, false));
//            add(new PieceState(2, 5, false));
//            add(new PieceState(2, 7, false));
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
        // TODO: Is this is just a shallow copy, then surely the PieceState references will remain the same and cause issues?
        ArrayList<PieceState> temp = (ArrayList<PieceState>) state.clone();
        int index = state.indexOf(find);
        temp.set(index, replace);
        return temp;
    }

    private ArrayList<Move> _detectJumps(int[][] grid, PieceState p, boolean turn, int x, int y) {
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

        // Top
        if (turn || p.isKing()) {
            // Empty adjacent tile
            if (x - 2 >= 0) {
                // Left
                if (y - 2 >= 0) {
                    // Top left is empty and there is an enemy in between
                    if (grid[x - 2][y - 2] == 0 && enemies.contains(grid[x - 1][y - 1])) {
                        ArrayList<Move> followingJumps = this._detectJumps(grid, p, turn, x-2, y-2);
                        if(followingJumps.size() < 1) {
                            moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x - 2, y - 2, p.isKing())), null));
                        }
                        else {
                            followingJumps.forEach((Move m) -> {
                                moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x - 2, y - 2, p.isKing())), m));
                            });
                        }
                    }
                }

                // Right
                if (y + 2 < 8) {
                    // Bottom left is empty and there is an enemy in between
                    if (grid[x - 2][y + 2] == 0 && enemies.contains(grid[x - 1][y + 1])) {
                        ArrayList<Move> followingJumps = this._detectJumps(grid, p, turn, x-2, y+2);
                        if(followingJumps.size() < 1) {
                            moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x - 2, y + 2, p.isKing())), null));
                        }
                        else {
                            followingJumps.forEach((Move m) -> {
                                moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x - 2, y + 2, p.isKing())), m));
                            });
                        }
                    }
                }
            }
        }

        // Bottom
        if(!turn || p.isKing()) {
            if (x + 2 < 8) {
                // Left
                if (y - 2 >= 0) {
                    // Top right is empty and there is an enemy in between
                    if (grid[x + 2][y - 2] == 0 && enemies.contains(grid[x + 1][y - 1])) {
                        ArrayList<Move> followingJumps = this._detectJumps(grid, p, turn, x+2, y-2);
                        if(followingJumps.size() < 1) {
                            moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x + 2, y - 2, p.isKing())), null));
                        }
                        else {
                            followingJumps.forEach((Move m) -> {
                                moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x + 2, y - 2, p.isKing())), m));
                            });
                        }
                    }
                }

                // Right
                if (y + 2 < 8) {
                    // Bottom right is empty and there is an enemy in between
                    if (grid[x + 2][y + 2] == 0 && enemies.contains(grid[x + 1][y + 1])) {
                        ArrayList<Move> followingJumps = this._detectJumps(grid, p, turn, x+2, y+2);
                        if(followingJumps.size() < 1) {
                            moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x + 2, y + 2, p.isKing())), null));
                        }
                        else {
                            followingJumps.forEach((Move m) -> {
                                moves.add(new Move(this.getState(), StateManager.createNewState(this.getState(), p, new PieceState(x + 2, y + 2, p.isKing())), m));
                            });
                        }
                    }
                }
            }
        }

        return moves;
    }
}
