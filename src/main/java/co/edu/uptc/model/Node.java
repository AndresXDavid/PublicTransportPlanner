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
        // JAXB necesita constructor vacío
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

    public List<Edge> getEdges() {
        if (edges == null) edges = new ArrayList<>();
        return edges;
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
