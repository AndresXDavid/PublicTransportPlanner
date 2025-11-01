package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import co.edu.uptc.model.*;
import co.edu.uptc.controller.*;

public class MainController {

    @FXML private TextArea outputArea;

    // Campos de estaciones
    @FXML private TextField stationIdField;
    @FXML private TextField stationNameField;

    // Campos de conexiones
    @FXML private TextField connFromField;
    @FXML private TextField connToField;
    @FXML private TextField connDistanceField;

    // Campos de ruta
    @FXML private TextField fromField;
    @FXML private TextField toField;

    private GraphController graphController = new GraphController();
    private RouteController routeController = new RouteController(graphController);

    private final String DEFAULT_XML_PATH = "src/main/resources/co/edu/uptc/network_example.xml";

    @FXML
    private void onLoadGraph() {
        try {
            graphController.loadGraph(DEFAULT_XML_PATH);
            outputArea.setText("✅ Grafo cargado correctamente.\nNodos: " + graphController.getAllNodes().size());
        } catch (Exception e) {
            outputArea.setText("❌ Error cargando grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveGraph() {
        try {
            graphController.saveGraph(DEFAULT_XML_PATH);
            outputArea.setText("✅ Grafo guardado correctamente en: " + DEFAULT_XML_PATH);
        } catch (Exception e) {
            outputArea.setText("❌ Error guardando grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onShowNodes() {
        StringBuilder sb = new StringBuilder("📌 Nodos del grafo:\n");
        for (Node n : graphController.getAllNodes()) {
            sb.append("• ").append(n.getId()).append(" - ").append(n.getName()).append("\n");
        }
        outputArea.setText(sb.toString());
    }

    @FXML
    private void onAddStation() {
        String id = stationIdField.getText().trim();
        String name = stationNameField.getText().trim();
        if (id.isEmpty() || name.isEmpty()) {
            outputArea.setText("⚠️ Ingresa ID y nombre de la estación.");
            return;
        }

        graphController.addStation(id, name);
        outputArea.setText("✅ Estación agregada: " + id + " - " + name);
        stationIdField.clear();
        stationNameField.clear();
    }

    @FXML
    private void onAddConnection() {
        String from = connFromField.getText().trim();
        String to = connToField.getText().trim();
        String distStr = connDistanceField.getText().trim();

        if (from.isEmpty() || to.isEmpty() || distStr.isEmpty()) {
            outputArea.setText("⚠️ Ingresa origen, destino y distancia.");
            return;
        }

        try {
            double distance = Double.parseDouble(distStr);
            graphController.addConnection(from, to, distance);
            outputArea.setText("✅ Conexión agregada: " + from + " → " + to + " (" + distance + ")");
            connFromField.clear();
            connToField.clear();
            connDistanceField.clear();
        } catch (NumberFormatException e) {
            outputArea.setText("⚠️ Distancia inválida.");
        }
    }

    @FXML
    private void onCalculateRoute() {
        String fromId = fromField.getText().trim();
        String toId = toField.getText().trim();
        if (fromId.isEmpty() || toId.isEmpty()) {
            outputArea.setText("⚠️ Ingresa ID de origen y destino.");
            return;
        }

        RouteResult result = routeController.findShortestRoute(fromId, toId);
        if (result == null || result.getPath().isEmpty()) {
            outputArea.setText("⚠️ No se encontró ruta entre " + fromId + " y " + toId);
        } else {
            outputArea.setText("🛤 Ruta encontrada:\n" + result.toString());
        }
    }
}
