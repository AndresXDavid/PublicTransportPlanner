package co.edu.uptc.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Edge
 * Cobertura: 100%
 */
class EdgeTest {
    
    @Test
    @DisplayName("Debería crear arista con datos válidos")
    void testCreateEdge() {
        Edge edge = new Edge("EST001", "EST002", 10.5);
        
        assertEquals("EST001", edge.getFromId());
        assertEquals("EST002", edge.getToId());
        assertEquals(10.5, edge.getDistance());
    }
    
    @Test
    @DisplayName("Debería permitir modificar distancia")
    void testSetDistance() {
        Edge edge = new Edge("EST001", "EST002", 10.0);
        edge.setDistance(15.0);
        
        assertEquals(15.0, edge.getDistance());
    }
    
    @Test
    @DisplayName("Debería generar toString correcto")
    void testToString() {
        Edge edge = new Edge("EST001", "EST002", 10.5);
        assertEquals("EST001 -> EST002 (10.5 km)", edge.toString());
    }
    
    @Test
    @DisplayName("Debería permitir modificar nodos")
    void testSetNodes() {
        Edge edge = new Edge("EST001", "EST002", 10.0);
        
        edge.setFromId("EST003");
        edge.setToId("EST004");
        
        assertEquals("EST003", edge.getFromId());
        assertEquals("EST004", edge.getToId());
    }
}