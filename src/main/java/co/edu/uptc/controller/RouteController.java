package co.edu.uptc.controller;

import co.edu.uptc.model.*;
import java.util.*;

public class RouteController {
    private GraphController graphController;

    public RouteController(GraphController graphController) {
        this.graphController = graphController;
    }

    public RouteResult findShortestRoute(String fromId, String toId) {
        Node start = graphController.getNode(fromId);
        Node end = graphController.getNode(toId);
        if (start == null || end == null) return null;

        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparing(distances::get));

        for (Node node : graphController.getAllNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
            previous.put(node, null);
        }

        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.equals(end)) break;

            for (Edge edge : current.getEdges()) {
                Node neighbor = graphController.getNode(edge.getDestinationId());
                if (neighbor == null) continue;

                double newDist = distances.get(current) + edge.getDistance();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
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

        return new RouteResult(path, distances.get(end));
    }
}