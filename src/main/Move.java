package main;

import java.util.ArrayList;

public class Move {
    private ArrayList<PieceState> _current;
    private ArrayList<PieceState> _next;
    private Move _previousMove;
    private Move _followingMove;
    private boolean _isOrigin;

    public Move(ArrayList<PieceState> current, ArrayList<PieceState> next, Move previousMove, Move followingMove, boolean isOrigin) {
        this._current = current;
        this._next = next;
        this._previousMove = previousMove;
        this._followingMove = followingMove;
        this._isOrigin = isOrigin;
    }

    public ArrayList<PieceState> getCurrent() {
        return this._current;
    }

    public ArrayList<PieceState> getNext() {
        return this._next;
    }

    public Move getPreviousMove() { return this._previousMove; }

    public Move getFollowingMove() {
        return this._followingMove;
    }

    public void setPreviousMove(Move move) { this._previousMove = move; }

    public void setFollowingMove(Move move) {
        this._followingMove = move;
    }

    public boolean isOrigin() {
        return this._isOrigin;
    }

    public ArrayList<Move> getAllMoves() {
        ArrayList<Move> allMoves = new ArrayList<Move>();

        if(this.getFollowingMove() != null) {
            allMoves.add(this.getFollowingMove());
            allMoves.addAll(this.getFollowingMove().getAllMoves());
        }

        return allMoves;
    }

    public ArrayList<PieceState> getFirstState() {
        return this.isOrigin() ? this.getCurrent() : this.getPreviousMove().getFirstState();
    }

    public ArrayList<PieceState> getFinalState() {
        return this.getFollowingMove() != null ? this.getFollowingMove().getFinalState() : this.getNext();
    }

    public Move clone() {
        return new Move(this._current, this._next, this._previousMove, this._followingMove, this._isOrigin);
    }
}
