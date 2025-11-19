package co.edu.uptc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import co.edu.uptc.viewController.MainController;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // --- 1. Cargar bundle de i18n ---
        ResourceBundle bundle = ResourceBundle.getBundle(
                "co.edu.uptc.i18n.messages",
                Locale.getDefault(),
                Thread.currentThread().getContextClassLoader()
        );

        // --- 2. Ubicación del FXML ---
        URL fxmlUrl = getClass().getResource("/co/edu/uptc/view/MainView.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("ERROR: No se encontró MainView.fxml en /co/edu/uptc/view/");
        }

        // --- 3. Crear loader con bundle ---
        FXMLLoader loader = new FXMLLoader(fxmlUrl, bundle);

        // --- 4. Cargar FXML (crea controlador automáticamente) ---
        Parent root = loader.load();

        // --- 5. Obtener controller inyectado por FXML ---
        MainController controller = loader.getController();

        // --- 6. Pasar Stage al controlador ---
        controller.postLoadInit(stage); // método recomendado (mejor que setStage)

        // --- 7. Crear escena ---
        Scene scene = new Scene(root);

        // --- 8. Cargar CSS si existe ---
        URL css = getClass().getResource("/co/edu/uptc/styles/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        // --- 9. Configurar stage ---
        stage.setScene(scene);
        stage.setTitle(bundle.getString("app.title"));
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}