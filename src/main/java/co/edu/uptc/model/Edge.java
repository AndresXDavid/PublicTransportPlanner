package co.edu.uptc.model;

import jakarta.xml.bind.annotation.*;

/**
 * Arista con id origen / destino, distancia y tiempo.
 * Controladores esperan getters: getFromId(), getToId(), getDistance(), getTime()
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

    @XmlElement
    private double time; // opcional, si 0 se puede calcular desde distancia/speed

    public Edge() { }

    public Edge(String fromId, String toId, double distance, double time) {
        this.fromId = fromId;
        this.toId = toId;
        this.distance = distance;
        this.time = time;
    }

    // getters / setters
    public String getFromId() { return fromId; }
    public void setFromId(String fromId) { this.fromId = fromId; }

    public String getToId() { return toId; }
    public void setToId(String toId) { this.toId = toId; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getTime() { return time; }
    public void setTime(double time) { this.time = time; }

    @Override
    public String toString() {
        return fromId + " -> " + toId + " (" + distance + ", t=" + time + ")";
    }
}