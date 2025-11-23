package co.edu.uptc.model;

import jakarta.xml.bind.annotation.*;
import java.util.*;

@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class Node {

    @XmlElement private String id;
    @XmlElement private String name;
    @XmlElement private Double latitude;
    @XmlElement private Double longitude;

    // Lista usada por JAXB para serializar/deserializar
    @XmlElementWrapper(name = "edges")
    @XmlElement(name = "edge")
    private List<Edge> edges = new ArrayList<>();

    // Mapa interno para acceso rápido
    @XmlTransient
    private Map<String, Edge> edgesMap = new HashMap<>();

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

    // ========== JAXB Getters/Setters ==========
    public List<Edge> getEdges() { return edges; }

    public void setEdges(List<Edge> edges) {
        this.edges = edges != null ? edges : new ArrayList<>();
        edgesMap.clear();
        for (Edge e : this.edges) {
            if (e.getToId() != null) edgesMap.put(e.getToId(), e);
        }
    }

    // ========== Mapa para acceso rápido ==========
    public Collection<Edge> getEdgesMapValues() { return edgesMap.values(); }

    public void addEdge(Edge e) {
        if (e != null && e.getToId() != null) {
            edgesMap.put(e.getToId(), e);
            // Mantener lista sincronizada para JAXB
            edges = new ArrayList<>(edgesMap.values());
        }
    }

    public boolean removeEdgeTo(String destinationId) {
        boolean removed = edgesMap.remove(destinationId) != null;
        edges = new ArrayList<>(edgesMap.values());
        return removed;
    }

    // ========== Getters/Setters básicos ==========
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    @Override
    public String toString() { return id + (name != null ? " - " + name : ""); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    /**
     * Retorna la arista hacia el nodo destino
     */
    public Edge getEdgeTo(String destinationId) { 
        return edgesMap.get(destinationId); 
    }

    /**
     * Añade una arista desde este nodo hacia otro
     */
    public void addEdge(String toId, double distance, double time) {
        if (toId == null || toId.isBlank()) return;
        Edge e = new Edge(this.id, toId, distance, time);
        edgesMap.put(toId, e);
        // Mantener lista sincronizada para JAXB
        edges = new ArrayList<>(edgesMap.values());
    }
}
