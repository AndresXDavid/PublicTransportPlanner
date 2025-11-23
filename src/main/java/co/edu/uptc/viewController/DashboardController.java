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

    // 🔹 Referencia al MainController
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

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
            int stationCount = graphController.getAllNodes().size();
            int connectionCount = graphController.getAllEdges().size();
            double totalDistance = graphController.getAllEdges().stream()
                                                .mapToDouble(Edge::getDistance)
                                                .sum();
            int possibleRoutes = stationCount > 1 ? stationCount * (stationCount - 1) : 0;

            if (lblStationCount != null) lblStationCount.setText(String.valueOf(stationCount));
            if (lblConnectionCount != null) lblConnectionCount.setText(String.valueOf(connectionCount));
            if (lblTotalDistance != null) lblTotalDistance.setText(String.format("%.1f km", totalDistance));
            if (lblRouteCount != null) lblRouteCount.setText(possibleRoutes > 0 ? String.valueOf(possibleRoutes) : "0");

        } catch (Exception e) {
            System.err.println("Error updating dashboard statistics: " + e.getMessage());
        }
    }

    public void refresh() {
        updateStatistics();
    }

    // 🔹 Quick Actions
    @FXML
    private void addStation() {
        if (mainController != null) {
            mainController.selectTab("stations");
        }
    }

    @FXML
    private void addConnection() {
        if (mainController != null) {
            mainController.selectTab("connections");
        }
    }

    @FXML
    private void calculateRoute() {
        if (mainController != null) {
            mainController.selectTab("routes");
        }
    }
}
