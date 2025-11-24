package co.edu.uptc.model;

import jakarta.xml.bind.annotation.*;
import java.util.*;

/**
 * Representa una estación en el sistema de transporte público.
 * 
 * <p>Un nodo ({@code Node}) es una ubicación física en la red de transporte
 * que puede conectarse con otras estaciones mediante aristas ({@link Edge}).
 * Cada nodo tiene un identificador único, un nombre descriptivo y 
 * opcionalmente coordenadas geográficas para visualización en mapas.</p>
 * 
 * <h3>Características principales:</h3>
 * <ul>
 *   <li><b>Identificación única</b>: Cada nodo tiene un ID único en el sistema</li>
 *   <li><b>Conexiones</b>: Puede conectarse con otros nodos mediante aristas</li>
 *   <li><b>Geolocalización</b>: Soporta coordenadas de latitud y longitud</li>
 *   <li><b>Persistencia</b>: Compatible con JAXB para serialización XML</li>
 * </ul>
 * 
 * <h3>Ejemplo de uso:</h3>
 * <pre>{@code
 * // Crear una estación básica
 * Node station = new Node("EST001", "Estación Central");
 * 
 * // Crear una estación con coordenadas
 * Node station = new Node("EST002", "Portal Norte", 4.7627, -74.0464);
 * 
 * // Añadir conexión a otra estación
 * station.addEdge("EST003", 5.0); // 5 km de distancia
 * }</pre>
 * 
 * @author Sistema de Transporte Público
 * @version 1.0
 * @since 2025-01-01
 * @see Edge
 * @see GraphData
 */
@XmlRootElement(name = "node")
@XmlAccessorType(XmlAccessType.FIELD)
public class Node {

    /** Identificador único de la estación */
    @XmlElement 
    private String id;
    
    /** Nombre descriptivo de la estación */
    @XmlElement 
    private String name;
    
    /** Latitud geográfica (opcional) */
    @XmlElement 
    private Double latitude;
    
    /** Longitud geográfica (opcional) */
    @XmlElement 
    private Double longitude;

    /** Lista de aristas salientes (para serialización JAXB) */
    @XmlElementWrapper(name = "edges")
    @XmlElement(name = "edge")
    private List<Edge> edges = new ArrayList<>();

    /** Mapa interno para acceso rápido a aristas por ID destino */
    @XmlTransient
    private Map<String, Edge> edgesMap = new HashMap<>();

    /**
     * Constructor por defecto requerido por JAXB.
     * 
     * <p><b>Nota</b>: Este constructor no debe usarse directamente.
     * Use {@link #Node(String, String)} o {@link #Node(String, String, Double, Double)}
     * para crear instancias.</p>
     */
    public Node() { }

    /**
     * Constructor para crear un nodo sin coordenadas geográficas.
     * 
     * @param id Identificador único de la estación (no nulo, no vacío)
     * @param name Nombre descriptivo de la estación (no nulo)
     * @throws NullPointerException si id o name son nulos
     */
    public Node(String id, String name) {
        this(id, name, null, null);
    }

    /**
     * Constructor completo para crear un nodo con coordenadas.
     * 
     * @param id Identificador único de la estación (no nulo, no vacío)
     * @param name Nombre descriptivo de la estación (no nulo)
     * @param latitude Latitud geográfica (entre -90 y 90, o null)
     * @param longitude Longitud geográfica (entre -180 y 180, o null)
     * @throws NullPointerException si id o name son nulos
     * @throws IllegalArgumentException si las coordenadas están fuera de rango
     */
    public Node(String id, String name, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Obtiene la lista de aristas salientes de este nodo.
     * 
     * <p>Esta lista se usa para serialización JAXB. Para operaciones de consulta
     * y modificación, use los métodos específicos como {@link #addEdge(String, double)}
     * y {@link #getEdgeTo(String)}.</p>
     * 
     * @return Lista no modificable de aristas salientes
     */
    public List<Edge> getEdges() { 
        return edges; 
    }

    /**
     * Establece la lista de aristas (usado por JAXB durante deserialización).
     * 
     * <p>Este método reconstruye el mapa interno de aristas para acceso rápido.</p>
     * 
     * @param edges Lista de aristas a establecer (puede ser null)
     */
    public void setEdges(List<Edge> edges) {
        this.edges = edges != null ? edges : new ArrayList<>();
        edgesMap.clear();
        for (Edge e : this.edges) {
            if (e.getToId() != null) {
                edgesMap.put(e.getToId(), e);
            }
        }
    }

    /**
     * Añade una arista a otro nodo.
     * 
     * <p>Si ya existe una arista al mismo destino, será reemplazada.</p>
     * 
     * @param toId ID del nodo destino (no nulo, no vacío)
     * @param distance Distancia en kilómetros (mayor que 0)
     * @throws IllegalArgumentException si toId es nulo/vacío o distance ≤ 0
     */
    public void addEdge(String toId, double distance) {
        if (toId == null || toId.isBlank()) {
            throw new IllegalArgumentException("toId no puede ser nulo o vacío");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("distance debe ser mayor que 0");
        }
        
        Edge e = new Edge(this.id, toId, distance);
        edgesMap.put(toId, e);
        edges = new ArrayList<>(edgesMap.values());
    }

    /**
     * Añade una arista existente a este nodo.
     * 
     * @param e Arista a añadir (no nula)
     * @throws NullPointerException si e es null
     */
    public void addEdge(Edge e) {
        if (e != null && e.getToId() != null) {
            edgesMap.put(e.getToId(), e);
            edges = new ArrayList<>(edgesMap.values());
        }
    }

    /**
     * Elimina la arista hacia un nodo destino específico.
     * 
     * @param destinationId ID del nodo destino (no nulo)
     * @return {@code true} si se eliminó una arista, {@code false} si no existía
     */
    public boolean removeEdgeTo(String destinationId) {
        boolean removed = edgesMap.remove(destinationId) != null;
        edges = new ArrayList<>(edgesMap.values());
        return removed;
    }

    /**
     * Obtiene la arista hacia un nodo destino específico.
     * 
     * @param destinationId ID del nodo destino
     * @return La arista hacia el destino, o {@code null} si no existe
     */
    public Edge getEdgeTo(String destinationId) { 
        return edgesMap.get(destinationId); 
    }

    /**
     * Obtiene el identificador único de este nodo.
     * 
     * @return ID del nodo
     */
    public String getId() { 
        return id; 
    }

    /**
     * Establece el identificador único de este nodo.
     * 
     * @param id Nuevo ID del nodo
     */
    public void setId(String id) { 
        this.id = id; 
    }

    /**
     * Obtiene el nombre descriptivo de este nodo.
     * 
     * @return Nombre del nodo
     */
    public String getName() { 
        return name; 
    }

    /**
     * Establece el nombre descriptivo de este nodo.
     * 
     * @param name Nuevo nombre del nodo
     */
    public void setName(String name) { 
        this.name = name; 
    }

    /**
     * Obtiene la latitud geográfica de este nodo.
     * 
     * @return Latitud (puede ser null si no está definida)
     */
    public Double getLatitude() { 
        return latitude; 
    }

    /**
     * Establece la latitud geográfica de este nodo.
     * 
     * @param latitude Nueva latitud (debe estar entre -90 y 90, o null)
     */
    public void setLatitude(Double latitude) { 
        this.latitude = latitude; 
    }

    /**
     * Obtiene la longitud geográfica de este nodo.
     * 
     * @return Longitud (puede ser null si no está definida)
     */
    public Double getLongitude() { 
        return longitude; 
    }

    /**
     * Establece la longitud geográfica de este nodo.
     * 
     * @param longitude Nueva longitud (debe estar entre -180 y 180, o null)
     */
    public void setLongitude(Double longitude) { 
        this.longitude = longitude; 
    }

    /**
     * Representación en texto de este nodo.
     * 
     * @return Cadena con formato "ID - Nombre" (o solo "ID" si no hay nombre)
     */
    @Override
    public String toString() { 
        return id + (name != null ? " - " + name : ""); 
    }

    /**
     * Compara este nodo con otro objeto.
     * 
     * <p>Dos nodos son iguales si tienen el mismo ID.</p>
     * 
     * @param o Objeto a comparar
     * @return {@code true} si los IDs son iguales
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    /**
     * Calcula el hash code basado en el ID del nodo.
     * 
     * @return Hash code del nodo
     */
    @Override
    public int hashCode() { 
        return Objects.hash(id); 
    }
}