package main;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
        this.history.getChildren().add(this.newHistoryItem(0, "Hello world!"));

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

                // Add piece buttons
                Circle pieceButton = new Circle();
                pieceButton.setRadius(34);
                pieceButton.setFill(Color.TRANSPARENT);
                pieceButton.setId(i + "" + j);
                GridPane.setRowIndex(pieceButton, i);
                GridPane.setColumnIndex(pieceButton, j);

                final String coord = "[" + i + ", " + j + "]";
                pieceButton.setOnMouseClicked((event -> {
                    controller.onPieceClick(event, coord);
                }));

                this.pieces.getChildren().add(pieceButton);
            }

            // Add class to history pane
            this.history.getStyleClass().add("history");
        }
    }

    public HBox newHistoryItem(int playerId, String text) {
        HBox item = new HBox();
        item.getStyleClass().add("history__item");
        item.getChildren().add(new Text(text));
        return item;
    }
}
