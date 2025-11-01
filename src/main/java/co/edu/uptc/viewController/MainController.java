package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import co.edu.uptc.model.*;
import co.edu.uptc.controller.*;

import java.util.Locale;
import java.util.ResourceBundle;

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

    // ResourceBundle para i18n
    private final ResourceBundle bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", Locale.getDefault());

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
}
