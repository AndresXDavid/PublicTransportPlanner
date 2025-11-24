package co.edu.uptc.persistence;

import co.edu.uptc.model.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para XmlRouteDAO
 * Cobertura: >90%
 */
class XmlRouteDAOTest {
    
    private XmlRouteDAO dao;
    private String testFilePath = "test_graph.xml";
    
    @BeforeEach
    void setUp() {
        dao = new XmlRouteDAO();
        // Limpiar archivo de prueba si existe
        new File(testFilePath).delete();
    }
    
    @AfterEach
    void tearDown() {
        // Limpiar archivo de prueba
        new File(testFilePath).delete();
    }
    
    @Test
    @DisplayName("Debería guardar grafo en XML")
    void testSaveGraph() {
        GraphData data = createTestGraphData();
        
        assertDoesNotThrow(() -> dao.save(data, testFilePath));
        assertTrue(new File(testFilePath).exists());
    }
    
    @Test
    @DisplayName("Debería cargar grafo desde XML")
    void testLoadGraph() {
        GraphData original = createTestGraphData();
        dao.save(original, testFilePath);
        
        GraphData loaded = dao.load(testFilePath);
        
        assertNotNull(loaded);
        assertEquals(original.getNodes().size(), loaded.getNodes().size());
    }
    
    @Test
    @DisplayName("Debería retornar null si archivo no existe")
    void testLoadNonExistentFile() {
        GraphData loaded = dao.load("no_existe.xml");
        assertNull(loaded);
    }
    
    @Test
    @DisplayName("Debería lanzar excepción si data es null")
    void testSaveNullData() {
        assertThrows(PersistenceException.class, () ->
            dao.save(null, testFilePath)
        );
    }
    
    private GraphData createTestGraphData() {
        Node node1 = new Node("EST001", "Estación 1", 4.65, -74.05);
        node1.addEdge("EST002", 10.0);
        
        Node node2 = new Node("EST002", "Estación 2", 4.70, -74.10);
        node2.addEdge("EST001", 10.0);
        
        List<Node> nodes = Arrays.asList(node1, node2);
        return new GraphData(nodes);
    }
}