package co.edu.uptc.model;

import java.util.Collections;
import java.util.List;

public class RouteResult {
    private final List<Node> path;
    private final double distance;
    private final int transfers; // optional: number of transfers
    private final double time;   // optional: time if computed

    public RouteResult(List<Node> path, double distance) {
        this(path, distance, -1, -1.0);
    }

    public RouteResult(List<Node> path, double distance, int transfers, double time) {
        this.path = path == null ? Collections.emptyList() : path;
        this.distance = distance;
        this.transfers = transfers;
        this.time = time;
    }

    public List<Node> getPath() { return path; }
    public double getDistance() { return distance; }
    public int getTransfers() { return transfers; }
    public double getTime() { return time; }
}