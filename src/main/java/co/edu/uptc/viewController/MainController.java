package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import co.edu.uptc.model.*;
import co.edu.uptc.controller.*;

import java.io.File;
import java.util.*;

/**
 * Controlador principal de la aplicación que maneja la interacción de la interfaz
 * con la lógica de negocio del grafo y el cálculo de rutas.
 * Permite cargar y guardar grafos, agregar estaciones y conexiones, calcular rutas
 * y cambiar el idioma de la interfaz.
 */
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

    @FXML private Button btnEditStation;
    @FXML private Button btnDeleteStation;
    @FXML private Button btnEditConnection;
    @FXML private Button btnDeleteConnection;
    
    @FXML private Label lblStationId;
    @FXML private Label lblStationName;

    @FXML private Label lblConnFrom;
    @FXML private Label lblConnTo;
    @FXML private Label lblConnDistance;

    @FXML private TextField stationIdEditField;
    @FXML private TextField stationNameEditField;

    @FXML private TextField connFromEditField;
    @FXML private TextField connToEditField;
    @FXML private TextField connDistanceEditField;

    @FXML private ComboBox<String> cmbSearchType;
    @FXML private ComboBox<String> cmbLanguage;

    private GraphController graphController = new GraphController();
    private RouteController routeController = new RouteController(graphController);

    private final String DEFAULT_XML_PATH = "src/main/resources/co/edu/uptc/network_example.xml";

    private Locale currentLocale = Locale.getDefault();
    private ResourceBundle bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", currentLocale);

    private Stage stage;

    /**
     * Asigna la referencia al Stage principal de la aplicación.
     * @param stage el Stage principal
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        updateTexts();
    }

    /**
     * Inicializa la interfaz, carga el grafo por defecto y configura ComboBoxes de idioma y búsqueda.
     */
    @FXML
    private void initialize() {
        loadGraphFromFile(DEFAULT_XML_PATH);

        cmbLanguage.getItems().addAll("Español", "English", "Français");
        cmbLanguage.setValue(currentLocale.getLanguage().equals("es") ? "Español" : "English");
        cmbLanguage.setOnAction(event -> onLanguageSelected());

        cmbSearchType.getItems().addAll(bundle.getString("search.time"), bundle.getString("search.transbords"));
        cmbSearchType.getSelectionModel().selectFirst();

        updateTexts();
    }

    /**
     * Abre un diálogo para que el usuario cargue un archivo XML y cargue el grafo.
     */
    @FXML
    private void onLoadGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("graph.load.dialog"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos XML", "*.xml"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                graphController.loadGraph(selectedFile.getAbsolutePath());
                outputArea.setText(bundle.getString("graph.loaded") + "\n" +
                                   bundle.getString("graph.nodes") + ": " + graphController.getAllNodes().size());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, bundle.getString("graph.load.error") + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            outputArea.setText(bundle.getString("graph.load.cancelled"));
        }
    }

    /**
     * Carga un grafo desde un archivo específico. Mantiene la salida en outputArea.
     * @param path ruta del archivo XML
     */
    private void loadGraphFromFile(String path) {
        try {
            graphController.loadGraph(path);
            outputArea.setText(bundle.getString("graph.loaded") +
                    "\n" + bundle.getString("graph.nodes") + ": " + graphController.getAllNodes().size());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("graph.load.error") + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Abre un diálogo para guardar el grafo actual en un archivo XML.
     */
    @FXML
    private void onSaveGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("graph.save.dialog"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos XML", "*.xml"));
        fileChooser.setInitialFileName("graph.xml");

        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            try {
                graphController.saveGraph(selectedFile.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, bundle.getString("graph.saved") + ": " + selectedFile.getAbsolutePath());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, bundle.getString("graph.save.error") + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            outputArea.setText(bundle.getString("graph.save.cancelled"));
        }
    }

    /**
     * Muestra todos los nodos del grafo en outputArea.
     */
    @FXML
    private void onShowNodes() {
        StringBuilder sb = new StringBuilder(bundle.getString("graph.nodes.list") + ":\n");
        for (Node n : graphController.getAllNodes()) {
            sb.append("• ").append(n.getId()).append(" - ").append(n.getName()).append("\n");
        }
        outputArea.setText(sb.toString());
    }

    /**
     * Agrega una estación al grafo. Muestra alertas si hay errores.
     */
    @FXML
    private void onAddStation() {
        String id = stationIdField.getText().trim();
        String name = stationNameField.getText().trim();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("station.enter.id"));
            return;
        }

        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("station.enter.name"));
            return;
        }

        boolean correct = graphController.addStation(id, name);
        if (!correct) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("station.exists") + ": " + id);
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, bundle.getString("station.added") + ": " + id + " - " + name);
        stationIdField.clear();
        stationNameField.clear();
    }

    /**
     * Agrega una conexión entre dos estaciones del grafo.
     * Muestra alertas según el resultado de la operación.
     */
    @FXML
    private void onAddConnection() {
        String from = connFromField.getText().trim();
        String to = connToField.getText().trim();
        String distStr = connDistanceField.getText().trim();

        if (from.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("connection.enter.from"));
            return;
        }

        if (to.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("connection.enter.to"));
            return;
        }

        if (distStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("connection.enter.distance"));
            return;
        }

        try {
            double distance = Double.parseDouble(distStr);
            AddConnectionResult result = graphController.addConnection(from, to, distance);

            switch (result) {
                case SUCCESS:
                    showAlert(Alert.AlertType.INFORMATION, bundle.getString("connection.added") + ": " + from + " → " + to + " (" + distance + ")");
                    connFromField.clear();
                    connToField.clear();
                    connDistanceField.clear();
                    break;
                case SAME_NODE:
                    showAlert(Alert.AlertType.ERROR, bundle.getString("connection.same"));
                    break;
                case NODE_NOT_FOUND:
                    showAlert(Alert.AlertType.ERROR, bundle.getString("connection.node.notfound"));
                    break;
                case INVALID_DISTANCE:
                    showAlert(Alert.AlertType.ERROR, bundle.getString("connection.distance.invalid"));
                    break;
                case ALREADY_EXISTS:
                    showAlert(Alert.AlertType.ERROR, bundle.getString("connection.already.exists"));
                    break;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.distance.invalid"));
        }
    }

    /**
     * Edita una estación existente.
     *
     * @FXML asociado a un botón en la UI.
     * Obtiene el ID y el nuevo nombre desde los campos de texto y actualiza la estación.
     * Muestra alertas según el resultado, usando i18n.
     */
    @FXML
    private void onEditStation() {
        String id = stationIdEditField.getText().trim();
        String newName = stationNameEditField.getText().trim();

        if (id.isEmpty() || newName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("station.enter.idname"));
            return;
        }

        boolean result = graphController.editStation(id, newName);
        if (result) {
            showAlert(Alert.AlertType.INFORMATION, bundle.getString("station.updated") + ": " + id + " → " + newName);
        } else {
            showAlert(Alert.AlertType.ERROR, bundle.getString("station.notfound") + ": " + id);
        }
    }

    /**
     * Elimina una estación existente.
     *
     * @FXML asociado a un botón en la UI.
     * Obtiene el ID desde el campo de texto y elimina la estación junto con sus conexiones.
     * Muestra alertas según el resultado, usando i18n.
     */
    @FXML
    private void onDeleteStation() {
        String id = stationIdEditField.getText().trim();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("station.enter.id"));
            return;
        }

        boolean result = graphController.deleteStation(id);
        if (result) {
            showAlert(Alert.AlertType.INFORMATION, bundle.getString("station.deleted") + ": " + id);
        } else {
            showAlert(Alert.AlertType.ERROR, bundle.getString("station.notfound") + ": " + id);
        }
    }

    /**
     * Edita la distancia de una conexión existente entre dos estaciones.
     *
     * @FXML asociado a un botón en la UI.
     * Obtiene origen, destino y nueva distancia desde los campos de texto y actualiza la conexión.
     * Muestra alertas según el resultado, usando i18n.
     */
    @FXML
    private void onEditConnection() {
        String from = connFromEditField.getText().trim();
        String to = connToEditField.getText().trim();
        String distStr = connDistanceEditField.getText().trim();

        if (from.isEmpty() || to.isEmpty() || distStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("connection.enter.all"));
            return;
        }

        try {
            double newDistance = Double.parseDouble(distStr);
            boolean result = graphController.editConnection(from, to, newDistance);

            if (result) {
                showAlert(Alert.AlertType.INFORMATION, bundle.getString("connection.updated") + ": " + from + " → " + to + " (" + newDistance + ")");
            } else {
                showAlert(Alert.AlertType.ERROR, bundle.getString("connection.notfound") + " / " + bundle.getString("connection.distance.invalid"));
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.distance.invalid"));
        }
    }

    /**
     * Elimina una conexión existente entre dos estaciones.
     *
     * @FXML asociado a un botón en la UI.
     * Obtiene origen y destino desde los campos de texto y elimina la conexión bidireccional.
     * Muestra alertas según el resultado, usando i18n.
     */
    @FXML
    private void onDeleteConnection() {
        String from = connFromEditField.getText().trim();
        String to = connToEditField.getText().trim();

        if (from.isEmpty() || to.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("connection.enter.fromto"));
            return;
        }

        boolean result = graphController.deleteConnection(from, to);
        if (result) {
            showAlert(Alert.AlertType.INFORMATION, bundle.getString("connection.deleted") + ": " + from + " → " + to);
        } else {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.notfound"));
        }
    }


    /**
     * Muestra un Alert de JavaFX con un mensaje específico.
     * @param type tipo de alerta (INFORMATION, WARNING, ERROR)
     * @param message mensaje a mostrar
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Calcula la ruta entre dos estaciones según el tipo de búsqueda seleccionado.
     */
    @FXML
    private void onCalculateRoute() {
        String fromId = fromField.getText().trim();
        String toId = toField.getText().trim();
        String selected = cmbSearchType.getValue();

        if (fromId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("route.enter.from"));
            return;
        }

        if (toId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("route.enter.to"));
            return;
        }

        RouteResult result;

        if (selected.equals(bundle.getString("search.time"))) {
            result = routeController.findShortestTimeRoute(fromId, toId);
        } else if (selected.equals(bundle.getString("search.transbords"))) {
            result = routeController.findFewestTransfers(fromId, toId);
        } else {
            showAlert(Alert.AlertType.ERROR, "Tipo de búsqueda no reconocido.");
            return;
        }

        if (result == null || result.getPath().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("route.notfound") + ": " + fromId + " → " + toId);
            return;
        }

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

    /**
     * Cambia el idioma de la interfaz según la selección del ComboBox.
     */
    @FXML
    private void onLanguageSelected() {
        String selected = cmbLanguage.getValue();
        if (selected.equals("Español")) currentLocale = new Locale("es", "ES");
        else if (selected.equals("Français")) currentLocale = Locale.FRENCH;
        else currentLocale = Locale.ENGLISH;

        bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages", currentLocale);
        updateTexts();
    }

    /**
     * Actualiza todos los textos de la interfaz según el idioma actual.
     */
    private void updateTexts() {
        // Labels principales
        lblAddStation.setText(bundle.getString("label.add.station"));
        lblAddConnection.setText(bundle.getString("label.add.connection"));
        lblRoute.setText(bundle.getString("label.route"));
        lblto.setText(bundle.getString("label.to"));
        lblLanguage.setText(bundle.getString("label.language"));

        // Prompt texts de estaciones
        stationIdField.setPromptText(bundle.getString("prompt.station.id"));
        stationNameField.setPromptText(bundle.getString("prompt.station.name"));

        // Prompt texts de conexiones
        connFromField.setPromptText(bundle.getString("prompt.connection.from"));
        connToField.setPromptText(bundle.getString("prompt.connection.to"));
        connDistanceField.setPromptText(bundle.getString("prompt.connection.distance"));

        // Prompt texts de rutas
        fromField.setPromptText(bundle.getString("prompt.route.from"));
        toField.setPromptText(bundle.getString("prompt.route.to"));

        // Botones generales
        btnLoadXml.setText(bundle.getString("button.load.xml"));
        btnSaveXml.setText(bundle.getString("button.save.xml"));
        btnShowNodes.setText(bundle.getString("button.show.nodes"));

        // Botones de agregar
        btnAddStation.setText(bundle.getString("button.add.station"));
        btnAddConnection.setText(bundle.getString("button.add.connection"));
        calculateRoute.setText(bundle.getString("button.calculate.route"));

        // Botones de editar y eliminar (estaciones)
        btnEditStation.setText(bundle.getString("button.edit.station"));
        btnDeleteStation.setText(bundle.getString("button.delete.station"));
        stationIdField.setPromptText(bundle.getString("prompt.station.id"));
        stationNameField.setPromptText(bundle.getString("prompt.station.name"));
        lblStationId.setText(bundle.getString("prompt.station.id"));
        lblStationName.setText(bundle.getString("prompt.station.name"));

        // Botones de editar y eliminar (conexiones)
        btnEditConnection.setText(bundle.getString("button.edit.connection"));
        btnDeleteConnection.setText(bundle.getString("button.delete.connection"));
        connFromField.setPromptText(bundle.getString("prompt.connection.from"));
        connToField.setPromptText(bundle.getString("prompt.connection.to"));
        lblConnFrom.setText(bundle.getString("prompt.connection.from"));
        lblConnTo.setText(bundle.getString("prompt.connection.to"));
        lblConnDistance.setText(bundle.getString("prompt.connection.distance"));

        // Tipo de búsqueda
        cmbSearchType.getItems().setAll(bundle.getString("search.time"), bundle.getString("search.transbords"));

        // Limpiar área de salida
        outputArea.clear();

        // Título de la ventana
        if (stage != null) stage.setTitle(bundle.getString("app.title"));
    }

}
