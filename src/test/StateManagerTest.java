import main.Move;
import main.PieceState;
import main.State;
import main.StateManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StateManagerTest {
    private StateManager _stateManager;
    private State _testState;

    public StateManagerTest() {
        this._testState = StateManager.createTestState();
        this._stateManager = new StateManager(this._testState);
    }

    @Test
    public void testStateManagerGetState() {
        assertEquals(this._testState, this._stateManager.getState());
    }

    @Test
    public void testStateManagerGetPieceByLocation() {
        // If there is no piece at the location, null should be returned
        assertNull(this._stateManager.getPieceByLocation(3, 0));

        // If there is a piece at the location, the PieceState should be returned
        assertThat(this._stateManager.getPieceByLocation(5, 0), instanceOf(PieceState.class));
    }

    @Test
    public void testStateManagerCreateNewState() {
        // Select a piece to be moved
        int index = 12;
        PieceState piece = this._stateManager.getState().getPieces().get(index);
        assertEquals(5, piece.getX());
        assertEquals(0, piece.getY());

        // Create a new piece (moved version of piece we just selected)
        PieceState newPiece = new PieceState(piece.getX() - 2, piece.getY() + 2, piece.isKing());

        // Create a next state based on the initial state and moved piece
        State nextState = StateManager.createNewState(this._stateManager.getState(), piece, newPiece, true);

        // Using the same index as before, we should now find the piece has been updated
        PieceState changedPiece = nextState.getPieces().get(index);
        assertEquals(3, changedPiece.getX());
        assertEquals(2, changedPiece.getY());
    }

    @Test void testStateManagerSetState() {
        State nextState = StateManager.createInitialState();
        assertTrue(nextState.getTurn());
        this._stateManager.setState(nextState);
        assertEquals(nextState, this._stateManager.getState());

        State newState = StateManager.createNewState(this._stateManager.getState(), this._stateManager.getState().getPieces().get(0), new PieceState(-1, -1, false), true);
        this._stateManager.setState(newState);
        assertFalse(this._stateManager.getState().getTurn());
    }

    @Test
    public void testStateManagerCreate2DGrid() {
        // The test state should be represented in a grid as such
        int[][] expectedTestGrid = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0},
                {3, 0, 3, 0, 3, 0, 3, 0},
                {0, 3, 0, 3, 0, 3, 0, 3},
                {3, 0, 3, 0, 3, 0, 3, 0},
        };
        int[][] actualTestGrid = StateManager.create2DGrid(this._stateManager.getState().getPieces());
        assertArrayEquals(expectedTestGrid, actualTestGrid);

        // Change state to initial state and check that grid changes
        this._stateManager.setState(StateManager.createInitialState());
        int[][] expectedInitialGrid = new int[][]{
                {0, 1, 0, 1, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 0, 1, 0},
                {0, 1, 0, 1, 0, 1, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {3, 0, 3, 0, 3, 0, 3, 0},
                {0, 3, 0, 3, 0, 3, 0, 3},
                {3, 0, 3, 0, 3, 0, 3, 0},
        };
        int[][] actualInitialGrid = StateManager.create2DGrid(this._stateManager.getState().getPieces());
        assertArrayEquals(expectedInitialGrid, actualInitialGrid);
    }

    @Test
    public void testStateManagerGetSuccessors() {
        this._stateManager.setState(StateManager.createInitialState());

        // Expect moves to: [4, 1], [4, 1], [4, 3], [4, 3], [4, 5], [4, 5], [4, 7]
        int expectedNumOfMoves = 7;
        ArrayList<Move> moves = this._stateManager.getSuccessors();
        assertEquals(expectedNumOfMoves, moves.size());

        int[][] expectedMoves = new int[][]{
                {4, 1},
                {4, 1},
                {4, 3},
                {4, 3},
                {4, 5},
                {4, 5},
                {4, 7}
        };
        int[][] actualMoves = new int[expectedNumOfMoves][2];
        int i = 0;
        for(Move m: moves) {
            PieceState[] piece = PieceState.identifyChangedPiece(m.getCurrent().getPieces(), m.getNext().getPieces());
            actualMoves[i] = new int[]{piece[1].getX(), piece[1].getY()};
            i++;
        }
        assertArrayEquals(expectedMoves, actualMoves);

        // Ensure each move has no previous move as they aren't jumps
        for(Move m: moves) {
            assertNull(m.getPreviousMove());
        }
    }

    @Test
    public void testStateManagerGetSuccessorsWithJumps() {
        // Expect moves to: [1, 2], [1, 0], [1, 4]
        int expectedNumOfMoves = 3;
        ArrayList<Move> moves = this._stateManager.getSuccessors();
        assertEquals(expectedNumOfMoves, moves.size());

        int[][] expectedMoves = new int[][]{
                {1, 0},
                {1, 4},
                {1, 2},
        };
        int[][] actualMoves = new int[expectedNumOfMoves][2];
        int i = 0;
        for(Move m: moves) {
            PieceState[] piece = PieceState.identifyChangedPiece(m.getCurrent().getPieces(), m.getNext().getPieces());
            actualMoves[i] = new int[]{piece[1].getX(), piece[1].getY()};
            i++;
        }

        assertArrayEquals(expectedMoves, actualMoves);

        // Ensure each move has the necessary previous move pointer to allow GUI to do its work
        Move prevMove;
        PieceState piece;

        // [1, 0] -> [3, 2]
        prevMove = moves.get(0).getPreviousMove();
        piece = PieceState.identifyChangedPiece(prevMove.getCurrent().getPieces(), prevMove.getNext().getPieces())[1];
        assertEquals(3, piece.getX());
        assertEquals(2, piece.getY());

        // [1, 4] -> [3, 2]
        prevMove = moves.get(1).getPreviousMove();
        piece = PieceState.identifyChangedPiece(prevMove.getCurrent().getPieces(), prevMove.getNext().getPieces())[1];
        assertEquals(3, piece.getX());
        assertEquals(2, piece.getY());

        // [1, 2] -> [3, 0]
        prevMove = moves.get(2).getPreviousMove();
        piece = PieceState.identifyChangedPiece(prevMove.getCurrent().getPieces(), prevMove.getNext().getPieces())[1];
        assertEquals(3, piece.getX());
        assertEquals(0, piece.getY());
    }
}