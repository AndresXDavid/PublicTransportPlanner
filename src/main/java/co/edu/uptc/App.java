package co.edu.uptc;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import co.edu.uptc.viewController.MainController;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        ResourceBundle bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", Locale.getDefault());

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/edu/uptc/view/MainView.fxml"));
        loader.setResources(bundle);

        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.setWidth(500);
        stage.setHeight(400);

        MainController controller = loader.getController();
        controller.setStage(stage);

        stage.setTitle(bundle.getString("app.title"));

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}