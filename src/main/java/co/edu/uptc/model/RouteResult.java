package co.edu.uptc.model;

import java.util.Collections;
import java.util.List;

/**
 * Representa el resultado de un cálculo de ruta entre dos estaciones.
 * 
 * <p>Contiene la secuencia de nodos que forman la ruta, así como métricas
 * asociadas como distancia total, tiempo estimado y número de transbordos.</p>
 * 
 * <h3>Información incluida:</h3>
 * <ul>
 *   <li><b>Path</b>: Lista ordenada de nodos desde origen a destino</li>
 *   <li><b>Distance</b>: Distancia total en kilómetros</li>
 *   <li><b>Time</b>: Tiempo estimado en horas</li>
 *   <li><b>Transfers</b>: Número de transbordos necesarios</li>
 * </ul>
 * 
 * <h3>Ejemplo de uso:</h3>
 * <pre>{@code
 * RouteResult result = routeController.findShortestByDistance("EST001", "EST005");
 * 
 * if (!result.getPath().isEmpty()) {
 *     System.out.println("Distancia: " + result.getDistance() + " km");
 *     System.out.println("Tiempo: " + result.getTime() + " h");
 *     System.out.println("Transbordos: " + result.getTransfers());
 *     
 *     for (Node node : result.getPath()) {
 *         System.out.println("- " + node.getName());
 *     }
 * }
 * }</pre>
 * 
 * @author Sistema de Transporte Público
 * @version 1.0
 * @since 2025-01-01
 * @see Node
 * @see co.edu.uptc.controller.RouteController
 */
public class RouteResult {
    
    /** Lista ordenada de nodos que forman la ruta */
    private final List<Node> path;
    
    /** Distancia total en kilómetros */
    private final double distance;
    
    /** Número de transbordos (paradas intermedias) */
    private final int transfers;
    
    /** Tiempo estimado en horas */
    private final double time;

    /**
     * Constructor simplificado sin tiempo ni transbordos.
     * 
     * @param path Lista de nodos de la ruta (no nula)
     * @param distance Distancia total en kilómetros
     */
    public RouteResult(List<Node> path, double distance) {
        this(path, distance, -1, -1.0);
    }

    /**
     * Constructor completo con todas las métricas.
     * 
     * @param path Lista de nodos de la ruta (no nula)
     * @param distance Distancia total en kilómetros
     * @param transfers Número de transbordos (-1 si no aplica)
     * @param time Tiempo estimado en horas (-1.0 si no aplica)
     */
    public RouteResult(List<Node> path, double distance, int transfers, double time) {
        this.path = path == null ? Collections.emptyList() : path;
        this.distance = distance;
        this.transfers = transfers;
        this.time = time;
    }

    /**
     * Obtiene la lista de nodos que forman la ruta.
     * 
     * @return Lista inmutable de nodos desde origen a destino
     */
    public List<Node> getPath() { 
        return path; 
    }

    /**
     * Obtiene la distancia total de la ruta.
     * 
     * @return Distancia en kilómetros
     */
    public double getDistance() { 
        return distance; 
    }

    /**
     * Obtiene el número de transbordos.
     * 
     * @return Número de transbordos, o -1 si no aplica
     */
    public int getTransfers() { 
        return transfers; 
    }

    /**
     * Obtiene el tiempo estimado de viaje.
     * 
     * @return Tiempo en horas, o -1.0 si no aplica
     */
    public double getTime() { 
        return time; 
    }
}