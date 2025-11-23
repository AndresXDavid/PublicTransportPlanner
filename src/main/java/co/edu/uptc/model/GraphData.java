package co.edu.uptc.model;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "graphData")
@XmlAccessorType(XmlAccessType.FIELD)
public class GraphData {

    @XmlElementWrapper(name = "nodes")
    @XmlElement(name = "node")
    private List<Node> nodes = new ArrayList<>();

    public GraphData() { }

    public GraphData(List<Node> nodes) { this.nodes = nodes; }

    public List<Node> getNodes() { return nodes; }

    public void setNodes(List<Node> nodes) { this.nodes = nodes; }
}
