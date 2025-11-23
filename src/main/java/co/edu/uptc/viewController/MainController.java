package co.edu.uptc.viewController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import co.edu.uptc.controller.GraphController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MainController {

    @FXML private TabPane mainTabPane;
    @FXML private AnchorPane dashboardContainer, stationsContainer, connectionsContainer, 
                              routesContainer, mapContainer, settingsContainer;
    @FXML private ComboBox<Locale> cmbLocale;
    @FXML private Button btnLoadXml, btnSaveXml;
    @FXML private Label lblLanguage;

    private ResourceBundle bundle;
    private Stage stage;
    private GraphController graphController;
    private DashboardController dashboardController;

    @FXML private Tab tabDashboard;
    @FXML private Tab tabStations;
    @FXML private Tab tabConnections;
    @FXML private Tab tabRoutes;
    @FXML private Tab tabMap;
    @FXML private Tab tabSettings;


    public void postLoadInit(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        loadBundle();
        graphController = GraphController.getInstance();
        autoLoadGraph();

        // 🔹 Cargar vistas y pasar referencia
        dashboardController = loadTabContentWithController("/co/edu/uptc/view/DashboardView.fxml", dashboardContainer);
        if (dashboardController != null) {
            dashboardController.setMainController(this);
        }

        loadTabContent("/co/edu/uptc/view/StationsView.fxml", stationsContainer);
        loadTabContent("/co/edu/uptc/view/ConnectionsView.fxml", connectionsContainer);
        loadTabContent("/co/edu/uptc/view/RoutesView.fxml", routesContainer);
        loadTabContent("/co/edu/uptc/view/MapView.fxml", mapContainer);
        loadTabContent("/co/edu/uptc/view/SettingsView.fxml", settingsContainer);

        setupLanguageSelector();
    }

    public void selectTab(String tabId) {
        switch (tabId) {
            case "dashboard":
                mainTabPane.getSelectionModel().select(tabDashboard);
                break;
            case "stations":
                mainTabPane.getSelectionModel().select(tabStations);
                break;
            case "connections":
                mainTabPane.getSelectionModel().select(tabConnections);
                break;
            case "routes":
                mainTabPane.getSelectionModel().select(tabRoutes);
                break;
            case "map":
                mainTabPane.getSelectionModel().select(tabMap);
                break;
            case "settings":
                mainTabPane.getSelectionModel().select(tabSettings);
                break;
        }
    }

    private void loadBundle() {
        try {
            bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages",
                    Locale.getDefault(),
                    Thread.currentThread().getContextClassLoader());
        } catch (MissingResourceException mre) {
            System.err.println("⚠️ Resource bundle not found, using English fallback");
            bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", Locale.ENGLISH,
                    Thread.currentThread().getContextClassLoader());
        }
    }

    private void setupLanguageSelector() {
        cmbLocale.getItems().addAll(
            new Locale("es", "ES"),
            new Locale("en", "US"),
            new Locale("fr", "FR")
        );
        
        cmbLocale.setConverter(new javafx.util.StringConverter<Locale>() {
            @Override
            public String toString(Locale locale) {
                if (locale == null) return "";
                switch (locale.getLanguage()) {
                    case "es": return "🇪🇸 Español";
                    case "en": return "🇺🇸 English";
                    case "fr": return "🇫🇷 Français";
                    default: return locale.getDisplayName();
                }
            }

            @Override
            public Locale fromString(String string) {
                return null;
            }
        });
        
        cmbLocale.setValue(Locale.getDefault());
        cmbLocale.setOnAction(e -> onLanguageChanged());
    }

    private void onLanguageChanged() {
        Locale selectedLocale = cmbLocale.getValue();
        if (selectedLocale == null) return;
        
        // Cambiar el locale por defecto
        Locale.setDefault(selectedLocale);
        
        // Crear diálogo de confirmación
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("🌍 " + getString("label.language"));
        confirmDialog.setHeaderText(getString("info.locale.changed"));
        confirmDialog.setContentText(getString("confirm.restart.app", 
            "¿Desea recargar la aplicación para aplicar los cambios?\n\nLa aplicación se recargará inmediatamente."));
        
        ButtonType reloadButton = new ButtonType("🔄 Recargar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("❌ Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        confirmDialog.getButtonTypes().setAll(reloadButton, cancelButton);
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == reloadButton) {
                reloadApplication();
            } else {
                // Restaurar el idioma anterior
                cmbLocale.setValue(Locale.getDefault());
            }
        });
    }

    private void reloadApplication() {
        try {
            // Recargar el ResourceBundle
            bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages",
                    Locale.getDefault(),
                    Thread.currentThread().getContextClassLoader());
            
            // Recargar todas las vistas
            dashboardController = loadTabContentWithController("/co/edu/uptc/view/DashboardView.fxml", dashboardContainer);
            loadTabContent("/co/edu/uptc/view/StationsView.fxml", stationsContainer);
            loadTabContent("/co/edu/uptc/view/ConnectionsView.fxml", connectionsContainer);
            loadTabContent("/co/edu/uptc/view/RoutesView.fxml", routesContainer);
            loadTabContent("/co/edu/uptc/view/MapView.fxml", mapContainer);
            loadTabContent("/co/edu/uptc/view/SettingsView.fxml", settingsContainer);
            
            // Actualizar título de la ventana
            if (stage != null) {
                stage.setTitle(bundle.getString("app.title"));
            }
            
            // Actualizar textos de los tabs
            updateTabTitles();

        } catch (Exception e) {
            showError("❌ Error al recargar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTabTitles() {
        // Este método actualiza los títulos de los tabs después del cambio de idioma
        // Los títulos se definen en MainView.fxml con % pero necesitamos refrescarlos
        Platform.runLater(() -> {
            try {
                mainTabPane.getTabs().get(0).setText("🏠 " + bundle.getString("tab.dashboard"));
                mainTabPane.getTabs().get(1).setText("📍 " + bundle.getString("tab.stations"));
                mainTabPane.getTabs().get(2).setText("🔗 " + bundle.getString("tab.connections"));
                mainTabPane.getTabs().get(3).setText("🧭 " + bundle.getString("tab.routes"));
                mainTabPane.getTabs().get(4).setText("🗺️ " + bundle.getString("tab.map"));
                mainTabPane.getTabs().get(5).setText("⚙️ " + bundle.getString("tab.settings"));
            } catch (Exception e) {
                System.err.println("Error actualizando títulos de tabs: " + e.getMessage());
            }
        });
    }

    private String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    private String getString(String key, String defaultValue) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }

    private void loadTabContent(String fxmlPath, AnchorPane container) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                String errorMsg = "❌ No se encontró: " + fxmlPath;
                System.err.println(errorMsg);
                showErrorInContainer(container, errorMsg);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(resource, bundle);
            Node content = loader.load();
            
            container.getChildren().clear();
            container.getChildren().add(content);
            AnchorPane.setTopAnchor(content, 0.0);
            AnchorPane.setBottomAnchor(content, 0.0);
            AnchorPane.setLeftAnchor(content, 0.0);
            AnchorPane.setRightAnchor(content, 0.0);
            
            System.out.println("✓ Vista cargada exitosamente: " + fxmlPath);
            
        } catch (IOException e) {
            String errorMsg = "❌ Error cargando: " + fxmlPath + "\n" + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            showErrorInContainer(container, errorMsg);
        }
    }

    private <T> T loadTabContentWithController(String fxmlPath, AnchorPane container) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("❌ No se encontró: " + fxmlPath);
                return null;
            }
            
            FXMLLoader loader = new FXMLLoader(resource, bundle);
            Node content = loader.load();
            
            container.getChildren().clear();
            container.getChildren().add(content);
            AnchorPane.setTopAnchor(content, 0.0);
            AnchorPane.setBottomAnchor(content, 0.0);
            AnchorPane.setLeftAnchor(content, 0.0);
            AnchorPane.setRightAnchor(content, 0.0);
            
            System.out.println("✓ Vista cargada exitosamente: " + fxmlPath);
            
            return loader.getController();
            
        } catch (IOException e) {
            System.err.println("❌ Error cargando: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    private void showErrorInContainer(AnchorPane container, String errorMsg) {
        Label errorLabel = new Label(errorMsg);
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        errorLabel.setWrapText(true);
        container.getChildren().clear();
        container.getChildren().add(errorLabel);
        AnchorPane.setTopAnchor(errorLabel, 20.0);
        AnchorPane.setLeftAnchor(errorLabel, 20.0);
        AnchorPane.setRightAnchor(errorLabel, 20.0);
    }

    @FXML
    private void onLoadGraph() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(bundle.getString("graph.load.dialog"));
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
            );
            
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                graphController.loadGraph(file.getAbsolutePath());
                showInfo("✅ " + bundle.getString("graph.loaded"));
                
                // Actualizar dashboard
                if (dashboardController != null) {
                    dashboardController.refresh();
                }
            } else {
                showInfo(bundle.getString("graph.load.cancelled"));
            }
            reloadApplication();
        } catch (Exception e) {
            showError(bundle.getString("graph.load.error") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void autoLoadGraph() {
        try {    
            File file = new File("src/main/resources/co/edu/uptc/network_example.xml");
            graphController.loadGraph(file.getAbsolutePath());
            
            // Actualizar dashboard
            if (dashboardController != null) {
                dashboardController.refresh();
            }
            reloadApplication();
        } catch (Exception e) {
            showError(bundle.getString("graph.load.error") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveGraph() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(bundle.getString("graph.save.dialog"));
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
            );
            fileChooser.setInitialFileName("transport_network.xml");
            
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                graphController.saveGraph(file.getAbsolutePath());
                showInfo("✅ " + bundle.getString("graph.saved"));
            } else {
                showInfo(bundle.getString("graph.save.cancelled"));
            }
        } catch (Exception e) {
            showError(bundle.getString("graph.save.error") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("❌ Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        if (stage != null) a.initOwner(stage);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("ℹ️ Información");
        a.setHeaderText(null);
        a.setContentText(msg);
        if (stage != null) a.initOwner(stage);
        a.showAndWait();
    }
}