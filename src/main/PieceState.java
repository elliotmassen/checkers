package main;

import java.util.ArrayList;

public class PieceState {
    private int _x;
    private int _y;
    private boolean _isKing;

    public PieceState(int x, int y, boolean isKing) {
        this._x = x;
        this._y = y;
        this._isKing = isKing;
    }

    public int getX() {
        return this._x;
    }

    public int getY() {
        return this._y;
    }

    public boolean isKing() {
        return this._isKing;
    }

    public boolean isActive() {
        return this.getX() >= 0 && this.getY() >= 0;
    }

    public boolean equals(PieceState compare) {
        return this == compare
                && this.getX() == compare.getX()
                && this.getY() == compare.getY();
    }

    public static PieceState[] identifyChangedPiece(ArrayList<PieceState> current, ArrayList<PieceState> next) {
        PieceState[] changed = null;
        int i = 0;

        // If changed is not longer null it means we found the changed piece
        while(i < current.size() && changed == null) {
            // We don't care about inactive pieces, we only want an active piece in new position
            if(current.get(i).isActive() && next.get(i).isActive() && !current.get(i).equals(next.get(i))) {
                changed = new PieceState[]{current.get(i), next.get(i)};
            }
            i++;
        }

        return changed;
    }

    public static String changesToString(ArrayList<PieceState> current, ArrayList<PieceState> next) {
        String changes = "";

        PieceState[] movedPieces = PieceState.identifyChangedPiece(current, next);
        PieceState movedPieceCurrent = movedPieces[0];
        int index = current.indexOf(movedPieceCurrent);
        PieceState movedPieceNext = next.get(index);

        int xChange = movedPieceCurrent.getX() - movedPieceNext.getX();
        int yChange = movedPieceCurrent.getY() - movedPieceNext.getY();

        String verb = "moves";
        if(Math.abs(xChange) > 1 || Math.abs(yChange) > 1) {
            verb = "jumps";
        }

        changes = verb + " from [" + movedPieceCurrent.getX() + ", " + movedPieceCurrent.getY() + "] to [" + movedPieceNext.getX() + ", " + movedPieceNext.getY() + "]";

        return changes;
    }

    public static String stateToString(ArrayList<PieceState> state) {
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

        String gridString = "";
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                gridString += " " + Integer.toString(grid[i][j]);
            }
            gridString += "\n";
        }

        return gridString;
    }
}
