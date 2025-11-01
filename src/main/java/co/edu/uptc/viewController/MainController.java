package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import co.edu.uptc.controller.GraphController;
import co.edu.uptc.model.Node;

public class MainController {

    @FXML private TextArea outputArea;

    private GraphController graphController = new GraphController();

    @FXML
    private void onLoadGraph() {
        try {
            graphController.loadGraph("src/main/resources/co/edu/uptc/network_example.xml");
            outputArea.setText("✅ Grafo cargado correctamente.\nNodos: " + graphController.getAllNodes().size());
        } catch (Exception e) {
            outputArea.setText("❌ Error al cargar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onShowNodes() {
        StringBuilder sb = new StringBuilder();
        for (Node n : graphController.getAllNodes()) {
            sb.append("• ").append(n.getId())
              .append(" - ").append(n.getName()).append("\n");
        }
        outputArea.setText(sb.toString());
    }
}
