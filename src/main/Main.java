package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/main.fxml"));
        GUI gui = new GUI();
        loader.setController(gui);
        Parent root = loader.load();
        root.getStylesheets().add(getClass().getResource("../styles/main.css").toString());

        primaryStage.setTitle("Checkers");
        primaryStage.setScene(new Scene(root, 1080, 735));
        primaryStage.show();

        StateManager stateManager = new StateManager(StateManager.createTestState());
        Controller controller = new Controller(gui, stateManager);
        controller.setup();
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
