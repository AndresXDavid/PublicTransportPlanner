package co.edu.uptc.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import co.edu.uptc.controller.GraphController;
import co.edu.uptc.validation.StationValidator;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StationsController {

     @FXML private TableView<co.edu.uptc.model.Node> stationsTable;
     @FXML private TableColumn<co.edu.uptc.model.Node, String> colId, colName, colLat, colLng;
     @FXML private TextField searchField, idField, nameField, latField, lngField;

     private GraphController graphController;
     private ResourceBundle bundle;

     @FXML
     public void initialize() {
          bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
          graphController = GraphController.getInstance(); // asumo singleton; ajusta si no es así

          // Column factories (ajusta nombres de propiedades si difieren)
          colId.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getId()));
          colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
          colLat.setCellValueFactory(cd -> {
               Double v = cd.getValue().getLatitude();
               return new SimpleStringProperty(v == null ? "" : String.valueOf(v));
          });
          colLng.setCellValueFactory(cd -> {
               Double v = cd.getValue().getLongitude();
               return new SimpleStringProperty(v == null ? "" : String.valueOf(v));
          });

          refreshTable();

          stationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
               if (newSel != null) loadToForm(newSel);
          });
     }

     private String originalId = null;
     
     private void loadToForm(co.edu.uptc.model.Node n) {
          idField.setText(n.getId());
          nameField.setText(n.getName());
          if (n.getLatitude() != null) latField.setText(n.getLatitude().toString());
          if (n.getLongitude() != null) lngField.setText(n.getLongitude().toString());
          idField.setDisable(true); // evitar cambiar ID en edición
          originalId = n.getId();
     }

     private void clearForm() {
          idField.setText("");
          nameField.setText("");
          latField.setText("");
          lngField.setText("");
          idField.setDisable(false);
          originalId = null;
          stationsTable.getSelectionModel().clearSelection();
     }

     private void refreshTable() {
          List<co.edu.uptc.model.Node> all = graphController.getAllNodes();
          stationsTable.getItems().setAll(all);
     }

     @FXML
     private void onNewStation() {
          clearForm();
     }

     @FXML
     private void onSaveStation() {
          String id = idField.getText().trim();
          String name = nameField.getText().trim();
          String latS = latField.getText().trim();
          String lngS = lngField.getText().trim();

          try {
               StationValidator.validate(id, name, latS, lngS, originalId,
                    graphController.getAllNodes(), 
                    bundle);
          } catch (Exception e) {
               showAlert(Alert.AlertType.ERROR, e.getMessage());
               return;
          }

          //Parse seguro despues de validacion
          Double lat = latS.isEmpty() ? null : Double.valueOf(latS);
          Double lng = lngS.isEmpty() ? null : Double.valueOf(lngS);

          boolean result;

          if (graphController.existsNode(id)) {
               //Editar
               graphController.editNode(id, name, lat, lng);
               result = true;
          } else {
               //Crear
               result = graphController.addNode(new co.edu.uptc.model.Node(id, name, lat, lng));
          }

          if (result) {
               refreshTable();
               clearForm();
               showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.station.saved"));
          } else {
               showAlert(Alert.AlertType.ERROR, bundle.getString("error.operation.failed"));
          }
     }

     @FXML
     private void onDeleteStation() {
          co.edu.uptc.model.Node sel = stationsTable.getSelectionModel().getSelectedItem();
          if (sel == null) {
               showAlert(Alert.AlertType.WARNING, bundle.getString("error.select.row"));
               return;
          }
          Alert c = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("confirm.delete.station") + " " + sel.getName(), ButtonType.YES, ButtonType.NO);
          c.setHeaderText(null);
          c.showAndWait().ifPresent(btn -> {
               if (btn == ButtonType.YES) {
                    graphController.deleteNode(sel.getId());
                    refreshTable();
                    showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.station.deleted"));
               }
          });
     }

     @FXML
     private void onSearch() {
          String q = searchField.getText().trim().toLowerCase();
          List<co.edu.uptc.model.Node> list = graphController.getAllNodes();
          if (!q.isEmpty()) {
               list = list.stream()
                         .filter(n -> n.getName().toLowerCase().contains(q) || n.getId().toLowerCase().contains(q))
                         .collect(Collectors.toList());
          }
          stationsTable.getItems().setAll(list);
     }

     @FXML
     private void onCancelEdit(ActionEvent event) {
          clearForm();
          co.edu.uptc.model.Node sel = stationsTable.getSelectionModel().getSelectedItem();
          if (sel != null) {
               // si había una selección, restaurar sus valores en el formulario
               loadToForm(sel);
          } else {
               // si no había selección (se estaba creando uno nuevo), limpiar el formulario
               clearForm();
          }
     }

     private void showAlert(Alert.AlertType type, String msg) {
          Alert a = new Alert(type);
          a.setHeaderText(null);
          a.setContentText(msg);
          a.showAndWait();
     }
}