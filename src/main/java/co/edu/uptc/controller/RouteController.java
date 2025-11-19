package co.edu.uptc.controller;

import co.edu.uptc.model.*;
import java.util.*;

/**
 * Controlador para el cálculo de rutas en el grafo.
 * Permite obtener rutas basadas en menor tiempo/distancia o en menor número de transbordos.
 */
public class RouteController {

    /** Referencia al controlador del grafo que contiene los nodos y conexiones. */
    private GraphController graphController;

    /**
     * Constructor del RouteController.
     * 
     * @param graphController instancia del controlador del grafo
     */
    public RouteController(GraphController graphController) {
        this.graphController = graphController;
    }

    /**
     * Encuentra la ruta más corta entre dos nodos usando el algoritmo de Dijkstra.
     * El peso de cada arista se considera como distancia o tiempo.
     * 
     * @param fromId ID del nodo de origen
     * @param toId ID del nodo de destino
     * @return un objeto RouteResult que contiene la lista de nodos del camino y la distancia total;
     *         devuelve null si alguno de los nodos no existe
     */
    public RouteResult findShortestTimeRoute(String fromId, String toId) {
        Node start = graphController.getNode(fromId);
        Node end = graphController.getNode(toId);
        if (start == null || end == null) return null;

        Map<Node, Double> distance = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> queue =
                new PriorityQueue<>(Comparator.comparing(distance::get));

        for (Node node : graphController.getAllNodes()) {
            distance.put(node, Double.POSITIVE_INFINITY);
            previous.put(node, null);
        }

        distance.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(end)) break;

            for (Edge edge : current.getEdges()) {
                Node neighbor = graphController.getNode(edge.getDestinationId());
                if (neighbor == null) continue;

                double newDist = distance.get(current) + edge.getDistance();

                if (newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return new RouteResult(path, distance.get(end));
    }

    /**
     * Encuentra la ruta con menos transbordos (menos pasos) entre dos nodos usando BFS.
     * 
     * @param fromId ID del nodo de origen
     * @param toId ID del nodo de destino
     * @return un objeto RouteResult que contiene la lista de nodos del camino y el número de transbordos;
     *         devuelve null si alguno de los nodos no existe
     */
    public RouteResult findFewestTransfers(String fromId, String toId) {
        Node start = graphController.getNode(fromId);
        Node end = graphController.getNode(toId);
        if (start == null || end == null) return null;

        Map<Node, Node> previous = new HashMap<>();
        Map<Node, Integer> steps = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();

        for (Node node : graphController.getAllNodes()) {
            steps.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        steps.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(end)) break;

            for (Edge edge : current.getEdges()) {
                Node neighbor = graphController.getNode(edge.getDestinationId());
                if (neighbor == null) continue;

                if (steps.get(neighbor) == Integer.MAX_VALUE) {
                    steps.put(neighbor, steps.get(current) + 1);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return new RouteResult(path, steps.get(end)-1);
    }
}
