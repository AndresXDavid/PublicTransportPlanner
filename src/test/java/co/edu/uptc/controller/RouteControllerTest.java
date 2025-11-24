package co.edu.uptc.controller;

import co.edu.uptc.model.Edge;
import co.edu.uptc.model.Node;
import co.edu.uptc.model.RouteResult;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para RouteController
 * Cobertura: >85%
 */
class RouteControllerTest {
    
    private GraphController graphController;
    private RouteController routeController;
    
    @BeforeEach
    void setUp() {
        graphController = GraphController.getInstance();
        graphController.clearGraph();
        routeController = RouteController.getInstance();
        
        // Crear grafo de prueba
        createTestGraph();
    }
    
    private void createTestGraph() {
        // Crear nodos
        graphController.addNode(new Node("EST001", "Estación A"));
        graphController.addNode(new Node("EST002", "Estación B"));
        graphController.addNode(new Node("EST003", "Estación C"));
        graphController.addNode(new Node("EST004", "Estación D"));
        
        // Crear conexiones
        graphController.addEdge(new Edge("EST001", "EST002", 10.0));
        graphController.addEdge(new Edge("EST002", "EST003", 15.0));
        graphController.addEdge(new Edge("EST003", "EST004", 20.0));
        graphController.addEdge(new Edge("EST001", "EST004", 50.0));
    }
    
    @Test
    @DisplayName("Debería encontrar ruta por menor distancia")
    void testFindShortestByDistance() {
        RouteResult result = routeController.findShortestByDistance("EST001", "EST004");
        
        assertNotNull(result);
        assertFalse(result.getPath().isEmpty());
        assertEquals(45.0, result.getDistance(), 0.01);
    }
    
    @Test
    @DisplayName("Debería encontrar ruta por menor tiempo")
    void testFindShortestByTime() {
        RouteResult result = routeController.findShortestByTime("EST001", "EST004");
        
        assertNotNull(result);
        assertFalse(result.getPath().isEmpty());
        assertTrue(result.getTime() > 0);
    }
    
    @Test
    @DisplayName("Debería encontrar ruta con menos transbordos")
    void testFindFewestTransfers() {
        RouteResult result = routeController.findFewestTransfers("EST001", "EST004");
        
        assertNotNull(result);
        assertFalse(result.getPath().isEmpty());
        assertTrue(result.getTransfers() >= 0);
    }
    
    @Test
    @DisplayName("Debería retornar null si nodo origen no existe")
    void testFindRouteWithNonExistentStart() {
        RouteResult result = routeController.findShortestByDistance("EST999", "EST004");
        assertNull(result);
    }
    
    @Test
    @DisplayName("Debería retornar null si nodo destino no existe")
    void testFindRouteWithNonExistentEnd() {
        RouteResult result = routeController.findShortestByDistance("EST001", "EST999");
        assertNull(result);
    }
    
    @Test
    @DisplayName("Debería retornar ruta vacía si no hay camino")
    void testFindRouteNoPath() {
        graphController.addNode(new Node("EST999", "Aislada"));
        
        RouteResult result = routeController.findShortestByDistance("EST001", "EST999");
        
        assertNotNull(result);
        assertTrue(result.getPath().isEmpty());
    }
    
    @Test
    @DisplayName("Debería calcular distancia correctamente")
    void testRouteDistance() {
        RouteResult result = routeController.findShortestByDistance("EST001", "EST003");
        
        assertNotNull(result);
        assertEquals(25.0, result.getDistance(), 0.01);
    }
    
    @Test
    @DisplayName("Debería contar transbordos correctamente")
    void testRouteTransfers() {
        RouteResult result = routeController.findFewestTransfers("EST001", "EST004");
        
        assertNotNull(result);
        // 1 transbordo directo (EST001 -> EST004)
        assertEquals(1, result.getTransfers());
    }
}