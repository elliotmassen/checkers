package main;

import java.util.ArrayList;

public class State {
    private ArrayList<PieceState> _pieces;
    private boolean _turn;

    public State(ArrayList<PieceState> pieces, boolean turn) {
        this._pieces = pieces;
        this._turn = turn;
    }

    public ArrayList<PieceState> getPieces() {
        return _pieces;
    }

    public boolean getTurn() {
        return _turn;
    }

    public void setTurn(boolean turn) {
        this._turn = turn;
    }
}
