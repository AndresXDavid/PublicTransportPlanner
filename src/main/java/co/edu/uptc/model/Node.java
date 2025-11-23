package co.edu.uptc.model;

import jakarta.xml.bind.annotation.*;
import java.util.*;

@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class Node {
    
    @XmlElement
    private String id;
    
    @XmlElement
    private String name;
    
    @XmlElement
    private Double latitude;
    
    @XmlElement
    private Double longitude;

    // Map para acceso rápido por ID de destino
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

    // ========== JAXB Serialization Methods ==========
    
    /**
     * Método para JAXB: retorna lista MUTABLE para deserialización
     */
    @XmlElementWrapper(name = "edges")
    @XmlElement(name = "edge")
    public List<Edge> getEdgesList() {
        return new ArrayList<>(edgesMap.values());
    }

    /**
     * Método para JAXB: recibe lista y la convierte al Map interno
     */
    public void setEdgesList(List<Edge> edgesList) {
        this.edgesMap.clear();
        if (edgesList != null) {
            for (Edge e : edgesList) {
                if (e != null && e.getToId() != null) {
                    this.edgesMap.put(e.getToId(), e);
                }
            }
        }
    }

    // ========== Getters / Setters ==========
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    // ========== Edge Management API ==========
    
    /**
     * Añade una arista desde este nodo hacia otro
     */
    public void addEdge(String toId, double distance, double time) {
        Edge e = new Edge(this.id, toId, distance, time);
        edgesMap.put(toId, e);
    }

    /**
     * Añade una arista existente
     */
    public void addEdge(Edge e) {
        if (e == null || e.getToId() == null) return;
        edgesMap.put(e.getToId(), e);
    }

    /**
     * Elimina la arista hacia el nodo destino
     */
    public boolean removeEdgeTo(String destinationId) {
        return edgesMap.remove(destinationId) != null;
    }

    /**
     * Actualiza la distancia de una arista existente
     */
    public boolean updateEdgeDistance(String destinationId, double newDistance) {
        Edge e = edgesMap.get(destinationId);
        if (e == null) return false;
        e.setDistance(newDistance);
        return true;
    }

    /**
     * Retorna todas las aristas salientes como Collection
     */
    public Collection<Edge> getEdges() {
        return edgesMap.values();
    }

    /**
     * Obtiene la arista hacia un destino específico
     */
    public Edge getEdgeTo(String destinationId) { 
        return edgesMap.get(destinationId); 
    }

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