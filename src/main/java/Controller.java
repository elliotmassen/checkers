import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class Controller {
    private GUI _gui;
    private StateManager _stateManager;
    private Stack<State> _history;
    private boolean _gameOver;
    private int _difficulty;
    private int _evaluations;

    public enum Type {
        RED,
        BLACK,
        INFO
    }

    /**
     * The constructor sets up the initial values of the game, and sets difficulty to medium by default.
     * @param gui Instance of the the GUI
     * @param stateManager Instance of the State Manager
     */
    public Controller(GUI gui, StateManager stateManager) {
        this._gui = gui;
        this._stateManager = stateManager;
        this._difficulty = 1;
    }

    /**
     * Creates the initial state and renders the board, preparing for the first move
     * @param reset Whether the GUI is being reset.
     */
    public void setup(boolean reset) {
        this._history = new Stack<State>();
        this._gameOver = false;
        State initialState = StateManager.createTestState();
        this._gui.setup(initialState, reset, this);
        this._addToHistory(initialState);
        this.updateState(initialState, null, false);
    }

    /**
     * Updates the game state to {@code newState}. If {@code successors} is null, then the successors will be
     * evaluated using {@code State.getSuccessors()}. {@code successors} can be used to restrict the next moves that
     * can be taken - for example when a semi move has occurred. Similarly, {@code shouldAddToHistory} determines
     * whether a new history item should be created for the new state.
     * @param newState The state to update to.
     * @param successors A list of restricted successor moves.
     * @param shouldAddToHistory Whether to add this state to the history stack.
     */
    public void updateState(State newState, @Nullable ArrayList<Move> successors, boolean shouldAddToHistory) {
        // Store the previous state in case we're going to end the turn
        State previousState = this._stateManager.getState();

        // Update the state
        this._stateManager.setState(newState);

        if(shouldAddToHistory) {
            // Add to history stack
            this._addToHistory(this._stateManager.getState());

            // Update the GUI to reflect the turn end (eg. add history item to sidebar)
            this._gui.endOfTurn(this._stateManager.getState(), previousState, this);
        }

        if(successors == null) {
            // If there were no given successors it means that we are free to evaluate them, unrestricted
            successors = this._stateManager.getState().getSuccessors();
        }

        this._gameOver = false;
        if(this._stateManager.getState().isGoalState(!this._stateManager.getState().getTurn(), successors)) {
            // Update the GUI to reflect that the game is over
            this._gui.gameOver(this._stateManager.getState(), previousState, this);
            this._gameOver = true;
        }

        // Update the GUI to reflect the new state and it's successor moves
        this._updateGUI(successors);

        // Check if it is time for the AI to make a move
        if(!this._stateManager.getState().getTurn() && !this.isGameOver()) {
            // Pick move to make
            Move optimalAIMove = this._getAIMove();

            if(this._evaluations < 1000) {
                // If the move was chosen quickly, sleep for 300 milliseconds to allow the human player to visually
                // register the change to the GUI
                try {
                    TimeUnit.SECONDS.sleep((long) 0.3);
                }
                catch (Exception e) {
                }
            }

            // Update the state
            this.updateState(optimalAIMove.getNext(), null, true);
        }
    }

    /**
     * @return An integer between 0 and 2 that represents the difficulty [easy, medium, hard].
     */
    public int getDifficulty() {
        return this._difficulty;
    }

    /**
     * @param level The new difficulty level.
     */
    public void setDifficulty(int level) {
        this._difficulty = level;
    }

    /**
     * Determines the optimal move for the AI player to take, given the current game state.
     * @return An instance of {@link Move} that contains the optimal game state.
     */
    private Move _getAIMove() {
        // An array is used rather than a Move object, as the array makes it easier to differentiate in recursive
        // minimax calls, eg. in the initial call the array is empty, but in recursive calls it is null. This can not
        // be achieved with the object alone.
        ArrayList<Move> optimalMoves = new ArrayList<Move>();

        // Translate the difficulty level [0, 1 or 2] to a tree depth [3, 5, 7].
        int depth = (this.getDifficulty() * 2) + 3;

        this._evaluations = 0;

        // Run mimimax and pass in pointer to optimalMoves, so that we can retrieve the optimal move.
        this._minimax(this._stateManager.getState(), depth, Integer.MIN_VALUE, Integer.MAX_VALUE, optimalMoves);

        // Display the amount of evaluations
        this._gui.setEvaluations(this._evaluations);

        // There will only be one optimal move to return
        return optimalMoves.get(0);
    }

    /**
     * Creates a search tree to identify the optimal move.
     * @param state The state to consider.
     * @param depth How many more search levels to consider.
     * @param alpha The best value that the maximising player can guarantee
     * @param beta The best value that the minimising player guarantee
     * @param optimalMoves An array to store the optimal move for the initial call
     * @return An integer representing the value of {@code state}.
     */
    private int _minimax(State state, int depth, int alpha, int beta, @Nullable ArrayList<Move> optimalMoves) {
        // Get the successor states
        ArrayList<Move> successors = state.getSuccessors();

        // If we have reached our depth limit or there are no successors then return the current state value
        if(depth < 1 || successors.isEmpty()) {
            this._evaluations++;
            return StateManager.getStateValue(state);
        }

        // Initialise the best value to be worst case scenario for the player: -inf for human player and +inf for AI
        int bestValue = state.getTurn() ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for(Move m: successors) {
            // Evaluate score for this successor, passing down alpha & beta values. Note that here optimalMoves is set
            // to null as we don't care to store optimal moves from recursive calls
            int eval = this._minimax(m.getNext(), depth-1, alpha, beta, null);

            this._evaluations++;

            // If state is for the human player and the evaluated score is greater than our best OR if state is for the
            // AI and the evaluated score is less than our best, then update best value (and store move if it's the
            // initial method call).
            if((state.getTurn() && eval > bestValue) || (!state.getTurn() && eval < bestValue)) {
                if(optimalMoves != null) {
                    // We clear the array because there should only ever be one element in the array
                    optimalMoves.clear();
                    optimalMoves.add(m);
                }

                bestValue = eval;
            }

            if(state.getTurn()) {
                alpha = Math.max(alpha, bestValue);
            }
            else {
                beta = Math.min(beta, bestValue);
            }

            // If alpha is greater or equal to beta, then we can stop considering moves at this level, we can
            // guarantee these branches will never be evaluated anyway
            if(alpha >= beta) {
                break;
            }
        }

        return bestValue;
    }

    /**
     * @param state A state to be added to the history stack.
     */
    private void _addToHistory(State state) {
        this._history.add(state);
    }

    /**
     * @return Whether the game is over.
     */
    public boolean isGameOver() {
        return this._gameOver;
    }

    /**
     * @return Whether it is possible to undo an more states.
     */
    public boolean canUndo() {
        // The history must be 2 or more, as we never want to remove the initial state
        return this._history.size() > 1;
    }

    /**
     * Undoes a single state.
     * @param shouldUpdateGUI Whether the GUI should be updated to represent the "new" state.
     * @return The state that was undone.
     */
    public State undo(boolean shouldUpdateGUI) {
        State undoneState = null;

        if(this.canUndo()) {
            undoneState = this._history.pop();

            if (shouldUpdateGUI) {
                this.updateState(this._history.peek(), null, false);
            }
        }

        return undoneState;
    }

    /**
     * Undoes multiple states until we have removed {@code state}.
     * @param state The state we want to undo to.
     * @return The number of states that were undone.
     */
    public int undoStateTo(State state) {
        boolean removed = false;
        State popped = null;
        int count = 0;

        do {
            popped = this.undo(false);
            removed = popped == state;
            count++;
        } while(!removed && popped != null);

        // Update the state to be the most recent on the history stack, we want successors to be evaluated so null
        // is passed, but we don't want a new history item added (it's already in the history stack).
        this.updateState(this._history.peek(), null, false);

        return count;
    }

    /**
     * Updates the GUI to represent the current game state.
     * @param successors The successor moves that should be offered.
     */
    private void _updateGUI(ArrayList<Move> successors) {
        this._gui.render(this._stateManager.getState(), successors, this);
    }
}
