import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PieceStateTest {
    private PieceState _pieceState;

    @BeforeAll
    public void beforeAll() {
        this._pieceState = new PieceState(1, 1, false);
    }

    @Test
    public void testPieceStateConstruction() {
        assertEquals(1, this._pieceState.getX());
        assertEquals(1, this._pieceState.getY());
        assertFalse(this._pieceState.isKing());
    }

    @Test
    public void testPieceStateInactivation() {
        // Piece should be active as it's coordinates are valid
        assertTrue(this._pieceState.isActive());

        // Create an inactive piece state by using invalid coordinates
        PieceState pieceState = new PieceState(-1, -1, false);
        assertFalse(pieceState.isActive());
    }

    @Test
    public void testPieceStateEquality() {
        // Create a new piece state with same attributes
        PieceState pieceStateClone = new PieceState(this._pieceState.getX(), this._pieceState.getY(), this._pieceState.isKing());

        // These piece states are not equal as they are different objects, this is important as there is a potential for
        // a piece to move into a space previously occupied by another piece and they must be regarded as different pieces
        assertNotEquals(pieceStateClone, this._pieceState);
    }

    @Test
    public void testIdentifyChangedPieceInState() {
        // Create an initial state
        State state = StateManager.createTestState();

        // Select a piece to be moved
        int index = 12;
        PieceState piece = ((State) state).getPieces().get(index);
        assertEquals(5, piece.getX());
        assertEquals(0, piece.getY());

        // Create a new piece (moved version of piece we just selected)
        PieceState newPiece = new PieceState(piece.getX() - 2, piece.getY() + 0, piece.isKing());

        // Create a next state based on the initial state and moved piece
        State nextState = StateManager.createNewState(state, piece, newPiece, true);

        // identifyChangedPiece should return two pieces, the original piece and the moved piece
        PieceState changedPiece = nextState.getPieces().get(index);
        PieceState[] expectedChangedPiece = PieceState.identifyChangedPiece(state.getPieces(), nextState.getPieces());
        assertEquals(expectedChangedPiece[0], piece);
        assertEquals(expectedChangedPiece[1], changedPiece);

        // changesToString should produce a correct string
        String message = PieceState.changesToString(state.getPieces(), nextState.getPieces());
        assertEquals(message, "jumps from [5, 0] to [3, 0]");
    }
}