package co.edu.uptc.viewController;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import co.edu.uptc.controller.GraphController;
import co.edu.uptc.model.Node;
import javafx.application.Platform;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MapController {

    @FXML private WebView webView;
    private WebEngine engine;
    private GraphController graphController;
    private ResourceBundle bundle;

    @FXML
     public void initialize() {
          bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
          graphController = GraphController.getInstance();

          engine = webView.getEngine();
          engine.load(getClass().getResource("/co/edu/uptc/view/map_template.html").toExternalForm());

          // Solo llamamos a initMap después de que la página cargue
          engine.getLoadWorker().stateProperty().addListener((obs, old, state) -> {
               if (state == Worker.State.SUCCEEDED) {
                    engine.executeScript("initMap();");
                    sendStationsToMap();
               }
          });
     }

     private void sendStationsToMap() {
          List<Node> nodes = graphController.getAllNodes();
          String json = nodes.stream()
                    .filter(n -> n.getLatitude() != null && n.getLongitude() != null)
                    .map(n -> String.format("{id:'%s', name:'%s', lat:%s, lng:%s}",
                              escape(n.getId()), escape(n.getName()), n.getLatitude(), n.getLongitude()))
                    .collect(Collectors.joining(", ", "[", "]"));

          Platform.runLater(() -> {
               engine.executeScript("loadStations(" + json + ");");
               engine.executeScript("setTimeout(() => map.invalidateSize(), 300);"); // fuerza redibujo
          });
          }

     private String escape(String s) {
          return s.replace("'", "\\'");
     }

}
