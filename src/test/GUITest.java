import main.GUI;
import main.Move;
import main.PieceState;
import main.StateManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GUITest {
    @Test
    public void testGUIGroupBySharedPath() {
        StateManager stateManager = new StateManager(StateManager.createTestState());

        // moves = {[1, 0], [1, 4], [1, 2]}, each has pointers to previous moves. This is tested in StateManagerTest
        ArrayList<Move> moves = stateManager.getState().getSuccessors();

        // As move 3 and 4 share a common ancestor semiMove, their pointers should reference the same object
        assertEquals(moves.get(0).getPreviousMove(), moves.get(1).getPreviousMove());

        // completeMove is a move that is not part of a greater move path
        // semiMove is a move that only exists as a part of a greater move path
        // The following is a general representation of what data is stored in group, however the real data type differs
        /* group = {
            [3, 0] => semiMove([1, 2])              (move 0)
            [1, 2] => completeMove,                 (move 1)
            [3, 2] => semiMove([1, 0], [1, 4]),     (move 2)
            [1, 0] => completeMove,                 (move 3)
            [1, 4] => completeMove                  (move 4)
        }
         */
        HashMap<PieceState, ArrayList<Move>> group = GUI.groupBySharedPath(moves, null);
        assertEquals(5, group.size());

        // Get the moves so we can compare the objects we have in the group
        Move[] move = new Move[5];
        move[0] = moves.get(2).getPreviousMove();
        move[1] = moves.get(2);
        move[2] = moves.get(0).getPreviousMove(); // this could be moves.get(1).getPreviousMove() too
        move[3] = moves.get(0);
        move[4] = moves.get(1);

        // Compare coordinates of changed pieces
        int[][] expectedPieceCoords = new int[][]{
                {3, 0},
                {1, 2},
                {3, 2},
                {1, 0},
                {1, 4}
        };

        int[][] pieceCoords = new int[5][2];
        PieceState[] pieces = new PieceState[5];
        for(int i = 0; i < 5; i++) {
            pieces[i] = PieceState.identifyChangedPiece(move[i].getCurrent().getPieces(), move[i].getNext().getPieces())[1];
            pieceCoords[i] = new int[]{pieces[i].getX(), pieces[i].getY()};
        }

        assertArrayEquals(expectedPieceCoords, pieceCoords);

        // Ensure the values in the hash map are lists of completing moves for semiMoves, or null for completeMoves
        ArrayList<Move>[] completingMoves = new ArrayList[5];
        for(int i = 0; i < 5; i++) {
            completingMoves[i] = group.get(pieces[i]);
        }

        assertEquals(1, completingMoves[0].size());
        assertEquals(1, completingMoves[1].size());
        assertEquals(2, completingMoves[2].size());
        assertEquals(1, completingMoves[3].size());
        assertEquals(1, completingMoves[4].size());

        // Move 0 is a semi move
        assertFalse(move[0].isEndMove());

        // Move 1 is a complete move
        assertTrue(move[1].isEndMove());

        // Move 2 is a semi move
        assertFalse(move[2].isEndMove());

        // Move 3 is a complete move
        assertTrue(move[3].isEndMove());

        // Move 4 is a complete move
        assertTrue(move[4].isEndMove());

        // Move 0's completing move is move 1
        assertEquals(move[1], completingMoves[0].get(0));

        // Move 1's completing move is itself
        assertEquals(move[1], completingMoves[1].get(0));

        // One of Move 2's completing moves is move 3
        assertEquals(move[3], completingMoves[2].get(0));

        // One of Move 2's completing moves is move 4
        assertEquals(move[4], completingMoves[2].get(1));

        // Move 3's completing move is itself
        assertEquals(move[3], completingMoves[3].get(0));

        // Move 4's completing move is itself
        assertEquals(move[4], completingMoves[4].get(0));
    }
}