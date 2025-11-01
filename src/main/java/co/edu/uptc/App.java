package co.edu.uptc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/edu/uptc/view/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Planificador de Transporte Público");
        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(400);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
