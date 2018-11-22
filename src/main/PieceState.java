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

    public static String changesToString(ArrayList<PieceState> current, ArrayList<PieceState> next) {
        String changes = "";

        for(int i = 0; i < current.size(); i++) {
            if(!current.get(i).equals(next.get(i))) {
                int xChange = current.get(i).getX() - next.get(i).getX();
                int yChange = current.get(i).getY() - next.get(i).getY();

                String verb = "moves";
                if(Math.abs(xChange) > 1 || Math.abs(yChange) > 1) {
                    verb = "jumps";
                }

                changes = verb + " from [" + current.get(i).getX() + ", " + current.get(i).getY() + "] to [" + next.get(i).getX() + ", " + next.get(i).getY() + "]";
                break;
            }
        }

        return changes;
    }
}
