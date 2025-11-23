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
          cmbCriteria.getItems().addAll(
               bundle.getString("criteria.time"),
               bundle.getString("criteria.transfers")
          );

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
          if (crit.equals(bundle.getString("criteria.time"))) {
               rr = routeController.findShortestByTime(from, to);
          } else if (crit.equals(bundle.getString("criteria.transfers"))) {
               rr = routeController.findFewestTransfers(from, to);
          }


          if (rr == null || rr.getPath().isEmpty()) {
               showAlert(Alert.AlertType.INFORMATION, bundle.getString("info.no.route"));
               resultList.getItems().clear();
               routeDetails.clear();
               return;
          }

          resultList.getItems().setAll(
          rr.getPath().stream()
               .map(n -> bundle.getString("label.node") + " " + n.getId() + ": " + n.getName())
               .toList()
          );

          double timeInHours = rr.getTime();
          int hours = (int) timeInHours;
          int minutes = (int) Math.round((timeInHours - hours) * 60);

          String timeText;
          if (hours > 0) {
          timeText = hours + " h";
          if (minutes > 0) timeText += " " + minutes + " min";
          } else {
          timeText = minutes + " min";
          }

          routeDetails.setText(
          bundle.getString("label.distance") + ": " + rr.getDistance() + " km\n" +
          bundle.getString("label.time") + ": " + timeText + "\n" +
          bundle.getString("label.transfers") + ": " + rr.getTransfers()
          );
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