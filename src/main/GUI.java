package main;

import javafx.fxml.FXML;
import javafx.scene.Node;
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

    public void setup(Controller controller) {
        HBox.setHgrow(this.historyScroll, Priority.ALWAYS);
        this.historyScroll.setFitToWidth(true);
        this.historyScroll.setFitToHeight(true);
        this.historyScroll.setMaxHeight(696);
        this.history.getStyleClass().add("history");
        this.history.getChildren().add(this.createHistoryItem(0, "Hello world!"));

        // Add items to toolbar
        this.toolbar.getItems().addAll(new ArrayList<Node>() {{
            add(new Button("New game"));
            add(new Button("Undo"));
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

    public void render(ArrayList<PieceState> state, ArrayList<ArrayList<PieceState>> successors, Controller controller) {
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

        final String coord = "[" + x + ", " + y + "]";
        pieceButton.setOnMouseClicked((event -> {
            controller.onPieceClick(event, coord);
        }));

        return pieceButton;
    }

    public HBox createHistoryItem(int playerId, String text) {
        HBox item = new HBox();
        item.getStyleClass().add("history__item");
        item.getChildren().add(new Text(text));
        return item;
    }
}
