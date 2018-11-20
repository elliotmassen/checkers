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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
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

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        HBox.setHgrow(this.historyScroll, Priority.ALWAYS);
        this.historyScroll.setFitToWidth(true);
        this.historyScroll.setFitToHeight(true);
        this.historyScroll.setMaxHeight(696);
        this.history.getStyleClass().add("history");
        this.history.getChildren().add(this._newHistoryItem(0, "Hello world!"));

        // Add items to toolbar
        this.toolbar.getItems().addAll(new ArrayList<Node>() {{
            add(new Button("New game"));
            add(new Button("Undo"));
            add(new Button("Show hints"));
            add(new Button("Rules"));
        }});

        // Add checkers and pieces
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
                Rectangle pieceButton = new Rectangle();
                pieceButton.setWidth(87);
                pieceButton.setHeight(87);
                pieceButton.setFill(Color.TRANSPARENT);
                GridPane.setRowIndex(pieceButton, i);
                GridPane.setColumnIndex(pieceButton, j);

                final String coord = "[" + i + ", " + j + "]";
                pieceButton.setOnMouseClicked((event -> {
                    HBox item = this._newHistoryItem(0, coord);
                    this.history.getChildren().add(item);
                    item.toBack();
                }));

                this.pieces.getChildren().add(pieceButton);
            }

            // Add class to history pane
            this.history.getStyleClass().add("history");
        }
    }

    private HBox _newHistoryItem(int playerId, String text) {
        HBox item = new HBox();
        item.getStyleClass().add("history__item");
        item.getChildren().add(new Text(text));
        return item;
    }
}
