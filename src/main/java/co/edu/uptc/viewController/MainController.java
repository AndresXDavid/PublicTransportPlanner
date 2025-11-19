package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MainController {

    @FXML private TabPane mainTabPane;
    @FXML private AnchorPane stationsContainer, connectionsContainer, routesContainer, mapContainer, settingsContainer;
    @FXML private ComboBox<Locale> cmbLocale;

    private ResourceBundle bundle;
    private Stage stage;

    public void postLoadInit(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        try {
            bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages",
                    Locale.getDefault(),
                    Thread.currentThread().getContextClassLoader());
        } catch (MissingResourceException mre) {
            // Fallback: usar un bundle mínimo en inglés o crear uno vacío para evitar NPE
            System.err.println("Resource bundle not found: co.edu.uptc.i18n.messages");
            bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", Locale.ENGLISH,
                    Thread.currentThread().getContextClassLoader());
        }

        // Cargar vistas en cada tab
        loadTabContent("/co/edu/uptc/view/StationsView.fxml", stationsContainer);
        loadTabContent("/co/edu/uptc/view/ConnectionsView.fxml", connectionsContainer);
        loadTabContent("/co/edu/uptc/view/RoutesView.fxml", routesContainer);
        loadTabContent("/co/edu/uptc/view/MapView.fxml", mapContainer);
        loadTabContent("/co/edu/uptc/view/SettingsView.fxml", settingsContainer);

        // Inicializar selector de idiomas
        cmbLocale.getItems().addAll(Locale.forLanguageTag("es"), Locale.forLanguageTag("en"));
        cmbLocale.setValue(Locale.getDefault());
        cmbLocale.setOnAction(e -> {
            Locale sel = cmbLocale.getValue();
            Locale.setDefault(sel);
            showInfo(bundle.getString("info.locale.changed"));
            // Nota: para aplicar el cambio en runtime necesitas recargar vistas o reiniciar.
        });
    }

    private void loadTabContent(String fxmlPath, AnchorPane container) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                showError(bundle != null ? bundle.getString("error.load.view") + ": " + fxmlPath
                        : "Error loading view (resource not found): " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource, bundle);
            // loader.setLocation(resource); // no es necesario si pasaste resource en constructor, pero no duele
            Node content = loader.load();
            container.getChildren().clear();
            container.getChildren().add(content);
            AnchorPane.setTopAnchor(content, 0.0);
            AnchorPane.setBottomAnchor(content, 0.0);
            AnchorPane.setLeftAnchor(content, 0.0);
            AnchorPane.setRightAnchor(content, 0.0);
        } catch (IOException e) {
            showError(bundle != null ? bundle.getString("error.load.view") + ": " + fxmlPath + "\n" + e.getMessage()
                    : "Error loading view: " + fxmlPath + "\n" + e.getMessage());
        }
    }

    @FXML
    private void onLoadGraph() {
        // Llamar a la persistencia (implementa el diálogo FileChooser si quieres)
        showInfo(bundle.getString("info.load.started"));
    }

    @FXML
    private void onSaveGraph() {
        showInfo(bundle.getString("info.save.started"));
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.initOwner(stage); // opcional: enlaza la alerta al stage principal
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.initOwner(stage);
        a.showAndWait();
    }
}