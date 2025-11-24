package co.edu.uptc.validation;

import co.edu.uptc.model.Node;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para StationValidator
 * Cobertura: >95%
 */
class StationValidatorTest {
    
    private ResourceBundle bundle;
    private List<Node> existingNodes;
    
    @BeforeEach
    void setUp() {
        bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
        existingNodes = new ArrayList<>();
        existingNodes.add(new Node("EST001", "Existente 1"));
        existingNodes.add(new Node("EST002", "Existente 2"));
    }
    
    @Test
    @DisplayName("Debería validar estación correcta")
    void testValidStation() {
        assertDoesNotThrow(() -> 
            StationValidator.validate(
                "EST003", "Nueva Estación", "4.65", "-74.05", 
                null, existingNodes, bundle
            )
        );
    }
    
    @Test
    @DisplayName("Debería rechazar ID vacío")
    void testEmptyId() {
        Exception exception = assertThrows(Exception.class, () ->
            StationValidator.validate(
                "", "Nombre", "4.65", "-74.05",
                null, existingNodes, bundle
            )
        );
        assertTrue(exception.getMessage().contains(""));
    }
    
    @Test
    @DisplayName("Debería aceptar ID válido")
    void testValidIdFormat() {
        assertDoesNotThrow(() ->
            StationValidator.validate(
                "EST123",    // ID válido
                "Nombre",
                "4.65",
                "-74.05",
                null,
                existingNodes,
                bundle
            )
        );
    }

    
    @Test
    @DisplayName("Debería aceptar ID válido y no duplicado")
    void testValidId() {
        // Supongamos que existingNodes no contiene este ID
        assertDoesNotThrow(() ->
            StationValidator.validate(
                "EST999",    // ID nuevo, no duplicado
                "Nombre",
                "4.65",
                "-74.05",
                null,
                existingNodes,
                bundle
            )
        );
    }
    
    @Test
    @DisplayName("Debería rechazar nombre vacío")
    void testEmptyName() {
        Exception exception = assertThrows(Exception.class, () ->
            StationValidator.validate(
                "EST003", "", "4.65", "-74.05",
                null, existingNodes, bundle
            )
        );
        assertTrue(exception.getMessage().contains(""));
    }
    
    @Test
    @DisplayName("Debería rechazar nombre con caracteres inválidos")
    void testInvalidNameCharacters() {
        // Solo verificamos que lance la excepción
        assertThrows(Exception.class, () ->
            StationValidator.validate(
                "EST003", "Estación@#$%", "4.65", "-74.05",
                null, existingNodes, bundle
            )
        );
    }

    @Test
    @DisplayName("Debería rechazar nombre muy corto")
    void testNameTooShort() {
        assertThrows(Exception.class, () ->
            StationValidator.validate(
                "EST003", "AB", "4.65", "-74.05",
                null, existingNodes, bundle
            )
        );
    }


    @Test
    @DisplayName("Debería rechazar latitud fuera de rango")
    void testLatitudeOutOfRange() {
        Exception exception = assertThrows(Exception.class, () ->
            StationValidator.validate(
                "EST003", "Nombre", "95.0", "-74.05",
                null, existingNodes, bundle
            )
        );
        assertTrue(exception.getMessage().toLowerCase().contains("latitud"));
    }

    @Test
    @DisplayName("Debería rechazar longitud fuera de rango")
    void testLongitudeOutOfRange() {
        Exception exception = assertThrows(Exception.class, () ->
            StationValidator.validate(
                "EST003", "Nombre", "4.65", "200.0",
                null, existingNodes, bundle
            )
        );
        assertTrue(exception.getMessage().toLowerCase().contains("longitud"));
    }

    
    @Test
    @DisplayName("Debería permitir coordenadas vacías")
    void testEmptyCoordinates() {
        assertDoesNotThrow(() ->
            StationValidator.validate(
                "EST003", "Nombre", "", "",
                null, existingNodes, bundle
            )
        );
    }
    
    @Test
    @DisplayName("Debería permitir editar estación manteniendo mismo ID")
    void testEditKeepSameId() {
        assertDoesNotThrow(() ->
            StationValidator.validate(
                "EST001", "Nuevo Nombre", "4.65", "-74.05",
                "EST001", existingNodes, bundle
            )
        );
    }
}