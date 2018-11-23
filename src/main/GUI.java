package main;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class GUI {
    @FXML
    public ToolBar toolbar;

    @FXML
    public GridPane pieces;

    @FXML
    public GridPane checkers;

    @FXML
    public ScrollPane historyScroll;

    @FXML
    public VBox history;

    private ArrayList<Circle> _pieces;
    private ArrayList<Circle> _options;

    public void resetPieces() {
        this.pieces.getChildren().clear();
    }

    public void setup() {
        this.resetPieces();

        HBox.setHgrow(this.historyScroll, Priority.ALWAYS);
        this.historyScroll.setFitToWidth(true);
        this.historyScroll.setFitToHeight(true);
        this.historyScroll.setMaxHeight(696);
        this.history.getStyleClass().add("history");
        this.history.getChildren().add(this.createHistoryItem(Controller.Type.INFO, "Hello world!"));

        // Add items to toolbar
        this.toolbar.getItems().addAll(new ArrayList<Node>() {{
            add(new Button("New game"));
            add(new Button("Undo"));
            add(new Button("Difficulty"));
            add(new Button("Show hints"));
            add(new Button("Rules"));
        }});

        // Add checkers and pieces
        this.pieces.getStyleClass().add("pieces");
        this.pieces.setHgap(17);
        this.pieces.setVgap(17);

        for(int i = 0; i < 8; i++) {
            ColumnConstraints piecesColumnConstraint = new ColumnConstraints();
            piecesColumnConstraint.setPercentWidth(12.5);
            this.pieces.getColumnConstraints().add(piecesColumnConstraint);

            RowConstraints piecesRowConstraint = new RowConstraints();
            piecesRowConstraint.setPercentHeight(12.5);
            this.pieces.getRowConstraints().add(piecesRowConstraint);
        }

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                // Add checker
                Rectangle square = new Rectangle();
                square.setWidth(87);
                square.setHeight(87);
                GridPane.setRowIndex(square, i);
                GridPane.setColumnIndex(square, j);

                if((i + j) % 2 == 0) {
                    square.setFill(Color.rgb(34, 47, 62));
                }
                else {
                    square.setFill(Color.rgb(87, 101, 116));
                }

                this.checkers.getChildren().add(square);
            }

            // Add class to history pane
            this.history.getStyleClass().add("history");
        }
    }

    public void render(ArrayList<PieceState> state, ArrayList<Move> successors, Controller controller) {
        this.resetPieces();
        this._pieces = new ArrayList<Circle>();
        this._options = new ArrayList<Circle>();

        for(PieceState s: state) {
            if(s.isActive()) {
                // Add piece button
                Color colour;

                if(state.indexOf(s) < 12) {
                   colour = Color.rgb(255, 159, 67);
                }
                else {
                    colour = Color.rgb(34, 34, 34);
                }

                Circle pieceButton = this.createPieceButton(s.getX(), s.getY(), colour, controller);
                this.pieces.getChildren().add(pieceButton);
                this._pieces.add(pieceButton);
            }
        }

        // For each potential move, find the piece that has moved.
        for(Move m: successors) {
            PieceState changed = null;
            ArrayList<PieceState> nextMove = m.getNext();
            for(int i = 0; i < state.size(); i++) {
                // If the piece has moved (but not destroyed), add options
                if(!state.get(i).equals(nextMove.get(i)) && state.get(i).isActive() && nextMove.get(i).isActive()) {
                    // TODO: Is it no only one piece that will change in each move? Is the enclosing for loop neccessary?
                    changed = PieceState.identifyChangedPiece(m.getCurrent(), m.getFinalState());
                    Scene scene = this.pieces.getScene();
                    Circle changedPieceCircle = (Circle) scene.lookup("#" + changed.getX() + changed.getY());
                    changedPieceCircle.getStyleClass().add("origin");

                    String message = PieceState.changesToString(m.getCurrent(), m.getFinalState());
                    Circle optionButton = this.createOptionButton(nextMove.get(i).getX(), nextMove.get(i).getY(), message, "" + changed.getX() + changed.getY(), m.getFinalState(), controller);
                    this.pieces.getChildren().add(optionButton);
                    this._options.add(optionButton);
                }
            }

            // If the potential move is a jump (and therefore has potential following moves), add the options for those too
            for(Move following: m.getAllMoves()) {
                nextMove = following.getNext();
                for(int i = 0; i < state.size(); i++) {
                    // If the piece has moved (but not destroyed), add options
                    if(!state.get(i).equals(nextMove.get(i)) && state.get(i).isActive() && nextMove.get(i).isActive()) {
                        String message = PieceState.changesToString(m.getCurrent(), m.getFinalState());
                        Circle optionButton = this.createOptionButton(nextMove.get(i).getX(), nextMove.get(i).getY(), message, "" + changed.getX() + changed.getY(), m.getFinalState(), controller);
                        this.pieces.getChildren().add(optionButton);
                        this._options.add(optionButton);
                    }
                }
            }
        }
    }

    public Circle createPieceButton(int x, int y, Color colour, Controller controller) {
        Circle pieceButton = new Circle();
        pieceButton.setRadius(34);
        pieceButton.setFill(colour);
        pieceButton.setId(x + "" + y);
        GridPane.setRowIndex(pieceButton, x);
        GridPane.setColumnIndex(pieceButton, y);

        pieceButton.setOnMouseClicked((event -> {
            controller.onPieceClick(event, x, y, this._options);
        }));

        return pieceButton;
    }

    public Circle createOptionButton(int x, int y, String message, String owner, ArrayList<PieceState> newState, Controller controller) {
        Circle pieceButton = new Circle();
        pieceButton.setRadius(34);
        pieceButton.setFill(Color.TRANSPARENT);
        pieceButton.getStyleClass().add("option");
        pieceButton.getStyleClass().add(owner);
        GridPane.setRowIndex(pieceButton, x);
        GridPane.setColumnIndex(pieceButton, y);

        pieceButton.setOnMouseClicked((event -> {
            controller.onOptionClick(event, message, newState);
        }));

        return pieceButton;
    }

    public HBox createHistoryItem(Controller.Type type, String text) {
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
        return item;
    }
}
