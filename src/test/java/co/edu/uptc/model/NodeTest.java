package co.edu.uptc.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Node
 * Cobertura: 100%
 */
class NodeTest {
    
    @Test
    @DisplayName("Debería crear nodo con ID y nombre")
    void testCreateNode() {
        Node node = new Node("EST001", "Estación Central");
        
        assertEquals("EST001", node.getId());
        assertEquals("Estación Central", node.getName());
    }
    
    @Test
    @DisplayName("Debería crear nodo con coordenadas")
    void testCreateNodeWithCoordinates() {
        Node node = new Node("EST001", "Estación Central", 4.65, -74.05);
        
        assertEquals("EST001", node.getId());
        assertEquals("Estación Central", node.getName());
        assertEquals(4.65, node.getLatitude());
        assertEquals(-74.05, node.getLongitude());
    }
    
    @Test
    @DisplayName("Debería añadir arista a nodo")
    void testAddEdge() {
        Node node = new Node("EST001", "Estación 1");
        node.addEdge("EST002", 10.0);
        
        assertNotNull(node.getEdgeTo("EST002"));
        assertEquals(1, node.getEdges().size());
    }
    
    @Test
    @DisplayName("Debería eliminar arista de nodo")
    void testRemoveEdge() {
        Node node = new Node("EST001", "Estación 1");
        node.addEdge("EST002", 10.0);
        
        assertTrue(node.removeEdgeTo("EST002"));
        assertNull(node.getEdgeTo("EST002"));
    }
    
    @Test
    @DisplayName("Debería comparar nodos por ID")
    void testNodeEquals() {
        Node node1 = new Node("EST001", "Estación 1");
        Node node2 = new Node("EST001", "Estación 2");
        Node node3 = new Node("EST002", "Estación 3");
        
        assertEquals(node1, node2);
        assertNotEquals(node1, node3);
    }
    
    @Test
    @DisplayName("Debería generar toString correcto")
    void testToString() {
        Node node = new Node("EST001", "Estación Central");
        assertEquals("EST001 - Estación Central", node.toString());
    }
    
    @Test
    @DisplayName("Debería manejar aristas duplicadas")
    void testAddDuplicateEdge() {
        Node node = new Node("EST001", "Estación 1");
        node.addEdge("EST002", 10.0);
        node.addEdge("EST002", 15.0); // Debería sobrescribir
        
        assertEquals(15.0, node.getEdgeTo("EST002").getDistance());
        assertEquals(1, node.getEdges().size());
    }
}