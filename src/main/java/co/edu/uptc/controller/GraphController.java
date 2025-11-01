package co.edu.uptc.controller;

import co.edu.uptc.persistence.PersistenceManager;
import co.edu.uptc.persistence.RouteDAO;
import co.edu.uptc.model.*;

import java.util.*;

public class GraphController {
    private Map<String, Node> nodes;
    private RouteDAO routeDAO;

    public GraphController() {
        this.nodes = new HashMap<>();
        this.routeDAO = PersistenceManager.getInstance().getRouteDAO();
    }

    // Crear o agregar una estación
    public void addStation(String id, String name) {
        if (!nodes.containsKey(id)) {
            nodes.put(id, new Node(id, name));
        }
    }

    // Crear conexión entre estaciones
    public void addConnection(String fromId, String toId, double distance) {
        Node from = nodes.get(fromId);
        Node to = nodes.get(toId);
        if (from != null && to != null) {
            from.addEdge(to, distance);
            to.addEdge(from, distance); // grafo no dirigido
        }
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    public void clear() {
        nodes.clear();
    }

    // Persistir el grafo en XML usando tu DAO existente
    public void saveGraph(String path) {
        GraphData data = new GraphData(new ArrayList<>(nodes.values()));
        routeDAO.save(data, path);
    }

    // Cargar grafo desde XML usando tu DAO existente
    public void loadGraph(String path) {
        GraphData data = (GraphData) routeDAO.load(path);
        if (data != null) {
            nodes.clear();
            for (Node n : data.getNodes()) {
                nodes.put(n.getId(), n);
            }
        }
    }
}
