import javafx.animation.FillTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI {
    @FXML
    public Button newGameButton;

    @FXML
    public Button undoButton;

    @FXML
    public MenuItem difficultyEasyButton;

    @FXML
    public MenuItem difficultyMediumButton;

    @FXML
    public MenuItem difficultyHardButton;

    @FXML
    public Button hintsButton;

    @FXML
    public Button rulesButton;

    @FXML
    public Text evaluations;

    @FXML
    public GridPane pieces;

    @FXML
    public GridPane checkers;

    @FXML
    public ScrollPane historyScroll;

    @FXML
    public VBox history;

    private boolean _isShowingHints = true;
    private ArrayList<Circle> _options;

    /**
     * Removes all pieces.
     */
    public void resetPieces() {
        this.pieces.getChildren().clear();
    }

    /**
     * Removes all item items.
     */
    public void resetHistory() {
        this.history.getChildren().clear();
    }

    /**
     * Renders the GUI into its initial state.
     * @param state The initial state.
     * @param reset Whether or not this is a reset ("New game").
     * @param controller The controller
     */
    public void setup(State state, boolean reset, Controller controller) {
        this.resetPieces();
        this.resetHistory();

        if(!reset) {
            // Add styling options
            HBox.setHgrow(this.historyScroll, Priority.ALWAYS);
            this.historyScroll.setFitToWidth(true);
            this.historyScroll.setFitToHeight(true);
            this.historyScroll.setMaxHeight(696);
            this.history.getStyleClass().add("history");
            this.pieces.getStyleClass().add("hints");
        }

        // Add a starting history item to mark the start of the game.
        this.history.getChildren().add(this.createHistoryItem(Controller.Type.INFO, "Game started!", state, false, controller));

        if(!reset) {
            // Add button click handlers

            this.newGameButton.setOnAction(e -> {
                controller.setup(true);
            });

            this.undoButton.setOnAction(e -> {
                if (controller.canUndo()) {
                    boolean gameOver = controller.isGameOver();

                    // Undo the AI's move
                    controller.undo(false);

                    // Undo human move
                    controller.undo(true);

                    // If the game is over, then we've added an extra history item which will need to also go
                    if (gameOver) {
                        this._removeHistoryItems(3);
                    } else {
                        this._removeHistoryItems(2);
                    }
                }
            });
            this.undoButton.setDisable(!controller.canUndo());

            this._manageDifficultyButtons(controller);
            this.difficultyEasyButton.setOnAction(e -> {
                controller.setDifficulty(0);
                this._manageDifficultyButtons(controller);
            });

            this.difficultyMediumButton.setOnAction(e -> {
                controller.setDifficulty(1);
                this._manageDifficultyButtons(controller);
            });

            this.difficultyHardButton.setOnAction(e -> {
                controller.setDifficulty(2);
                this._manageDifficultyButtons(controller);
            });

            this.hintsButton.setOnAction(e -> {
                this._isShowingHints = !this._isShowingHints;
                if(this._isShowingHints) {
                    this.hintsButton.setText("Hide hints");
                    this.pieces.getStyleClass().add("hints");
                }
                else {
                    this.hintsButton.setText("Show hints");
                    this.pieces.getStyleClass().remove("hints");
                }
            });

            this.rulesButton.setOnAction(e -> {
                Alert rulesAlert = new Alert(Alert.AlertType.INFORMATION);
                rulesAlert.setHeaderText("Rules");

                VBox rulesBox = new VBox();
                rulesBox.getChildren().add(new Text("\t- Black always plays first."));
                rulesBox.getChildren().add(new Text("\t- Regular pieces can only move forward."));
                rulesBox.getChildren().add(new Text("\t- Pieces only ever more diagonally."));
                rulesBox.getChildren().add(new Text("\t- A regular piece becomes a king when they reach the end of the board."));
                rulesBox.getChildren().add(new Text("\t- A king piece can move forward or backwards."));
                rulesBox.getChildren().add(new Text("\t- A regular move is a movement of a piece into an adjacent empty cell."));
                rulesBox.getChildren().add(new Text("\t- A capturing move is a jump, where there is an adjacent enemy piece with an empty tile behind it."));
                rulesBox.getChildren().add(new Text("\t- A capturing move may to multiple other capturing moves within the same turn."));
                rulesBox.getChildren().add(new Text("\t- If capturing moves exist, the player must make a capturing move. This is called a forced capture."));
                rulesBox.getChildren().add(new Text("\t- The game is won when one player captures all enemy pieces, or when the enemy can not move any remaining pieces."));

                rulesAlert.getDialogPane().setContent(rulesBox);
                rulesAlert.showAndWait();
            });

            // Add checkers and pieces
            this.pieces.getStyleClass().add("pieces");
            this.pieces.setHgap(17);
            this.pieces.setVgap(17);

            for (int i = 0; i < 8; i++) {
                ColumnConstraints piecesColumnConstraint = new ColumnConstraints();
                piecesColumnConstraint.setPercentWidth(12.5);
                this.pieces.getColumnConstraints().add(piecesColumnConstraint);

                RowConstraints piecesRowConstraint = new RowConstraints();
                piecesRowConstraint.setPercentHeight(12.5);
                this.pieces.getRowConstraints().add(piecesRowConstraint);
            }

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    // Add checker
                    Rectangle square = new Rectangle();
                    square.setWidth(87);
                    square.setHeight(87);
                    GridPane.setRowIndex(square, i);
                    GridPane.setColumnIndex(square, j);

                    if ((i + j) % 2 == 0) {
                        square.setFill(Color.rgb(34, 47, 62));
                    } else {
                        square.setFill(Color.rgb(87, 101, 116));
                    }

                    this.checkers.getChildren().add(square);
                }
            }
        }
    }

    /**
     * Updates the GUI and renders the given state and lays out any potential following moves.
     * @param state The state to render.
     * @param successors Any potential following moves.
     * @param controller The controller.
     */
    public void render(State state, ArrayList<Move> successors, Controller controller) {
        this.resetPieces();
        this._options = new ArrayList<Circle>();

        // Iterate each piece and add to board
        for(PieceState s: state.getPieces()) {
            if(s.isActive()) {
                // Add piece button
                Color colour;

                if(state.getPieces().indexOf(s) < 12) {
                   colour = Color.rgb(255, 159, 67);
                }
                else {
                    colour = Color.rgb(34, 34, 34);
                }

                StackPane pieceButton = this.createPieceButton(s.getX(), s.getY(), s.isKing(), colour, controller);
                this.pieces.getChildren().add(pieceButton);
            }
        }

        // Add invalid buttons to all other tiles
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(this.pieces.lookup("#" + x + y) == null) {
                    Circle invalidCircle = this.createInValidButton(x, y);
                    this.pieces.getChildren().add(invalidCircle);
                }
            }
        }

        // Get all moves group by the piece they move (eg. where we want to place an option button)
        HashMap<PieceState, ArrayList<Move>> group = GUI.groupBySharedPath(successors, null);

        for(Map.Entry<PieceState, ArrayList<Move>> entry: group.entrySet()) {
            PieceState changedPiece = null;
            try {
                changedPiece = PieceState.identifyChangedPiece(entry.getValue().get(0).getPreviousMove().getCurrent().getPieces(), entry.getValue().get(0).getPreviousMove().getNext().getPieces())[1];
            }
            catch(NullPointerException e) {}

            if(!entry.getKey().equals(changedPiece)) {
                Move move = entry.getValue().get(0);
                PieceState piece = entry.getKey();

                Scene scene = this.pieces.getScene();
                PieceState originalPieceState = this._lookupOriginalPiece(move, scene);
                StackPane changedPiecePane = (StackPane) scene.lookup("#" + originalPieceState.getX() + originalPieceState.getY());

                // Add origin class to original piece
                changedPiecePane.getStyleClass().add("origin");

                Circle optionButton = this.createOptionButton(piece.getX(), piece.getY(), "" + originalPieceState.getX() + originalPieceState.getY(), move.getFirstMove().getCurrent(), move.getNext(), controller);
                this.pieces.getChildren().add(optionButton);
                this._options.add(optionButton);
            }
            else {
                Move move = entry.getValue().get(0).getPreviousMove();
                PieceState piece = entry.getKey();

                // Get the origin piece that started the move (for css hiding on click)
                Move firstMove = move.getFirstMove();
                PieceState originalPiece = PieceState.identifyChangedPiece(firstMove.getCurrent().getPieces(), firstMove.getNext().getPieces())[0];

                Circle optionButton = this.createSemiOptionButton(piece.getX(), piece.getY(), "" + originalPiece.getX() + originalPiece.getY(), move.getNext(), entry.getValue(), controller);
                this.pieces.getChildren().add(optionButton);
                this._options.add(optionButton);
            }
        }

        // Enable/disable undo button
        this.undoButton.setDisable(!controller.canUndo());
    }

    /**
     * Creates a new piece button
     * @param x The x location.
     * @param y The y location.
     * @param isKing Whether or not the piece is a king
     * @param colour The colour of the piece.
     * @param controller The controller.
     * @return A stack pane that contains the piece.
     */
    public StackPane createPieceButton(int x, int y, boolean isKing, Color colour, Controller controller) {
        StackPane pieceStack = new StackPane();

        Circle pieceButton = new Circle();
        pieceButton.setRadius(34);
        pieceButton.setFill(colour);

        pieceStack.getChildren().add(pieceButton);

        if(isKing) {
            Pane kingMarker = new Pane();
            kingMarker.getStyleClass().add("king");
            pieceStack.getChildren().add(kingMarker);
        }

        pieceStack.setId(x + "" + y);
        GridPane.setRowIndex(pieceStack, x);
        GridPane.setColumnIndex(pieceStack, y);

        pieceStack.setOnMouseClicked((event -> {
            this.onPieceClick(x, y);
        }));

        return pieceStack;
    }

    /**
     * Create an invalid button.
     * @param x The x location.
     * @param y The y location.
     * @return A invalid button
     */
    public Circle createInValidButton(int x, int y) {
        Circle invalidCircle = new Circle();
        invalidCircle.setRadius(34);
        invalidCircle.setFill(Color.TRANSPARENT);

        GridPane.setRowIndex(invalidCircle, x);
        GridPane.setColumnIndex(invalidCircle, y);

        invalidCircle.setOnMouseClicked((event -> {
            this.onInValidPieceClick(invalidCircle);
        }));

        return invalidCircle;
    }

    /**
     * Creates an option button that the user can click to update the state.
     * @param x The x location.
     * @param y The y location.
     * @param owner The ID of the original piece that will be moving.
     * @param state The current state.
     * @param newState The state that will be updated to.
     * @param controller The controller.
     * @return A option button.
     */
    public Circle createOptionButton(int x, int y, String owner, State state, State newState, Controller controller) {
        Circle pieceButton = new Circle();
        pieceButton.setRadius(34);
        pieceButton.setFill(Color.TRANSPARENT);
        pieceButton.getStyleClass().add("option");

        // Setting this allows us to show/hide paths using CSS
        pieceButton.getStyleClass().add(owner);

        GridPane.setRowIndex(pieceButton, x);
        GridPane.setColumnIndex(pieceButton, y);

        pieceButton.setOnMouseClicked((event -> {
            controller.updateState(newState, null, true);
        }));

        return pieceButton;
    }

    /**
     * Creates an option button for intermediate parts of multi-step moves.
     * @param x The x location.
     * @param y The y location.
     * @param owner The ID of the original piece that will be moving.
     * @param restrictedMoves An array of subsequent moves that the user can make from this position.
     * @param controller The controller.
     * @return A option button.
     */
    public Circle createSemiOptionButton(int x, int y, String owner, State newState, ArrayList<Move> restrictedMoves, Controller controller) {
        Circle pieceButton = new Circle();
        pieceButton.setRadius(34);
        pieceButton.setFill(Color.TRANSPARENT);
        pieceButton.getStyleClass().add("option");

        // Setting this allows us to show/hide paths using CSS
        pieceButton.getStyleClass().add(owner);

        GridPane.setRowIndex(pieceButton, x);
        GridPane.setColumnIndex(pieceButton, y);

        pieceButton.setOnMouseClicked(event -> {
            controller.updateState(newState, restrictedMoves, false);
        });

        return pieceButton;
    }

    /**
     * Returns a box to be added to the history pane.
     * @param type The type of item.
     * @param text The message to be added to the item.
     * @param state The state to be reverted to if canBeUndone.
     * @param canBeUndone Whether or not to display a rewind button for this item.
     * @param controller The controller.
     * @return A history item.
     */
    public HBox createHistoryItem(Controller.Type type, String text, State state, boolean canBeUndone, Controller controller) {
        HBox item = new HBox();
        item.getStyleClass().add("history__item");
        item.setAlignment(Pos.CENTER_LEFT);

        if(type != Controller.Type.INFO) {
            // If it's not an info item, it's a player item
            Circle icon = new Circle();
            icon.setRadius(10);

            if (type == Controller.Type.BLACK) {
                icon.setFill(Color.rgb(34, 34, 34));
            } else if (type == Controller.Type.RED) {
                icon.setFill(Color.rgb(255, 159, 67));
            }

            item.getChildren().add(icon);
        }

        item.getChildren().add(new Text(text));

        if(canBeUndone) {
            // If it can be undone, then add a rewind button that rolls back the state
            Pane rewindButton = new Pane();
            rewindButton.setPrefWidth(30);
            rewindButton.setPrefHeight(30);
            rewindButton.getStyleClass().add("rewind");

            rewindButton.setOnMouseClicked(event -> {
                this.onHistoryItemClick(event, state, controller);
            });

            item.getChildren().add(rewindButton);
            HBox.setHgrow(rewindButton, Priority.ALWAYS);
        }

        return item;
    }

    /**
     * Handles representing the end of a turn.
     * @param newState The upcoming state.
     * @param previousState The previous state.
     * @param controller The controller.
     */
    public void endOfTurn(State newState, State previousState, Controller controller) {
        // Get the changes to be added to a history item
        String message = PieceState.changesToString(previousState.getPieces(), newState.getPieces());

        Controller.Type type;
        Boolean canBeUndone;
        if(previousState.getTurn()) {
            type = Controller.Type.BLACK;
            canBeUndone = true;
        }
        else {
            type = Controller.Type.RED;
            canBeUndone = false;
        }

        HBox item = this.createHistoryItem(type, message, newState, canBeUndone, controller);
        this.history.getChildren().add(item);
        item.toBack();
    }

    /**
     * Handles when the game is over.
     * @param winningState The winning state.
     * @param previousState The previous state.
     * @param controller The controller.
     */
    public void gameOver(State winningState, State previousState, Controller controller) {
        String message;
        if(!winningState.getTurn()) {
            message = "Black";
        }
        else {
            message = "Red";
        }

        message += " won the game!";

        HBox gameoverItem = this.createHistoryItem(Controller.Type.INFO, message, previousState, false, controller);
        this.history.getChildren().add(gameoverItem);
        gameoverItem.toBack();
    }

    /**
     * Handles when a piece is clicked. Potential options will be highlighted if hint showing is enabled.
     * @param x The x location of the clicked piece.
     * @param y The y location of the clicked piece.
     */
    public void onPieceClick(int x, int y) {
        this._options.forEach((Circle c) -> { c.getStyleClass().remove("option--visible"); });

        // TODO: Is there a nice filter function available for this?
        this._options.forEach((Circle c) -> {
            if(c.getStyleClass().contains("" + x + y)) {
                c.getStyleClass().add("option--visible");
            }
        });
    }

    /**
     * Handles when an invalid tile is clicked and displays to the user that it is invalid.
     * @param invalidCircle The clicked circle.
     */
    public void onInValidPieceClick(Circle invalidCircle) {
        // Flash red
        FillTransition transition = new FillTransition(Duration.millis(300), invalidCircle, Color.TRANSPARENT, Color.rgb(255, 71, 87, 0.75));
        transition.setAutoReverse(true);
        transition.setCycleCount(2);
        transition.play();

        // Display a popup error
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid move");

        String message = "This is not a valid move!";

        if(!this._isShowingHints) {
            message += " If you are having trouble identifying valid moves, consider showing hints. See the \"Show hints\" button in the menu.";
        }

        alert.setContentText(message);
        alert.show();
    }

    /**
     * Handles when a history rewind button is clicked. The state is reverted.
     * @param event The click event.
     * @param state The state to roll back to.
     * @param controller The controller.
     */
    public void onHistoryItemClick(MouseEvent event, State state, Controller controller) {
        if (state != null) {
            boolean gameOver = controller.isGameOver();

            int numToRemove = controller.undoStateTo(state);

            // If the game is over, then we've added an extra history item which will need to also go
            if(gameOver) {
                numToRemove++;
            }

            this._removeHistoryItems(numToRemove);
        }
    }

    public void setEvaluations(int num) {
        this.evaluations.setText("Evaluations: " + num);
    }

    /**
     * Creates a hash map that groups all potential moves (include intermediate moves) indexed by the immediate origin piece.
     * This helps to solve the issue of overlapping intermediate multi-step moves. If multiple multi-step moves share an
     * intermediate tile, then by indexing the map with that tile, we can get the array of restricted moves from that tile!
     * For further information please see GUITest.java.
     * @param moves
     * @param previousMove
     * @return
     */
    public static HashMap<PieceState, ArrayList<Move>> groupBySharedPath(ArrayList<Move> moves, Move previousMove) {
        HashMap<PieceState, ArrayList<Move>> group = new HashMap<PieceState, ArrayList<Move>>();

        for(Move move: moves) {
            // Get the location of the start piece and the end piece (once moved)
            PieceState[] changedPieces = PieceState.identifyChangedPiece(move.getCurrent().getPieces(), move.getNext().getPieces());

            // Add a move to the end piece key. If it is a complete move then previousMove will be null and this will
            // be the only move added. If previousMove isn't null then this is a semiMove and part of a larger path
            Move addedMove;
            if(previousMove == null) {
                addedMove = move;
            }
            else {
                addedMove = previousMove;
            }

            group.computeIfAbsent(changedPieces[1], k -> new ArrayList<Move>()).add(addedMove);

            // If the move is part of a path then we must group those moves too
            if(move.getPreviousMove() != null) {
                // Create a one element array containing the parent of the previous move
                ArrayList<Move> parentMoveArray = new ArrayList<Move>() {{ add(move.getPreviousMove()); }};

                // Get groups from previous move
                HashMap<PieceState, ArrayList<Move>> newGroup = GUI.groupBySharedPath(parentMoveArray, move);

                // Add each of the groups to our grouping
                for(Map.Entry<PieceState, ArrayList<Move>> entry: newGroup.entrySet()) {
                    group.merge(entry.getKey(), entry.getValue(), (ArrayList<Move> oldMoves, ArrayList<Move> newMoves) -> {
                        oldMoves.addAll(newMoves);
                        return oldMoves;
                    });
                }
            }
        }

        return group;
    }

    /**
     * This method returns the origin piece for the given move.
     * @param move The move to find the origin piece for.
     * @param scene The scene.
     * @return The origin piece.
     */
    private PieceState _lookupOriginalPiece(Move move, Scene scene) {
        PieceState originalPiece = null;

        // move will be null once it has recursively reached the first move
        if(move != null) {
            // Recurse
            originalPiece = this._lookupOriginalPiece(move.getPreviousMove(), scene);

            // originalPiece will be null once we have reached the first move
            if (originalPiece == null) {
                // Attempt to find the board piece
                PieceState originalPieceState = PieceState.identifyChangedPiece(move.getCurrent().getPieces(), move.getNext().getPieces())[0];
                StackPane changedPiecePane = (StackPane) scene.lookup("#" + originalPieceState.getX() + originalPieceState.getY());

                // If there is no board piece then relinquish control to the above recurse level
                if(changedPiecePane == null) {
                    originalPiece = null;
                }
                else {
                    originalPiece = originalPieceState;
                }
            }
        }

        return originalPiece;
    }

    private void _removeHistoryItems(int numToRemove) {
        for(int i = 0; i < numToRemove; i++) {
            this.history.getChildren().remove(0);
        }
    }

    private void _manageDifficultyButtons(Controller controller) {
        this.difficultyEasyButton.setDisable(controller.getDifficulty() == 0);
        this.difficultyMediumButton.setDisable(controller.getDifficulty() == 1);
        this.difficultyHardButton.setDisable(controller.getDifficulty() == 2);
    }
}
