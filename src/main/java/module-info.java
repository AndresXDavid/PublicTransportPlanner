module co.edu.uptc {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires jakarta.xml.bind;
    requires java.logging;

    // Abrir paquetes a JavaFX FXML
    opens co.edu.uptc to javafx.fxml;
    opens co.edu.uptc.viewController to javafx.fxml;

    // Abrir paquetes para acceso reflexivo en tests
    opens co.edu.uptc.model;
    opens co.edu.uptc.validation;
    opens co.edu.uptc.persistence;
    opens co.edu.uptc.controller;

    // Exports para otros módulos
    exports co.edu.uptc;
    exports co.edu.uptc.controller;
    exports co.edu.uptc.persistence;
    exports co.edu.uptc.model;  
    exports co.edu.uptc.viewController;
    exports co.edu.uptc.validation;
}
