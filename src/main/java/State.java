import java.util.ArrayList;
import java.util.HashSet;

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

    /**
     * getSuccessors
     * This method returns a list of Moves. Each move contains a current and next state, as well as a potential
     * following move (for jumps).
     * @return A list of Moves.
     */
    public ArrayList<Move> getSuccessors() {
        // Convert state rep into a 2d grid
        int[][] grid = StateManager.create2DGrid(this.getPieces());

        // For each piece in our turn (half of the rep) create Move's if there is an adjacent tile that is empty
        // or jump-able, and has a direction that is possible (king/not-king). For jumps we must explore the further
        // jump possibilities.

        ArrayList<Move> moves = new ArrayList<Move>();
        ArrayList<Move> jumps = new ArrayList<Move>();
        int index = 0;
        for(PieceState p: this.getPieces()) {
            if(index < 12 != this.getTurn() && p.isActive()) {
                moves.addAll(this._detectMoves(this, p, grid));
                jumps.addAll(this._detectJumps(this, p, null, this.getTurn(), null));
            }

            index++;
        }

        // TODO: It would be more efficient to detect jumps first and only detect moves if there are no jumps
        // If there are any jumps, in accordance with the rules they should be the only options to the user
        if(jumps.size() > 0) {
            moves = jumps;
        }

        for(Move m: moves) {
            m.setIsEndMove(true);
            m.getNext().setTurn(!this.getTurn());
        }

        return moves;
    }

    public boolean isGoalState(boolean turn, ArrayList<Move> successors) {
        // The opponent has no active pieces
        boolean opponentHasActivePieces = false;
        int index = 0;
        for(PieceState p: this.getPieces()) {
            if(index < 12 == turn && p.isActive()) {
                opponentHasActivePieces = true;
                break;
            }

            index++;
        }

        // Or, the opponent has no moves they can make
        int numberOfSuccessorStates = successors.size();

        return !opponentHasActivePieces || numberOfSuccessorStates < 1;
    }

    // TODO: The fact this function needs to exist may mean my state representation isn't efficient, although this should
    // be weighted against the lesser complexity of the isGoalState method.
    public PieceState getPieceByLocation(int x, int y) {
        PieceState found = null;

        for(PieceState p: this.getPieces()) {
            if(p.getX() == x && p.getY() == y) {
                found = p;
                break;
            }
        }

        return found;
    }

    private ArrayList<Move> _detectJumps(State state, PieceState piece, Move previousMove, boolean overrideTurn, int[][] grid) {
        ArrayList<Move> moves = new ArrayList<Move>();

        // If there is no grid (eg. this is the initial call) then create it
        if(grid == null) {
            grid = StateManager.create2DGrid(state.getPieces());
        }

        // Top left
        moves.addAll(this._detectJumpInDirection(-2, -2, state, piece, previousMove, overrideTurn, grid));

        // Top right
        moves.addAll(this._detectJumpInDirection(-2, 2, state, piece, previousMove, overrideTurn, grid));

        // Bottom left
        moves.addAll(this._detectJumpInDirection(2, -2, state, piece, previousMove, overrideTurn, grid));

        // Bottom right
        moves.addAll(this._detectJumpInDirection(2, 2, state, piece, previousMove, overrideTurn, grid));

        return moves;
    }

    private ArrayList<Move> _detectJumpInDirection(int xChange, int yChange, State state, PieceState piece, Move previousMove, boolean overrideTurn, int[][] grid) {
        ArrayList<Move> moves = new ArrayList<Move>();
        HashSet<Integer> enemies = new HashSet<Integer>();

        if(overrideTurn) {
            enemies.add(1);
            enemies.add(2);
        }
        else {
            enemies.add(3);
            enemies.add(4);
        }

        // Determine if the player is allowed to move up and/or down
        boolean canMoveTop = overrideTurn || piece.isKing();
        boolean canMoveBottom = !overrideTurn || piece.isKing();
        int xDirection = (int) Math.signum(xChange);

        // If player has permission to move in that direction and the space is valid
        if((xDirection == -1 && canMoveTop || xDirection == 1 && canMoveBottom)
                && Math.min(0, piece.getX() + xChange) == 0
                && Math.max(7, piece.getX() + xChange) == 7
                && Math.min(0, piece.getY() + yChange) == 0
                && Math.max(7, piece.getY() + yChange) == 7) {
            // Compute x & y changes (relative to piece) of the tile to be jumped over
            int inbetweenXChange = State._computeInBetweenChange(xChange);
            int inbetweenYChange = State._computeInBetweenChange(yChange);

            // If there is an empty space 2 tiles away and in between is an enemy (aka. a potential jump)
            if(grid[piece.getX() + xChange][piece.getY() + yChange] == 0
                    && enemies.contains(grid[piece.getX() + inbetweenXChange][piece.getY() + inbetweenYChange])) {
                // Create state in which the piece has jumped
                PieceState newPiece = new PieceState(piece.getX() + xChange, piece.getY() + yChange, piece.isKing());
                newPiece.makeKingIfAtBoardEnd(overrideTurn);
                State nextState1 = StateManager.createNewState(state, piece, newPiece, false);

                // Create based on the previous one in which the jumped over piece becomes in active
                PieceState jumpedOver = this.getPieceByLocation(piece.getX() + inbetweenXChange, piece.getY() + inbetweenYChange);
                State nextState = StateManager.createNewState(nextState1, jumpedOver, new PieceState(-1, -1, jumpedOver.isKing()), false);

                Move newMove = new Move(state, nextState, previousMove);
                ArrayList<Move> followingMoves = this._detectJumps(nextState, newPiece, newMove, overrideTurn, null);

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

    private ArrayList<Move> _detectMoves(State state, PieceState piece, int[][] grid) {
        ArrayList<Move> moves = new ArrayList<Move>();

        // Top left
        moves.addAll(this._detectMoveInDirection(-1, -1, state, piece, grid));

        // Top right
        moves.addAll(this._detectMoveInDirection(-1, 1, state, piece, grid));

        // Bottom left
        moves.addAll(this._detectMoveInDirection(1, -1, state, piece, grid));

        // Bottom right
        moves.addAll(this._detectMoveInDirection(1, 1, state, piece, grid));

        return moves;
    }

    // An array list is used (even though it'll only be a singular move) to avoid having to do null checking
    private ArrayList<Move> _detectMoveInDirection(int xChange, int yChange, State state, PieceState piece, int[][] grid) {
        ArrayList<Move> moves = new ArrayList<Move>();

        // The piece can move if has permission to move in that direction, the tile is valid and the destination tile is empty
        boolean hasPermission = piece.isKing() || (xChange == -1 && state.getTurn()) || (xChange == 1 && !state.getTurn());
        boolean hasPermissionAndTileIsValid = hasPermission
                && Math.min(0, piece.getX() + xChange) == 0
                && Math.max(7, piece.getX() + xChange) == 7
                && Math.min(0, piece.getY() + yChange) == 0
                && Math.max(7, piece.getY() + yChange) == 7;
        boolean hasPermissionAndTileIsValidAndTileIsEmpty = hasPermissionAndTileIsValid
                && grid[piece.getX() + xChange][piece.getY() + yChange] == 0;

        if (hasPermissionAndTileIsValidAndTileIsEmpty) {
            PieceState newPiece = new PieceState(piece.getX() + xChange, piece.getY() + yChange, piece.isKing());
            newPiece.makeKingIfAtBoardEnd(state.getTurn());
            moves.add(new Move(state, StateManager.createNewState(state, piece, newPiece, true), null));
        }

        return moves;
    }

    private static int _computeInBetweenChange(int x) {
        return x + (-1 * (int) Math.signum(x));
    }
}