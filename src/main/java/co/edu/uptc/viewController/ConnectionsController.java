package co.edu.uptc.viewController;

import javafx.beans.property.SimpleStringProperty;
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
     @FXML private TextField distanceField, timeField, searchField;

     private GraphController graphController;
     private ResourceBundle bundle;

     @FXML
     public void initialize() {
          bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
          graphController = GraphController.getInstance();

          colFrom.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFromId()));
          colTo.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getToId()));
          colDistance.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getDistance())));
          colTime.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getTime())));

          refreshData();
     }

     private void refreshData() {
          List<Edge> edges = graphController.getAllEdges();
          connectionsTable.getItems().setAll(edges);

          // rellenar comboboxes
          List<String> ids = graphController.getAllNodes().stream().map(n -> n.getId()).collect(Collectors.toList());
          cmbFrom.getItems().setAll(ids);
          cmbTo.getItems().setAll(ids);
     }

     @FXML
     private void onNewConnection() {
          clearForm();
     }

     @FXML
     private void onSaveConnection() {
          String from = cmbFrom.getValue();
          String to = cmbTo.getValue();
          String distS = distanceField.getText().trim();
          String timeS = timeField.getText().trim();

          if (from == null || to == null || distS.isEmpty()) {
               showAlert(Alert.AlertType.WARNING, bundle.getString("error.fill.fields"));
               return;
          }
          if (from.equals(to)) {
               showAlert(Alert.AlertType.WARNING, bundle.getString("error.same.station"));
               return;
          }
          try {
               double dist = Double.parseDouble(distS);
               double time = timeS.isEmpty() ? dist / graphController.getDefaultSpeed() : Double.parseDouble(timeS);
               graphController.addEdge(new Edge(from, to, dist, time));
               refreshData();
               showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.connection.saved"));
               clearForm();
          } catch (NumberFormatException ex) {
               showAlert(Alert.AlertType.WARNING, bundle.getString("error.invalid.number"));
          }
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
     }

     @FXML
     private void onSearch() {
          String q = searchField.getText().trim().toLowerCase();
          List<Edge> edges = graphController.getAllEdges();
          if (!q.isEmpty()) {
               edges = edges.stream().filter(e -> e.getFromId().toLowerCase().contains(q) || e.getToId().toLowerCase().contains(q)).collect(Collectors.toList());
          }
          connectionsTable.getItems().setAll(edges);
     }

     private void clearForm() {
          cmbFrom.setValue(null);
          cmbTo.setValue(null);
          distanceField.setText("");
          timeField.setText("");
     }

     private void showAlert(Alert.AlertType t, String txt) {
          Alert a = new Alert(t);
          a.setHeaderText(null);
          a.setContentText(txt);
          a.showAndWait();
     }
}