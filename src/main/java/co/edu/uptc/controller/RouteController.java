package co.edu.uptc.controller;

import co.edu.uptc.model.*;
import java.util.*;

public class RouteController {
    private GraphController graphController;

    public RouteController(GraphController graphController) {
        this.graphController = graphController;
    }

    /**
     * 1. Ruta más corta (menor tiempo / distancia)
     *    Algoritmo de Dijkstra
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

                // Peso = tiempo o distancia
                double newDist = distance.get(current) + edge.getDistance();

                if (newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // reconstruir ruta
        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return new RouteResult(path, distance.get(end));
    }

    /**
     * 2. Ruta con menos transbordos (menos pasos)
     *    Algoritmo BFS
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

        // reconstruir ruta
        List<Node> path = new ArrayList<>();
        for (Node at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        return new RouteResult(path, steps.get(end));
    }
}
