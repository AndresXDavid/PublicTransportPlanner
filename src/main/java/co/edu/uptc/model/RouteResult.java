package co.edu.uptc.model;

import java.util.List;
import java.util.stream.Collectors;

public class RouteResult {

    private final List<Node> path;
    private final Double totalDistance; // null si no aplica
    private final Integer transbords;   // null si no aplica
    private final RouteResultType type;

    // Constructor para ruta por distancia (Dijkstra)
    public RouteResult(List<Node> path, double totalDistance) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.transbords = null;
        this.type = RouteResultType.DISTANCE;
    }

    // Constructor para ruta por transbordos (BFS)
    public RouteResult(List<Node> path, int transbords) {
        this.path = path;
        this.totalDistance = null;
        this.transbords = transbords;
        this.type = RouteResultType.TRANSFERS;
    }

    // Constructor para ambos valores (opcional)
    public RouteResult(List<Node> path, double totalDistance, int transbords) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.transbords = transbords;
        this.type = RouteResultType.BOTH;
    }

    public List<Node> getPath() {
        return path;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public Integer getTransbords() {
        return transbords;
    }

    public RouteResultType getType() {
        return type;
    }

    // Devuelve la ruta como String simple (solo nodos, útil para logs)
    public String getPathString() {
        return path.stream()
                   .map(Node::getName)
                   .collect(Collectors.joining(" -> "));
    }

    // toString() neutro, no incluye textos; la UI decide qué mostrar
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getPathString());
        if (totalDistance != null) sb.append(" | ").append(totalDistance);
        if (transbords != null) sb.append(" | ").append(transbords);
        return sb.toString();
    }
}