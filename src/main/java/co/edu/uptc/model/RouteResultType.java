package co.edu.uptc.model;

public enum RouteResultType {
    DISTANCE,    // Ruta calculada por menor tiempo / distancia (Dijkstra)
    TRANSFERS,   // Ruta calculada por menor número de transbordos (BFS)
    BOTH         // Ruta que contiene ambos datos
}
