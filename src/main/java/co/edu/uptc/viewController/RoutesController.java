package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import co.edu.uptc.controller.GraphController;
import co.edu.uptc.controller.RouteController;
import co.edu.uptc.model.RouteResult;

import java.util.List;
import java.util.ResourceBundle;

public class RoutesController {

     @FXML private ComboBox<String> cmbFrom, cmbTo, cmbCriteria;
     @FXML private ListView<String> resultList;
     @FXML private TextArea routeDetails;

     private GraphController graphController;
     private RouteController routeController;
     private ResourceBundle bundle;

     @FXML
     public void initialize() {
          bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
          graphController = GraphController.getInstance();
          routeController = RouteController.getInstance();

          // criterios
          cmbCriteria.getItems().addAll("Distancia", "Tiempo", "Transbordos");
          refreshNodes();
     }

     private void refreshNodes() {
          List<String> ids = graphController.getAllNodes().stream().map(n -> n.getId()).toList();
          cmbFrom.getItems().setAll(ids);
          cmbTo.getItems().setAll(ids);
     }

     @FXML
     private void onCalculate() {
          String from = cmbFrom.getValue();
          String to = cmbTo.getValue();
          String crit = cmbCriteria.getValue();
          if (from == null || to == null || crit == null) {
               showAlert(Alert.AlertType.WARNING, bundle.getString("error.select.route.params"));
               return;
          }
          RouteResult rr = null;
          if (crit.equals("Distancia")) {
               rr = routeController.findShortestByDistance(from, to);
          } else if (crit.equals("Tiempo")) {
               rr = routeController.findShortestByTime(from, to);
          } else {
               rr = routeController.findFewestTransfers(from, to);
          }

          if (rr == null || rr.getPath().isEmpty()) {
               showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.no.route"));
               resultList.getItems().clear();
               routeDetails.clear();
               return;
          }

          resultList.getItems().setAll(rr.getPath().stream().map(n -> n.getId()).toList());
          routeDetails.setText("Distancia: " + rr.getDistance() + "\nTiempo: " + rr.getTime() + "\nTransbordos: " + rr.getTransfers());
     }

     @FXML
     private void onShowOnMap() {
          // Lógica para notificar a MapView y centrar/mostrar ruta
          showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.map.show"));
     }

     @FXML
     private void onExport() {
          showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.export.ready"));
     }

     private void showAlert(Alert.AlertType t, String txt) {
          Alert a = new Alert(t);
          a.setHeaderText(null);
          a.setContentText(txt);
          a.showAndWait();
     }
}