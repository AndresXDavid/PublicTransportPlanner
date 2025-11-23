package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import co.edu.uptc.controller.GraphController;
import co.edu.uptc.model.Edge;

import java.util.ResourceBundle;

public class DashboardController {

     @FXML private Label lblStationCount;
     @FXML private Label lblConnectionCount;
     @FXML private Label lblTotalDistance;
     @FXML private Label lblRouteCount;

     private GraphController graphController;
     private ResourceBundle bundle;

     @FXML
     public void initialize() {
          bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
          graphController = GraphController.getInstance();
          
          // Actualizar estadísticas cada 2 segundos
          javafx.animation.Timeline timeline = new javafx.animation.Timeline(
               new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(2),
                    event -> updateStatistics()
               )
          );
          timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
          timeline.play();
          
          // Actualización inicial
          updateStatistics();
     }

     private void updateStatistics() {
          try {
               // Número de estaciones
               int stationCount = graphController.getAllNodes().size();
               if (lblStationCount != null) {
                    lblStationCount.setText(String.valueOf(stationCount));
               }

               // Número de conexiones
               int connectionCount = graphController.getAllEdges().size();
               if (lblConnectionCount != null) {
                    lblConnectionCount.setText(String.valueOf(connectionCount));
               }

               // Distancia total
               double totalDistance = graphController.getAllEdges().stream()
                    .mapToDouble(Edge::getDistance)
                    .sum();
               if (lblTotalDistance != null) {
                    lblTotalDistance.setText(String.format("%.1f km", totalDistance));
               }

               // Rutas posibles (n * (n-1) para grafos dirigidos)
               int possibleRoutes = stationCount > 1 ? stationCount * (stationCount - 1) : 0;
               if (lblRouteCount != null) {
                    if (possibleRoutes > 0) {
                         lblRouteCount.setText(String.valueOf(possibleRoutes));
                    } else {
                         lblRouteCount.setText("0");
                    }
               }
          } catch (Exception e) {
               System.err.println("Error updating dashboard statistics: " + e.getMessage());
          }
     }

     public void refresh() {
          updateStatistics();
     }
}