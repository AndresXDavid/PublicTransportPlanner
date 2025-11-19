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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(bundle.getString("file.filter.xml"), "*.xml"));

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(bundle.getString("file.filter.xml"), "*.xml"));
        fileChooser.setInitialFileName(bundle.getString("file.default.name"));

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
     * Valida si el ID es aceptable (sin espacios y no vacío).
     */
    private boolean isValidStationId(String id) {
        if (id == null) return false;
        id = id.trim();
        if (id.isEmpty()) return false;
        // No permitir espacios en el ID; permitir letras, números, guiones y guion bajo
        return id.matches("[A-Za-z0-9_-]+");
    }

    /**
     * Valida si el nombre de estación es aceptable (letras, espacios simples, tildes, ñ).
     */
    private boolean isValidStationName(String name) {
        if (name == null) return false;

        name = name.trim();

        if (name.isEmpty()) return false;

        // No permitir doble espacio
        if (name.contains("  ")) return false;

        // Solo letras (may/min), espacios, tildes y ñ
        if (!name.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) return false;

        return true;
    }


    /**
     * Agrega una estación al grafo. Muestra alertas si hay errores.
     */
    @FXML
    private void onAddStation() {
        String id = stationIdField.getText();
        String name = stationNameField.getText();

        // Debug en consola para ver los valores crudos
        System.out.println("[DEBUG] onAddStation called. raw id='" + id + "' raw name='" + name + "'");

        if (id != null) id = id.trim();
        if (name != null) name = name.trim();

        System.out.println("[DEBUG] onAddStation trimmed. id='" + id + "' name='" + name + "'");

        if (id == null || id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("station.enter.id"));
            System.out.println("[DEBUG] fail: id empty");
            return;
        }

        if (name == null || name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("station.enter.name"));
            System.out.println("[DEBUG] fail: name empty");
            return;
        }

        // Validar formato del ID: no espacios, solo letras/números/guiones/underscore
        if (!id.matches("[A-Za-z0-9_-]+")) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("id.invalid.pattern"));
            System.out.println("[DEBUG] fail: id invalid pattern");
            return;
        }

        // Validar formato del nombre: letras (tildes), espacios simples y Ñ
        if (!name.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("name.invalid.pattern"));
            System.out.println("[DEBUG] fail: name invalid pattern");
            return;
        }

        // Verificar si existe ID duplicado
        Node existingById = graphController.getNode(id);
        System.out.println("[DEBUG] existingById = " + (existingById == null ? "null" : existingById.toString()));
        if (existingById != null) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("station.exists") + ": " + id + "\n" + bundle.getString("station.exists.details"));
            System.out.println("[DEBUG] fail: duplicate id");
            return;
        }

        // Verificar si existe nombre duplicado usando la normalización del GraphController
        boolean nameExists = graphController.stationNameExists(name);
        System.out.println("[DEBUG] normalized nameExists = " + nameExists);

        // Preguntar al usuario si desea continuar cuando el nombre ya existe (pero ID distinto)
        boolean force = false;
        if (nameExists) {
            try {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                String title = bundle.containsKey("label.confirm") ? bundle.getString("label.confirm") : "Confirm";
                confirm.setTitle(title);
                confirm.setHeaderText(null);
                String confirmMsg = bundle.containsKey("station.name.exists.confirm")
                        ? bundle.getString("station.name.exists.confirm")
                        : "A station with a similar name already exists. Create anyway?";
                confirm.setContentText(confirmMsg + "\n" + id + " - " + name);
                ButtonType aceptar = new ButtonType(bundle.containsKey("button.yes") ? bundle.getString("button.yes") : "Sí, crear", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelar = new ButtonType(bundle.containsKey("button.cancel") ? bundle.getString("button.cancel") : "Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                confirm.getButtonTypes().setAll(aceptar, cancelar);

                System.out.println("[DEBUG] duplicate name found, asking user...");

                Optional<ButtonType> result = confirm.showAndWait();
                if (!result.isPresent() || result.get() == cancelar) {
                        System.out.println("[DEBUG] user cancelled creation due to duplicate name");
                        showAlert(Alert.AlertType.INFORMATION, bundle.getString("station.add.cancelled"));
                        return;
                    }

                System.out.println("[DEBUG] user accepted duplicate name, continuing...");
                force = true;
            } catch (Exception e) {
                // Log and show friendly error instead of letting the exception bubble
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, bundle.getString("error.confirm.dialog") + ": " + e.getMessage());
                return;
            }
        }

        // Si todo está bien, agregar la estación (pasar 'force' según confirmación)
        boolean correct = graphController.addStation(id, name, force);
        System.out.println("[DEBUG] addStation returned: " + correct);
        if (!correct) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("addstation.internal.error"));
            System.out.println("[DEBUG] fail: addStation returned false");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, bundle.getString("station.added") + ": " + id + " - " + name);
        stationIdField.clear();
        stationNameField.clear();
        System.out.println("[DEBUG] station added successfully");
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

        // Validar formato de IDs (evita espacios en IDs)
        if (!isValidStationId(from)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("id.invalid.pattern"));
            return;
        }
        if (!isValidStationId(to)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("id.invalid.pattern"));
            return;
        }

        // Verificar existencia de nodos antes de intentar la conexión
        if (graphController.getNode(from) == null) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.origin.notfound") + " " + from);
            return;
        }
        if (graphController.getNode(to) == null) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.dest.notfound") + " " + to);
            return;
        }

        // Parsear y validar distancia
        double distance;
        try {
            distance = Double.parseDouble(distStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.distance.invalid"));
            return;
        }

        if (distance <= 0) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.distance.invalid"));
            return;
        }

        // Evitar conexión a sí mismo
        if (from.equalsIgnoreCase(to)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.same"));
            return;
        }

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
                showAlert(Alert.AlertType.WARNING, bundle.getString("connection.already.exists"));
                break;
            default:
                showAlert(Alert.AlertType.ERROR, bundle.getString("connection.add.error"));
                break;
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

        if (!isValidStationId(id)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("id.invalid.pattern"));
            return;
        }

        if (!isValidStationName(newName)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("name.invalid.pattern"));
            return;
        }

        Node node = graphController.getNode(id);
        if (node == null) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("station.notfound") + ": " + id);
            return;
        }

        // Evitar duplicar nombre con otro nodo
        for (Node n : graphController.getAllNodes()) {
            if (!n.getId().equals(id) && n.getName() != null && n.getName().equalsIgnoreCase(newName)) {
                showAlert(Alert.AlertType.ERROR, bundle.getString("editstation.duplicate.name") + ": \"" + n.getName() + "\".");
                return;
            }
        }

        boolean result = graphController.editStation(id, newName);
        if (result) {
            showAlert(Alert.AlertType.INFORMATION, bundle.getString("station.updated") + ": " + id + " → " + newName);
        } else {
            showAlert(Alert.AlertType.ERROR, bundle.getString("editstation.internal.error"));
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

        if (!isValidStationId(id)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("id.invalid.pattern"));
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

        if (!isValidStationId(from) || !isValidStationId(to)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("ids.invalid.pattern"));
            return;
        }

        if (graphController.getNode(from) == null) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.origin.notfound") + " " + from);
            return;
        }
        if (graphController.getNode(to) == null) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.dest.notfound") + " " + to);
            return;
        }

        double newDistance;
        try {
            newDistance = Double.parseDouble(distStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.distance.invalid"));
            return;
        }

        if (newDistance <= 0) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.distance.invalid"));
            return;
        }

        boolean result = graphController.editConnection(from, to, newDistance);

        if (result) {
            showAlert(Alert.AlertType.INFORMATION, bundle.getString("connection.updated") + ": " + from + " → " + to + " (" + newDistance + ")");
        } else {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.update.notfound") + ": " + from + " - " + to);
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
        String from = connFromField.getText().trim();
        String to = connToField.getText().trim();

        if (from.isEmpty() || to.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("connection.enter.fromto"));
            return;
        }

        if (!isValidStationId(from) || !isValidStationId(to)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("ids.invalid.pattern"));
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
        String title;
        if (type == Alert.AlertType.ERROR) title = bundle.getString("label.error");
        else if (type == Alert.AlertType.WARNING) title = bundle.getString("label.warning");
        else title = bundle.getString("label.info");
        alert.setTitle(title);
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

        // Validar IDs y existencia antes de consultar el RouteController
        if (!isValidStationId(fromId)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("id.invalid.pattern"));
            return;
        }
        if (!isValidStationId(toId)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("id.invalid.pattern"));
            return;
        }

        if (graphController.getNode(fromId) == null) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.origin.notfound") + " " + fromId);
            return;
        }
        if (graphController.getNode(toId) == null) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("connection.dest.notfound") + " " + toId);
            return;
        }

        RouteResult result;

        if (selected.equals(bundle.getString("search.time"))) {
            result = routeController.findShortestTimeRoute(fromId, toId);
        } else if (selected.equals(bundle.getString("search.transbords"))) {
            result = routeController.findFewestTransfers(fromId, toId);
        } else {
            showAlert(Alert.AlertType.ERROR, bundle.getString("route.type.invalid"));
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
        if (selected.equals("Español")) currentLocale = Locale.forLanguageTag("es-ES");
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