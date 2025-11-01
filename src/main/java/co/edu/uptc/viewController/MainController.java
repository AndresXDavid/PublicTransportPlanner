package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import co.edu.uptc.model.*;
import co.edu.uptc.controller.*;

import java.util.*;

public class MainController {

    @FXML private TextArea outputArea;

    // Campos de estaciones
    @FXML private TextField stationIdField;
    @FXML private TextField stationNameField;
    @FXML private Label lblAddStation;

    // Campos de conexiones
    @FXML private TextField connFromField;
    @FXML private TextField connToField;
    @FXML private TextField connDistanceField;
    @FXML private Label lblAddConnection;

    // Campos de ruta
    @FXML private TextField fromField;
    @FXML private TextField toField;
    @FXML private Label lblRoute;
    @FXML private Label lblto;

    // Botones
    @FXML private Button btnLoadXml;
    @FXML private Button btnSaveXml;
    @FXML private Button btnShowNodes;
    @FXML private Button btnAddStation;
    @FXML private Button btnAddConnection;
    @FXML private Button calculateRoute;
    @FXML private Label lblLanguage;

    // ComboBox de idiomas
    @FXML private ComboBox<String> cmbLanguage;

    private GraphController graphController = new GraphController();
    private RouteController routeController = new RouteController(graphController);

    private final String DEFAULT_XML_PATH = "src/main/resources/co/edu/uptc/network_example.xml";

    // Idioma actual y ResourceBundle
    private Locale currentLocale = Locale.getDefault();
    private ResourceBundle bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", currentLocale);

    // Referencia al Stage
    private Stage stage;

    // ========================== CONFIGURACIÓN ==========================
    public void setStage(Stage stage) {
        this.stage = stage;
        updateTexts(); // Actualiza el título del stage la primera vez
    }

    @FXML
    private void initialize() {
        // Configurar ComboBox de idiomas
        cmbLanguage.getItems().addAll("Español", "English", "Français");
        cmbLanguage.setValue(currentLocale.getLanguage().equals("es") ? "Español" : "English");

        cmbLanguage.setOnAction(event -> onLanguageSelected());

        updateTexts();
    }

    // ========================== FUNCIONES DE GRAFO ==========================
    @FXML
    private void onLoadGraph() {
        try {
            graphController.loadGraph(DEFAULT_XML_PATH);
            outputArea.setText(bundle.getString("graph.loaded") + "\n" +
                               bundle.getString("graph.nodes") + ": " + graphController.getAllNodes().size());
        } catch (Exception e) {
            outputArea.setText(bundle.getString("graph.load.error") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveGraph() {
        try {
            graphController.saveGraph(DEFAULT_XML_PATH);
            outputArea.setText(bundle.getString("graph.saved") + ": " + DEFAULT_XML_PATH);
        } catch (Exception e) {
            outputArea.setText(bundle.getString("graph.save.error") + ": " + e.getMessage());
            e.printStackTrace();
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

    // ========================== FUNCIONES DE ESTACIONES ==========================
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

    // ========================== FUNCIONES DE CONEXIONES ==========================
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

    // ========================== FUNCIONES DE RUTA ==========================
    @FXML
    private void onCalculateRoute() {
        String fromId = fromField.getText().trim();
        String toId = toField.getText().trim();
        if (fromId.isEmpty()) {
            outputArea.setText(bundle.getString("route.enter.from"));
            return;
        }

        if (toId.isEmpty()) {
            outputArea.setText(bundle.getString("route.enter.to"));
            return;
        }

        RouteResult result = routeController.findShortestRoute(fromId, toId);
        if (result == null || result.getPath().isEmpty()) {
            outputArea.setText(bundle.getString("route.notfound") + ": " + fromId + " → " + toId);
        } else {
            outputArea.setText(bundle.getString("route.found") + ":\n" + result.toString());
        }
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

    // ========================== ACTUALIZACIÓN DE TEXTOS ==========================
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

        // Limpiar área de salida
        outputArea.clear();

        // Actualizar título del Stage
        if (stage != null) {
            stage.setTitle(bundle.getString("app.title"));
        }
    }
}
