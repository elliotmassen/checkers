import java.util.ArrayList;

public class Move {
    private State _current;
    private State _next;
    private Move _previousMove;
    private boolean _isEndMove;

    public Move(State current, State next, Move previousMove) {
        this._current = current;
        this._next = next;
        this._previousMove = previousMove;

        this.setIsEndMove(false);
    }

    public State getCurrent() {
        return this._current;
    }

    public State getNext() {
        return this._next;
    }

    public Move getPreviousMove() { return this._previousMove; }

    public boolean isEndMove() { return this._isEndMove; }

    public void setIsEndMove(boolean isEndMove) {
        this._isEndMove = isEndMove;
    }

    public Move getFirstMove() {
        return this.getPreviousMove() == null ? this : this.getPreviousMove().getFirstMove();
    }

    public Move clone() {
        return new Move(this._current, this._next, this._previousMove);
    }
}
