package co.edu.uptc.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import co.edu.uptc.controller.GraphController;

import java.util.ResourceBundle;

public class SettingsController {

    @FXML private TextField speedField;
    private GraphController graphController;
    private ResourceBundle bundle;

    // === RANGO PERMITIDO ===
    private static final double MIN_SPEED = 1.0;     // km/h
    private static final double MAX_SPEED = 300.0;   // km/h

    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
        graphController = GraphController.getInstance();
        speedField.setText(String.valueOf(graphController.getDefaultSpeed()));

        // === Restringir el campo SOLO a dígitos y un punto ===
        speedField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d*)?")) {  
                return change; 
            }
            return null;
        }));
    }


    @FXML
    private void onSaveSettings() {

        String raw = speedField.getText().trim();

        if (raw.isEmpty()) {
            showError(bundle.getString("error.speed.empty"));
            return;
        }

        double speed;

        try {
            speed = Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            showError(bundle.getString("error.speed.not_number"));
            return;
        }

        if (speed < MIN_SPEED || speed > MAX_SPEED) {
            showError(
                bundle.getString("error.speed.out_of_range")
                        .replace("{min}", String.valueOf(MIN_SPEED))
                        .replace("{max}", String.valueOf(MAX_SPEED))
            );
            return;
        }

        graphController.setDefaultSpeed(speed);
        showInfo(bundle.getString("info.settings.saved"));
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