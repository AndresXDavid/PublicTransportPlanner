package co.edu.uptc.controller;

import co.edu.uptc.model.Edge;
import co.edu.uptc.model.Node;
import co.edu.uptc.model.RouteResult;

import java.util.*;

/**
 * Controller de rutas (singleton).
 */
public class RouteController {

    private static RouteController instance;
    private final GraphController graphController;

    private RouteController(GraphController graphController) {
        this.graphController = graphController;
    }

    public static synchronized RouteController getInstance() {
        if (instance == null) instance = new RouteController(GraphController.getInstance());
        return instance;
    }

    public RouteResult findShortestByDistance(String fromId, String toId) {
        return dijkstra(fromId, toId, false);
    }

    public RouteResult findShortestByTime(String fromId, String toId) {
        return dijkstra(fromId, toId, true);
    }

    private RouteResult dijkstra(String fromId, String toId, boolean useTime) {
        Node start = graphController.getNode(fromId);
        Node end = graphController.getNode(toId);
        if (start == null || end == null) return null;

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        for (Node n : graphController.getAllNodes()) {
            dist.put(n.getId(), Double.POSITIVE_INFINITY);
            prev.put(n.getId(), null);
        }
        dist.put(start.getId(), 0.0);

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(start.getId());

        while (!pq.isEmpty()) {
            String curId = pq.poll();
            double curD = dist.get(curId);
            if (Double.isInfinite(curD)) break;
            if (curId.equals(end.getId())) break;

            Node cur = graphController.getNode(curId);
            if (cur == null) continue;

            for (Edge e : cur.getEdges()) {
                String nb = e.getToId();
                double w = useTime ? (e.getTime() > 0 ? e.getTime() : e.getDistance() / Math.max(1.0, graphController.getDefaultSpeed()))
                                : e.getDistance();
                double alt = curD + w;
                if (alt < dist.get(nb)) {
                    dist.put(nb, alt);
                    prev.put(nb, curId);
                    pq.remove(nb);
                    pq.add(nb);
                }
            }
        }

        if (Double.isInfinite(dist.get(end.getId()))) {
            return new RouteResult(Collections.emptyList(), Double.POSITIVE_INFINITY, -1, -1.0);
        }

        List<Node> path = new ArrayList<>();
        String at = end.getId();
        while (at != null) {
            Node n = graphController.getNode(at);
            if (n != null) path.add(n);
            at = prev.get(at);
        }
        Collections.reverse(path);

        double finalDist = dist.get(end.getId());
        int transfers = Math.max(0, path.size() - 1);
        double time = useTime ? finalDist : -1.0;

        return new RouteResult(path, finalDist, transfers, time);
    }

    public RouteResult findFewestTransfers(String fromId, String toId) {
        Node start = graphController.getNode(fromId);
        Node end = graphController.getNode(toId);
        if (start == null || end == null) return null;

        Map<String, String> prev = new HashMap<>();
        Map<String, Integer> steps = new HashMap<>();
        Queue<String> q = new LinkedList<>();

        for (Node n : graphController.getAllNodes()) {
            steps.put(n.getId(), Integer.MAX_VALUE);
            prev.put(n.getId(), null);
        }
        steps.put(start.getId(), 0);
        q.add(start.getId());

        while (!q.isEmpty()) {
            String cur = q.poll();
            if (cur.equals(end.getId())) break;
            Node curNode = graphController.getNode(cur);
            if (curNode == null) continue;

            for (Edge e : curNode.getEdges()) {
                String nb = e.getToId();
                if (steps.get(nb) == Integer.MAX_VALUE) {
                    steps.put(nb, steps.get(cur) + 1);
                    prev.put(nb, cur);
                    q.add(nb);
                }
            }
        }

        if (steps.get(end.getId()) == Integer.MAX_VALUE) {
            return new RouteResult(Collections.emptyList(), Double.POSITIVE_INFINITY, -1, -1.0);
        }

        List<Node> path = new ArrayList<>();
        String at = end.getId();
        while (at != null) {
            Node n = graphController.getNode(at);
            if (n != null) path.add(n);
            at = prev.get(at);
        }
        Collections.reverse(path);

        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node a = path.get(i);
            Node b = path.get(i + 1);
            Edge e = a.getEdgeTo(b.getId());
            if (e != null) totalDistance += e.getDistance();
        }
        int transfers = Math.max(0, path.size() - 1);
        return new RouteResult(path, totalDistance, transfers, -1.0);
    }
}