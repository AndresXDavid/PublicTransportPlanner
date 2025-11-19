package co.edu.uptc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Node {

    @XmlElement
    private String id;

    @XmlElement
    private String name;

    @XmlElementWrapper(name = "edges")
    @XmlElement(name = "edge")
    private List<Edge> edges = new ArrayList<>();

    public Node() {
    }

    public Node(String id, String name) {
        this.id = id;
        this.name = name;
        this.edges = new ArrayList<>();
    }

    public void addEdge(Node destination, double distance) {
        edges.add(new Edge(id, destination.getId(), distance));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Edge> getEdges() {
        if (edges == null) edges = new ArrayList<>();
        return edges;
    }

    /**
     * Elimina una arista que apunte a un nodo específico.
     *
     * @param nodeId ID del nodo destino de la arista a eliminar
     * @return true si se eliminó alguna arista, false si no existía
     */
    public boolean removeEdgeTo(String nodeId) {
        if (edges == null) return false;
        return edges.removeIf(edge -> edge.getDestinationId().equals(nodeId));
    }

    /**
     * Actualiza la distancia de la arista hacia un nodo específico.
     *
     * @param nodeId ID del nodo destino
     * @param newDistance Nueva distancia de la arista (debe ser mayor que 0)
     * @return true si se actualizó correctamente, false si no se encontró la arista
     */
    public boolean updateEdgeDistance(String nodeId, double newDistance) {
        if (edges == null || newDistance <= 0) return false;
        for (Edge edge : edges) {
            if (edge.getDestinationId().equals(nodeId)) {
                edge.setDistance(newDistance);
                return true;
            }
        }
        return false;
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

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}