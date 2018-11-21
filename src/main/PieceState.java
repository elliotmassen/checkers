package main;

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
}
