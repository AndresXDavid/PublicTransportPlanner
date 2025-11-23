package co.edu.uptc.model;

import jakarta.xml.bind.annotation.*;

/**
 * Arista con id origen / destino, distancia.
 * Controladores esperan getters: getFromId(), getToId(), getDistance()
 */
@XmlRootElement(name = "edge")
@XmlAccessorType(XmlAccessType.FIELD)
public class Edge {
    @XmlElement
    private String fromId;

    @XmlElement
    private String toId;

    @XmlElement
    private double distance;

    public Edge() { }

    public Edge(String fromId, String toId, double distance) {
        this.fromId = fromId;
        this.toId = toId;
        this.distance = distance;
    }

    // getters / setters
    public String getFromId() { return fromId; }
    public void setFromId(String fromId) { this.fromId = fromId; }

    public String getToId() { return toId; }
    public void setToId(String toId) { this.toId = toId; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    @Override
    public String toString() {
        return fromId + " -> " + toId + " (" + distance +" km)";
    }
}