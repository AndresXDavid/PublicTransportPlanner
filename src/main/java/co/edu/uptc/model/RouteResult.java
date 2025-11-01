package co.edu.uptc.model;

import java.util.List;

public class RouteResult {
    private List<Node> path;
    private double totalDistance;

    public RouteResult(List<Node> path, double totalDistance) {
        this.path = path;
        this.totalDistance = totalDistance;
    }

    public List<Node> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Ruta: ");
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) sb.append(" -> ");
        }
        sb.append("\nDistancia total: ").append(totalDistance);
        return sb.toString();
    }
}
