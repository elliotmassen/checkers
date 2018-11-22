package main;

import java.util.ArrayList;

public class Move {
    private ArrayList<PieceState> _current;
    private ArrayList<PieceState> _next;
    private Move _followingMove;

    public Move(ArrayList<PieceState> current, ArrayList<PieceState> next, Move followingMove) {
        this._current = current;
        this._next = next;
        this._followingMove = followingMove;
    }

    public ArrayList<PieceState> getCurrent() {
        return this._current;
    }

    public ArrayList<PieceState> getNext() {
        return this._next;
    }

    public Move getFollowingMove() {
        return this._followingMove;
    }

    public ArrayList<Move> getAllMoves() {
        ArrayList<Move> allMoves = new ArrayList<Move>();

        if(this.getFollowingMove() != null) {
            allMoves.add(this.getFollowingMove());
            allMoves.addAll(this.getFollowingMove().getAllMoves());
        }

        return allMoves;
    }

    public ArrayList<PieceState> getFinalState() {
        return this.getFollowingMove() != null ? this.getFollowingMove().getFinalState() : this.getNext();
    }
}
