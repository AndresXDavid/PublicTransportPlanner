package co.edu.uptc.controller;

import co.edu.uptc.model.Edge;
import co.edu.uptc.model.GraphData;
import co.edu.uptc.model.Node;
import co.edu.uptc.persistence.PersistenceManager;
import co.edu.uptc.persistence.RouteDAO;

import java.util.*;

/**
 * GraphController singleton que expone exactamente la API usada por los view-controllers.
 */
public class GraphController {

    private static GraphController instance;

    private final Map<String, Node> nodes = new HashMap<>();
    private final RouteDAO routeDAO;
    private double defaultSpeed = 40.0; // km/h

    private GraphController() {
        this.routeDAO = PersistenceManager.getInstance().getRouteDAO();
    }

    public static synchronized GraphController getInstance() {
        if (instance == null) instance = new GraphController();
        return instance;
    }

    // ---- NODES ----
    public boolean addNode(Node node) {
        if (node == null || node.getId() == null || node.getId().isBlank()) return false;
        if (nodes.containsKey(node.getId())) return false;
        nodes.put(node.getId(), node);
        return true;
    }

    public boolean existsNode(String id) {
        return nodes.containsKey(id);
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public List<Node> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }

    public boolean editNode(String id, String newName, Double lat, Double lng) {
        Node n = nodes.get(id);
        if (n == null) return false;
        n.setName(newName);
        n.setLatitude(lat);
        n.setLongitude(lng);
        return true;
    }

    public boolean deleteNode(String id) {
        Node removed = nodes.remove(id);
        if (removed == null) return false;
        // eliminar aristas que apunten a este nodo
        for (Node n : nodes.values()) {
            n.removeEdgeTo(id);
        }
        return true;
    }

    // ---- EDGES ----
    /**
     * Añade una arista bidireccional a partir de un objeto Edge.
     * Si los nodos no existen, falla.
     */
    public boolean addEdge(Edge e) {
        if (e == null) return false;
        Node from = nodes.get(e.getFromId());
        Node to = nodes.get(e.getToId());
        if (from == null || to == null) return false;

        // Añadir arista bidireccional
        from.addEdge(to.getId(), e.getDistance(), e.getTime());
        to.addEdge(from.getId(), e.getDistance(), e.getTime());
        return true;
    }

    /**
     * Elimina la arista bidireccional representada por 'e' (busca por from->to).
     */
    public boolean deleteEdge(Edge e) {
        if (e == null) return false;
        Node from = nodes.get(e.getFromId());
        Node to = nodes.get(e.getToId());
        if (from == null || to == null) return false;
        boolean r1 = from.removeEdgeTo(e.getToId());
        boolean r2 = to.removeEdgeTo(e.getFromId());
        return r1 && r2;
    }

    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Node n : nodes.values()) {
            for (Edge e : n.getEdges()) {
                String key = e.getFromId() + "->" + e.getToId();
                String rev = e.getToId() + "->" + e.getFromId();
                if (seen.contains(key) || seen.contains(rev)) continue;
                edges.add(e);
                seen.add(key);
            }
        }
        return edges;
    }

    // ---- Persistencia ----
    public void saveGraph(String path) {
        GraphData gd = new GraphData(new ArrayList<>(nodes.values()));
        routeDAO.save(gd, path);
    }

    public void loadGraph(String path) {
        try {
            GraphData data = routeDAO.load(path);
            if (data == null || data.getNodes() == null) {
                System.err.println("No se pudo cargar el grafo: datos nulos");
                return;
            }
            
            // Limpiar grafo actual
            nodes.clear();
            
            // Cargar nodos
            for (Node n : data.getNodes()) {
                if (n != null && n.getId() != null) {
                    nodes.put(n.getId(), n);
                }
            }
            
            System.out.println("✅ Grafo cargado: " + nodes.size() + " nodos");
            
            // Contar aristas
            int totalEdges = 0;
            for (Node n : nodes.values()) {
                totalEdges += n.getEdges().size();
            }
            System.out.println("✅ Total de aristas: " + totalEdges);
            
        } catch (Exception e) {
            System.err.println("❌ Error al cargar grafo: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ---- Config ----
    public double getDefaultSpeed() { return defaultSpeed; }
    public void setDefaultSpeed(double defaultSpeed) { this.defaultSpeed = defaultSpeed; }
    
    // ---- Clear ----
    public void clearGraph() {
        nodes.clear();
    }
}