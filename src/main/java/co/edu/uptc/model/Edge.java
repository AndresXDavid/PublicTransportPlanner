package co.edu.uptc.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Edge {

    @XmlElement
    private String sourceId;

    @XmlElement
    private String destinationId;

    @XmlElement
    private double distance;

    public Edge() {
    }

    public Edge(String sourceId, String destinationId, double distance) {
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.distance = distance;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return sourceId + " -> " + destinationId + " (" + distance + ")";
    }
}