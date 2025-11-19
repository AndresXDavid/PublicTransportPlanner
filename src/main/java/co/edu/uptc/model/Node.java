package co.edu.uptc.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Nodo (estación) con aristas salientes guardadas en un map (destinationId -> Edge).
 */
public class Node {
    private String id;
    private String name;
    private Double latitude;   // nullable
    private Double longitude;  // nullable

    // edges keyed by destination node id
    private Map<String, Edge> edges = new HashMap<>();

    public Node() { }

    public Node(String id, String name) {
        this(id, name, null, null);
    }

    public Node(String id, String name, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    // edges API
    public void addEdge(String toId, double distance, double time) {
        Edge e = new Edge(this.id, toId, distance, time);
        edges.put(toId, e);
    }

    public void addEdge(Edge e) {
        if (e == null) return;
        edges.put(e.getToId(), e);
    }

    public boolean removeEdgeTo(String destinationId) {
        return edges.remove(destinationId) != null;
    }

    public boolean updateEdgeDistance(String destinationId, double newDistance) {
        Edge e = edges.get(destinationId);
        if (e == null) return false;
        e.setDistance(newDistance);
        return true;
    }

    public Collection<Edge> getEdges() {
        return edges.values();
    }

    public Edge getEdgeTo(String destinationId) { return edges.get(destinationId); }

    @Override
    public String toString() {
        return id + (name != null ? " - " + name : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}