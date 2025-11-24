package co.edu.uptc.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import co.edu.uptc.controller.GraphController;
import co.edu.uptc.model.Edge;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ConnectionsController {

    @FXML private TableView<Edge> connectionsTable;
    @FXML private TableColumn<Edge, String> colFrom, colTo, colDistance, colTime;
    @FXML private ComboBox<String> cmbFrom, cmbTo;
    @FXML private TextField distanceField, searchField;

    private GraphController graphController;
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
        graphController = GraphController.getInstance();

        colFrom.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFromId()));
        colTo.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getToId()));
        colDistance.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getDistance())));
        
        refreshData();

        connectionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null){
                cmbFrom.setValue(newSel.getFromId());
                cmbTo.setValue(newSel.getToId());
                distanceField.setText(String.valueOf(newSel.getDistance()));
            }
        });
    }

    private void refreshData() {
        List<Edge> edges = graphController.getAllEdges();
        connectionsTable.getItems().setAll(edges);

        List<String> ids = graphController.getAllNodes()
                .stream()
                .map(n -> n.getId())
                .collect(Collectors.toList());

        cmbFrom.getItems().setAll(ids);
        cmbTo.getItems().setAll(ids);
    }

    @FXML
    private void onNewConnection() {
        clearForm();
        connectionsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void onDeleteConnection() {
        Edge sel = connectionsTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("error.select.row"));
            return;
        }

        graphController.deleteEdge(sel);
        refreshData();
        showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.connection.deleted"));
        clearForm();
    }

    @FXML
    private void onSearch() {
        String q = searchField.getText().trim().toLowerCase();
        List<Edge> edges = graphController.getAllEdges();

        if (!q.isEmpty()) {
            edges = edges.stream()
                    .filter(e ->
                            e.getFromId().toLowerCase().contains(q) ||
                            e.getToId().toLowerCase().contains(q)
                    )
                    .collect(Collectors.toList());
        }

        connectionsTable.getItems().setAll(edges);
    }

    @FXML
    private void onSaveConnection() {
        String from = cmbFrom.getValue();
        String to = cmbTo.getValue();
        String distS = distanceField.getText().trim();

        // === VALIDACIONES ===
        if (from == null || to == null || distS.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("error.fill.fields"));
            return;
        }

        if (from.equals(to)) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("error.same.station"));
            return;
        }

        double dist;
        try {
            dist = Double.parseDouble(distS);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("error.invalid.number"));
            return;
        }

        if (dist <= 0) {
            showAlert(Alert.AlertType.WARNING, bundle.getString("error.distance.invalid"));
            return;
        }

        // VERIFICAR SI YA EXISTE
        Edge existing = graphController.getEdge(from, to);

        if (existing != null) {
            // Confirmación de sobrescritura
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setHeaderText(null);
            confirm.setContentText(
                    bundle.getString("confirm.connection.overwrite")
                            + " (" + from + " → " + to + ")"
            );

            ButtonType btnYes = new ButtonType(bundle.getString("button.yes"), ButtonBar.ButtonData.YES);
            ButtonType btnNo = new ButtonType(bundle.getString("button.no"), ButtonBar.ButtonData.NO);

            confirm.getButtonTypes().setAll(btnYes, btnNo);

            confirm.showAndWait().ifPresent(button -> {
                if (button == btnYes) {
                    graphController.editEdge(from, to, dist);
                    refreshData();
                    showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.connection.updated"));
                    clearForm();
                }
            });

            return;
        }

        // CREAR NUEVA
        graphController.addEdge(new Edge(from, to, dist));
        refreshData();
        showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.connection.saved"));
        clearForm();
    }

    @FXML
    private void onCancelEdit(ActionEvent event) {
        clearForm();
        connectionsTable.getSelectionModel().clearSelection();
    }

    private void clearForm() {
        cmbFrom.setValue(null);
        cmbTo.setValue(null);
        distanceField.setText("");
    }

    private void showAlert(Alert.AlertType t, String txt) {
        Alert a = new Alert(t);
        a.setHeaderText(null);
        a.setContentText(txt);
        a.showAndWait();
    }
}