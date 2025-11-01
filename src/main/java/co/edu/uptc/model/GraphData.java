package co.edu.uptc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase contenedora de nodos y conexiones, para serialización XML.
 * Sustituye el antiguo RouteTree del DAO.
 */
@XmlRootElement(name = "graphData")
@XmlAccessorType(XmlAccessType.FIELD)
public class GraphData {

    @XmlElementWrapper(name = "nodes")
    @XmlElement(name = "node")
    private List<Node> nodes;

    public GraphData() {
        this.nodes = new ArrayList<>();
    }

    public GraphData(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        if (nodes == null) nodes = new ArrayList<>();
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
