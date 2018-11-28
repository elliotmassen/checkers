import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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

    private ArrayList<Circle> _options;

    public void resetPieces() {
        this.pieces.getChildren().clear();
    }

    public void resetHistory() {
        this.history.getChildren().clear();
    }

    public void setup(State state, boolean reset, Controller controller) {
        this.resetPieces();
        this.resetHistory();

        if(!reset) {
            HBox.setHgrow(this.historyScroll, Priority.ALWAYS);
            this.historyScroll.setFitToWidth(true);
            this.historyScroll.setFitToHeight(true);
            this.historyScroll.setMaxHeight(696);
            this.history.getStyleClass().add("history");
        }

        this.history.getChildren().add(this.createHistoryItem(Controller.Type.INFO, "Game started!", state, controller));

        if(!reset) {
            this.newGameButton.setOnAction(e -> {
                controller.setup(true);
            });

            this.undoButton.setOnAction(e -> {
                if (controller.canUndo()) {
                    boolean gameOver = controller.isGameOver();

                    controller.undo(true);

                    // If the game is over, then we've added an extra history item which will need to also go
                    if (gameOver) {
                        this._removeHistoryItems(2);
                    } else {
                        this._removeHistoryItems(1);
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

    public void render(State state, ArrayList<Move> successors, Controller controller) {
        this.resetPieces();
        this._options = new ArrayList<Circle>();

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

        this.undoButton.setDisable(!controller.canUndo());
    }

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

    public Circle createOptionButton(int x, int y, String owner, State state, State newState, Controller controller) {
        Circle pieceButton = new Circle();
        pieceButton.setRadius(34);
        pieceButton.setFill(Color.TRANSPARENT);
        pieceButton.getStyleClass().add("option");
        pieceButton.getStyleClass().add(owner);
        GridPane.setRowIndex(pieceButton, x);
        GridPane.setColumnIndex(pieceButton, y);

        pieceButton.setOnMouseClicked((event -> {
            controller.updateState(newState, null, true);
        }));

        return pieceButton;
    }

    public Circle createSemiOptionButton(int x, int y, String owner, State newState, ArrayList<Move> restrictedMoves, Controller controller) {
        Circle pieceButton = new Circle();
        pieceButton.setRadius(34);
        pieceButton.setFill(Color.TRANSPARENT);
        pieceButton.getStyleClass().add("option");
        pieceButton.getStyleClass().add(owner);
        GridPane.setRowIndex(pieceButton, x);
        GridPane.setColumnIndex(pieceButton, y);

        pieceButton.setOnMouseClicked(event -> {
            controller.updateState(newState, restrictedMoves, false);
        });

        return pieceButton;
    }

    public HBox createHistoryItem(Controller.Type type, String text, State state, Controller controller) {
        HBox item = new HBox();
        item.getStyleClass().add("history__item");
        item.setAlignment(Pos.CENTER_LEFT);

        if(type != Controller.Type.INFO) {
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

        if(type != Controller.Type.INFO) {
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

    public void endOfTurn(State newState, State previousState, Controller controller) {
        String message = PieceState.changesToString(previousState.getPieces(), newState.getPieces());

        Controller.Type type;
        if(previousState.getTurn()) {
            type = Controller.Type.BLACK;
        }
        else {
            type = Controller.Type.RED;
        }

        HBox item = this.createHistoryItem(type, message, newState, controller);
        this.history.getChildren().add(item);
        item.toBack();
    }

    public void gameOver(State winningState, State previousState, Controller controller) {
        String message;
        if(!winningState.getTurn()) {
            message = "Black";
        }
        else {
            message = "Red";
        }

        message += " won the game!";

        HBox gameoverItem = this.createHistoryItem(Controller.Type.INFO, message, previousState, controller);
        this.history.getChildren().add(gameoverItem);
        gameoverItem.toBack();
    }

    public void onPieceClick(int x, int y) {
        this._options.forEach((Circle c) -> { c.getStyleClass().remove("option--visible"); });

        // TODO: Is there a nice filter function available for this?
        this._options.forEach((Circle c) -> {
            if(c.getStyleClass().contains("" + x + y)) {
                c.getStyleClass().add("option--visible");
            }
        });
    }

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
