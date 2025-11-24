package co.edu.uptc.controller;

import co.edu.uptc.model.Edge;
import co.edu.uptc.model.Node;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para GraphController
 * Cobertura: >90%
 */
class GraphControllerTest {
    
    private GraphController controller;
    
    @BeforeEach
    void setUp() {
        controller = GraphController.getInstance();
        controller.clearGraph();
    }
    
    @Test
    @DisplayName("Debería añadir un nodo correctamente")
    void testAddNode() {
        Node node = new Node("EST001", "Estación Central");
        assertTrue(controller.addNode(node));
        assertEquals(node, controller.getNode("EST001"));
    }
    
    @Test
    @DisplayName("No debería permitir nodos duplicados")
    void testAddDuplicateNode() {
        Node node1 = new Node("EST001", "Estación 1");
        Node node2 = new Node("EST001", "Estación 2");
        
        assertTrue(controller.addNode(node1));
        assertFalse(controller.addNode(node2));
    }
    
    @Test
    @DisplayName("No debería añadir nodo nulo")
    void testAddNullNode() {
        assertFalse(controller.addNode(null));
    }
    
    @Test
    @DisplayName("No debería añadir nodo sin ID")
    void testAddNodeWithoutId() {
        Node node = new Node(null, "Sin ID");
        assertFalse(controller.addNode(node));
    }
    
    @Test
    @DisplayName("Debería verificar existencia de nodo")
    void testExistsNode() {
        Node node = new Node("EST001", "Estación Test");
        controller.addNode(node);
        
        assertTrue(controller.existsNode("EST001"));
        assertFalse(controller.existsNode("EST999"));
    }
    
    @Test
    @DisplayName("Debería obtener todos los nodos")
    void testGetAllNodes() {
        controller.addNode(new Node("EST001", "Estación 1"));
        controller.addNode(new Node("EST002", "Estación 2"));
        controller.addNode(new Node("EST003", "Estación 3"));
        
        assertEquals(3, controller.getAllNodes().size());
    }
    
    @Test
    @DisplayName("Debería editar un nodo existente")
    void testEditNode() {
        Node node = new Node("EST001", "Original", 4.65, -74.05);
        controller.addNode(node);
        
        assertTrue(controller.editNode("EST001", "Modificado", 4.70, -74.10));
        
        Node updated = controller.getNode("EST001");
        assertEquals("Modificado", updated.getName());
        assertEquals(4.70, updated.getLatitude());
        assertEquals(-74.10, updated.getLongitude());
    }
    
    @Test
    @DisplayName("No debería editar nodo inexistente")
    void testEditNonExistentNode() {
        assertFalse(controller.editNode("EST999", "No Existe", null, null));
    }
    
    @Test
    @DisplayName("Debería eliminar un nodo")
    void testDeleteNode() {
        Node node = new Node("EST001", "A Eliminar");
        controller.addNode(node);
        
        assertTrue(controller.deleteNode("EST001"));
        assertNull(controller.getNode("EST001"));
    }
    
    @Test
    @DisplayName("No debería eliminar nodo inexistente")
    void testDeleteNonExistentNode() {
        assertFalse(controller.deleteNode("EST999"));
    }
    
    @Test
    @DisplayName("Debería eliminar aristas al eliminar nodo")
    void testDeleteNodeRemovesEdges() {
        Node node1 = new Node("EST001", "Estación 1");
        Node node2 = new Node("EST002", "Estación 2");
        
        controller.addNode(node1);
        controller.addNode(node2);
        
        Edge edge = new Edge("EST001", "EST002", 5.0);
        controller.addEdge(edge);
        
        controller.deleteNode("EST002");
        
        assertNull(controller.getEdge("EST001", "EST002"));
    }
    
    @Test
    @DisplayName("Debería añadir una arista bidireccional")
    void testAddEdge() {
        Node node1 = new Node("EST001", "Estación 1");
        Node node2 = new Node("EST002", "Estación 2");
        
        controller.addNode(node1);
        controller.addNode(node2);
        
        Edge edge = new Edge("EST001", "EST002", 10.5);
        assertTrue(controller.addEdge(edge));
        
        assertNotNull(controller.getEdge("EST001", "EST002"));
        assertNotNull(controller.getEdge("EST002", "EST001"));
    }
    
    @Test
    @DisplayName("No debería añadir arista con nodos inexistentes")
    void testAddEdgeWithNonExistentNodes() {
        Edge edge = new Edge("EST999", "EST888", 5.0);
        assertFalse(controller.addEdge(edge));
    }
    
    @Test
    @DisplayName("Debería obtener todas las aristas")
    void testGetAllEdges() {
        controller.addNode(new Node("EST001", "Estación 1"));
        controller.addNode(new Node("EST002", "Estación 2"));
        controller.addNode(new Node("EST003", "Estación 3"));
        
        controller.addEdge(new Edge("EST001", "EST002", 5.0));
        controller.addEdge(new Edge("EST002", "EST003", 7.0));
        
        assertEquals(2, controller.getAllEdges().size());
    }
    
    @Test
    @DisplayName("Debería editar una arista existente")
    void testEditEdge() {
        controller.addNode(new Node("EST001", "Estación 1"));
        controller.addNode(new Node("EST002", "Estación 2"));
        
        controller.addEdge(new Edge("EST001", "EST002", 10.0));
        
        assertTrue(controller.editEdge("EST001", "EST002", 15.0));
        
        Edge updated = controller.getEdge("EST001", "EST002");
        assertEquals(15.0, updated.getDistance());
    }
    
    @Test
    @DisplayName("Debería eliminar una arista")
    void testDeleteEdge() {
        controller.addNode(new Node("EST001", "Estación 1"));
        controller.addNode(new Node("EST002", "Estación 2"));
        
        Edge edge = new Edge("EST001", "EST002", 10.0);
        controller.addEdge(edge);
        
        assertTrue(controller.deleteEdge(edge));
        assertNull(controller.getEdge("EST001", "EST002"));
    }
    
    @Test
    @DisplayName("Debería configurar velocidad por defecto")
    void testSetDefaultSpeed() {
        controller.setDefaultSpeed(50.0);
        assertEquals(50.0, controller.getDefaultSpeed());
    }
}

