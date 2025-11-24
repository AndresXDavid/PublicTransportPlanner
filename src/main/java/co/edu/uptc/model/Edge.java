package co.edu.uptc.model;

import jakarta.xml.bind.annotation.*;

/**
 * Representa una conexión direccional entre dos estaciones.
 * 
 * <p>Una arista ({@code Edge}) define una conexión de transporte entre dos nodos
 * con una distancia asociada. Las aristas son direccionales, pero típicamente
 * se crean en pares para representar conexiones bidireccionales.</p>
 * 
 * <h3>Características:</h3>
 * <ul>
 *   <li><b>Direccional</b>: Tiene un nodo origen y uno destino</li>
 *   <li><b>Ponderada</b>: Incluye la distancia en kilómetros</li>
 *   <li><b>Persistente</b>: Compatible con JAXB para XML</li>
 * </ul>
 * 
 * <h3>Ejemplo de uso:</h3>
 * <pre>{@code
 * // Crear conexión de 10 km
 * Edge edge = new Edge("EST001", "EST002", 10.0);
 * 
 * // Modificar distancia
 * edge.setDistance(12.5);
 * }</pre>
 * 
 * @author Sistema de Transporte Público
 * @version 1.0
 * @since 2025-01-01
 * @see Node
 */
@XmlRootElement(name = "edge")
@XmlAccessorType(XmlAccessType.FIELD)
public class Edge {
    
    /** ID del nodo origen */
    @XmlElement
    private String fromId;

    /** ID del nodo destino */
    @XmlElement
    private String toId;

    /** Distancia en kilómetros */
    @XmlElement
    private double distance;

    /**
     * Constructor por defecto (requerido por JAXB).
     */
    public Edge() { }

    /**
     * Constructor completo para crear una arista.
     * 
     * @param fromId ID del nodo origen (no nulo)
     * @param toId ID del nodo destino (no nulo)
     * @param distance Distancia en kilómetros (mayor que 0)
     * @throws IllegalArgumentException si distance ≤ 0
     */
    public Edge(String fromId, String toId, double distance) {
        this.fromId = fromId;
        this.toId = toId;
        this.distance = distance;
    }

    /**
     * Obtiene el ID del nodo origen.
     * 
     * @return ID del nodo origen
     */
    public String getFromId() { 
        return fromId; 
    }

    /**
     * Establece el ID del nodo origen.
     * 
     * @param fromId Nuevo ID del nodo origen
     */
    public void setFromId(String fromId) { 
        this.fromId = fromId; 
    }

    /**
     * Obtiene el ID del nodo destino.
     * 
     * @return ID del nodo destino
     */
    public String getToId() { 
        return toId; 
    }

    /**
     * Establece el ID del nodo destino.
     * 
     * @param toId Nuevo ID del nodo destino
     */
    public void setToId(String toId) { 
        this.toId = toId; 
    }

    /**
     * Obtiene la distancia de esta arista.
     * 
     * @return Distancia en kilómetros
     */
    public double getDistance() { 
        return distance; 
    }

    /**
     * Establece la distancia de esta arista.
     * 
     * @param distance Nueva distancia en kilómetros (debe ser > 0)
     */
    public void setDistance(double distance) { 
        this.distance = distance; 
    }

    /**
     * Representación en texto de esta arista.
     * 
     * @return Cadena con formato "fromId -> toId (distancia km)"
     */
    @Override
    public String toString() {
        return fromId + " -> " + toId + " (" + distance +" km)";
    }
}