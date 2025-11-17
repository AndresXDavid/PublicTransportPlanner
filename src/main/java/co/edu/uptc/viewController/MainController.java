package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import co.edu.uptc.model.*;
import co.edu.uptc.controller.*;

import java.io.File;
import java.util.*;

public class MainController {

    @FXML private TextArea outputArea;

    @FXML private TextField stationIdField;
    @FXML private TextField stationNameField;
    @FXML private Label lblAddStation;

    @FXML private TextField connFromField;
    @FXML private TextField connToField;
    @FXML private TextField connDistanceField;
    @FXML private Label lblAddConnection;

    @FXML private TextField fromField;
    @FXML private TextField toField;
    @FXML private Label lblRoute;
    @FXML private Label lblto;

    @FXML private Button btnLoadXml;
    @FXML private Button btnSaveXml;
    @FXML private Button btnShowNodes;
    @FXML private Button btnAddStation;
    @FXML private Button btnAddConnection;
    @FXML private Button calculateRoute;
    @FXML private Label lblLanguage;

    // ComboBox de Busqueda de Ruta
    @FXML private ComboBox<String> cmbSearchType;

    // ComboBox de idiomas
    @FXML private ComboBox<String> cmbLanguage;

    private GraphController graphController = new GraphController();
    private RouteController routeController = new RouteController(graphController);

    private final String DEFAULT_XML_PATH = "src/main/resources/co/edu/uptc/network_example.xml";

    private Locale currentLocale = Locale.getDefault();
    private ResourceBundle bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", currentLocale);

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
        updateTexts();
    }

    @FXML
    private void initialize() {
        // Carga inicial con archivo por defecto
        loadGraphFromFile(DEFAULT_XML_PATH);

        // Configurar ComboBox de idiomas
        cmbLanguage.getItems().addAll("Español", "English", "Français");
        cmbLanguage.setValue(currentLocale.getLanguage().equals("es") ? "Español" : "English");

        cmbLanguage.setOnAction(event -> onLanguageSelected());

        cmbSearchType.getItems().addAll(
            bundle.getString("search.time"),
            bundle.getString("search.transbords")
        );

        cmbSearchType.getSelectionModel().selectFirst();

        updateTexts();
    }

    @FXML
    private void onLoadGraph() {
        // Crear el FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("graph.load.dialog")); // título del diálogo
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos XML", "*.xml")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                graphController.loadGraph(selectedFile.getAbsolutePath());
                outputArea.setText(bundle.getString("graph.loaded") + "\n" +
                                bundle.getString("graph.nodes") + ": " + graphController.getAllNodes().size());
            } catch (Exception e) {
                outputArea.setText(bundle.getString("graph.load.error") + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            outputArea.setText(bundle.getString("graph.load.cancelled"));
        }
    }

    // Carga un archivo dado, maneja errores
    private void loadGraphFromFile(String path) {
        try {
            graphController.loadGraph(path);
            outputArea.setText(bundle.getString("graph.loaded") + "\n" +
                            bundle.getString("graph.nodes") + ": " + graphController.getAllNodes().size());
        } catch (Exception e) {
            outputArea.setText(bundle.getString("graph.load.error") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("graph.save.dialog")); // título del diálogo
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos XML", "*.xml")
        );

        // Sugerir nombre por defecto
        fileChooser.setInitialFileName("graph.xml");

        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            try {
                graphController.saveGraph(selectedFile.getAbsolutePath());
                outputArea.setText(bundle.getString("graph.saved") + ": " + selectedFile.getAbsolutePath());
            } catch (Exception e) {
                outputArea.setText(bundle.getString("graph.save.error") + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            outputArea.setText(bundle.getString("graph.save.cancelled")); // opcional
        }
    }


    @FXML
    private void onShowNodes() {
        StringBuilder sb = new StringBuilder(bundle.getString("graph.nodes.list") + ":\n");
        for (Node n : graphController.getAllNodes()) {
            sb.append("• ").append(n.getId()).append(" - ").append(n.getName()).append("\n");
        }
        outputArea.setText(sb.toString());
    }

    @FXML
    private void onAddStation() {
        String id = stationIdField.getText().trim();
        String name = stationNameField.getText().trim();
        if (id.isEmpty()) {
            outputArea.setText(bundle.getString("station.enter.id"));
            return;
        }

        if (name.isEmpty()) {
            outputArea.setText(bundle.getString("station.enter.name"));
            return;
        }

        boolean correct = graphController.addStation(id, name);
        if (!correct) {
            outputArea.setText(bundle.getString("station.exists") + ": " + id);
            return;
        }
        outputArea.setText(bundle.getString("station.added") + ": " + id + " - " + name);
        stationIdField.clear();
        stationNameField.clear();
    }

    @FXML
    private void onAddConnection() {
        String from = connFromField.getText().trim();
        String to = connToField.getText().trim();
        String distStr = connDistanceField.getText().trim();

        if (from.isEmpty()) {
            outputArea.setText(bundle.getString("connection.enter.from"));
            return;
        }

        if (to.isEmpty()) {
            outputArea.setText(bundle.getString("connection.enter.to"));
            return;
        }

        if (distStr.isEmpty()) {
            outputArea.setText(bundle.getString("connection.enter.distance"));
            return;
        }

        try {
            double distance = Double.parseDouble(distStr);
            graphController.addConnection(from, to, distance);
            outputArea.setText(bundle.getString("connection.added") + ": " + from + " → " + to + " (" + distance + ")");
            connFromField.clear();
            connToField.clear();
            connDistanceField.clear();
        } catch (NumberFormatException e) {
            outputArea.setText(bundle.getString("connection.distance.invalid"));
        }
    }

    @FXML
    private void onCalculateRoute() {
        String fromId = fromField.getText().trim();
        String toId = toField.getText().trim();
        String selected = cmbSearchType.getValue();

        if (fromId.isEmpty()) {
            outputArea.setText(bundle.getString("route.enter.from"));
            return;
        }

        if (toId.isEmpty()) {
            outputArea.setText(bundle.getString("route.enter.to"));
            return;
        }

        RouteResult result;

        if (selected.equals(bundle.getString("search.time"))) {
            result = routeController.findShortestTimeRoute(fromId, toId);
        } else if (selected.equals(bundle.getString("search.transbords"))) {
            result = routeController.findFewestTransfers(fromId, toId);
        } else {
            outputArea.setText("Tipo de búsqueda no reconocido.");
            return;
        }           

        if (result == null || result.getPath().isEmpty()) {
            outputArea.setText(bundle.getString("route.notfound") + ": " + fromId + " → " + toId);
            return;
        }

        // ========================= Formatear salida con i18n =========================
        StringBuilder output = new StringBuilder();
        output.append(bundle.getString("route.found")).append(":\n");
        output.append(bundle.getString("label.route")).append(": ").append(result.getPathString());

        if (result.getType() == RouteResultType.DISTANCE || result.getType() == RouteResultType.BOTH) {
            output.append(" | ").append(bundle.getString("label.distance"))
                .append(": ").append(result.getTotalDistance());
        }

        if (result.getType() == RouteResultType.TRANSFERS || result.getType() == RouteResultType.BOTH) {
            output.append(" | ").append(bundle.getString("label.transfers"))
                .append(": ").append(result.getTransbords());
        }

        outputArea.setText(output.toString());
    }

    // ========================== SELECCIÓN DE IDIOMA ==========================
    @FXML
    private void onLanguageSelected() {
        String selected = cmbLanguage.getValue();
        if (selected.equals("Español")) {
            currentLocale = new Locale("es", "ES");
        }
        else if (selected.equals("Français")) {
            currentLocale = Locale.FRENCH;
        }
        else {
            currentLocale = Locale.ENGLISH;
        }
        bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", currentLocale);
        updateTexts();
    }

    private void updateTexts() {
        lblAddStation.setText(bundle.getString("label.add.station"));
        lblAddConnection.setText(bundle.getString("label.add.connection"));
        lblRoute.setText(bundle.getString("label.route"));
        lblto.setText(bundle.getString("label.to"));
        lblLanguage.setText(bundle.getString("label.language"));

        stationIdField.setPromptText(bundle.getString("prompt.station.id"));
        stationNameField.setPromptText(bundle.getString("prompt.station.name"));

        connFromField.setPromptText(bundle.getString("prompt.connection.from"));
        connToField.setPromptText(bundle.getString("prompt.connection.to"));
        connDistanceField.setPromptText(bundle.getString("prompt.connection.distance"));

        fromField.setPromptText(bundle.getString("prompt.route.from"));
        toField.setPromptText(bundle.getString("prompt.route.to"));

        btnLoadXml.setText(bundle.getString("button.load.xml"));
        btnSaveXml.setText(bundle.getString("button.save.xml"));
        btnShowNodes.setText(bundle.getString("button.show.nodes"));

        btnAddStation.setText(bundle.getString("button.add.station"));
        btnAddConnection.setText(bundle.getString("button.add.connection"));
        calculateRoute.setText(bundle.getString("button.calculate.route"));

        cmbSearchType.getItems().setAll(
            bundle.getString("search.time"),
            bundle.getString("search.transbords")
        );

        // Limpiar área de salida
        outputArea.clear();

        if (stage != null) {
            stage.setTitle(bundle.getString("app.title"));
        }
    }
}