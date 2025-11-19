package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import co.edu.uptc.controller.GraphController;

import java.util.ResourceBundle;

public class SettingsController {

     @FXML private TextField speedField;
     private GraphController graphController;
     private ResourceBundle bundle;

     @FXML
     public void initialize() {
          bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
          graphController = GraphController.getInstance();
          speedField.setText(String.valueOf(graphController.getDefaultSpeed()));
     }

     @FXML
     private void onSaveSettings() {
          try {
               double s = Double.parseDouble(speedField.getText().trim());
               graphController.setDefaultSpeed(s);
               showInfo(bundle.getString("info.settings.saved"));
          } catch (NumberFormatException ex) {
               showError(bundle.getString("error.invalid.number"));
          }
     }

     private void showInfo(String txt) {
          Alert a = new Alert(Alert.AlertType.INFORMATION, txt);
          a.setHeaderText(null);
          a.showAndWait();
     }

     private void showError(String txt) {
          Alert a = new Alert(Alert.AlertType.ERROR, txt);
          a.setHeaderText(null);
          a.showAndWait();
     }
}