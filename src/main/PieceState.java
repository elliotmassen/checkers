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

    public static PieceState identifyChangedPiece(ArrayList<PieceState> current, ArrayList<PieceState> next) {
        PieceState changed = null;
        int i = 0;

        // If changed is not longer null it means we found the changed piece
        while(i < current.size() && changed == null) {
            // We don't care about inactive pieces, we only want an active piece in new position
            if(current.get(i).isActive() && next.get(i).isActive() && !current.get(i).equals(next.get(i))) {
                changed = current.get(i);
            }
            i++;
        }

        return changed;
    }

    public static String changesToString(ArrayList<PieceState> current, ArrayList<PieceState> next) {
        String changes = "";

        PieceState movedPieceCurrent = PieceState.identifyChangedPiece(current, next);
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
}
