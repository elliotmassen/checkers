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
        MainController main = new MainController();
        loader.setController(main);
        Parent root = loader.load();
        System.out.println(getClass().getResource("../styles/main.css").toString());
        root.getStylesheets().add(getClass().getResource("../styles/main.css").toString());

        primaryStage.setTitle("Checkers");
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
