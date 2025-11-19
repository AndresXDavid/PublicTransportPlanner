package co.edu.uptc.controller;

import co.edu.uptc.persistence.PersistenceManager;
import co.edu.uptc.persistence.RouteDAO;
import co.edu.uptc.model.*;

import java.util.*;

/**
 * Controlador del grafo que maneja la lógica de estaciones (nodos) y conexiones (aristas).
 * Permite agregar estaciones, conexiones, cargar y guardar grafos, y obtener nodos.
 */
public class GraphController {

    private Map<String, Node> nodes;
    private RouteDAO routeDAO;

    /**
     * Constructor por defecto. Inicializa la colección de nodos
     * y obtiene la instancia del DAO de rutas desde el PersistenceManager.
     */
    public GraphController() {
        this.nodes = new HashMap<>();
        this.routeDAO = PersistenceManager.getInstance().getRouteDAO();
    }

    /**
     * Agrega una estación al grafo.
     *
     * @param id   Identificador único de la estación
     * @param name Nombre de la estación
     * @return true si la estación se agregó correctamente, false si ya existe
     */
    public boolean addStation(String id, String name, boolean force) {

        // Validación del nombre
        if (!isValidStationName(name)) {
            System.out.println("[DEBUG addStation] Invalid name format");
            return false;
        }

        // Validación del ID
        if (nodes.containsKey(id)) {
            System.out.println("[DEBUG addStation] Duplicate ID");
            return false;
        }

        // Normalizar el nuevo nombre para comparaciones
        String normalizedNew = normalizeName(name);

        // Si NO es forzado, bloquear nombres duplicados usando normalización
        if (!force) {
            for (Node n : nodes.values()) {
                String existingNorm = normalizeName(n.getName());
                if (existingNorm.equals(normalizedNew)) {
                    System.out.println("[DEBUG addStation] Duplicate name blocked (normalized match)");
                    return false;
                }
            }
        } else {
            System.out.println("[DEBUG addStation] Duplicate name allowed (force=true)");
        }

        nodes.put(id, new Node(id, name.trim()));
        System.out.println("[DEBUG addStation] Station created successfully");
        return true;
    }



    /**
     * Agrega una conexión bidireccional entre dos estaciones.
     *
     * @param fromId   ID de la estación de origen
     * @param toId     ID de la estación de destino
     * @param distance Distancia entre las estaciones (debe ser mayor que 0)
     * @return Resultado de la operación representado por {@link AddConnectionResult}
     */
    public AddConnectionResult addConnection(String fromId, String toId, double distance) {

        if (fromId.equalsIgnoreCase(toId)) {
            return AddConnectionResult.SAME_NODE;
        }

        Node from = nodes.get(fromId);
        Node to = nodes.get(toId);

        if (from == null || to == null) {
            return AddConnectionResult.NODE_NOT_FOUND;
        }

        if (distance <= 0) {
            return AddConnectionResult.INVALID_DISTANCE;
        }

        // 🔍 NUEVO: Verificar si la conexión ya existe
        boolean existsFrom = from.getEdges().stream()
                .anyMatch(e -> e.getDestinationId().equals(toId));

        if (existsFrom) {
            return AddConnectionResult.ALREADY_EXISTS;  // <<--- importante
        }

        // Si no existe la conexión, agregarla
        from.addEdge(to, distance);
        to.addEdge(from, distance);

        return AddConnectionResult.SUCCESS;
    }


    /**
     * Obtiene un nodo del grafo por su ID.
     *
     * @param id Identificador del nodo
     * @return El nodo correspondiente, o null si no existe
     */
    public Node getNode(String id) {
        return nodes.get(id);
    }

    /**
     * Obtiene todos los nodos del grafo.
     *
     * @return Colección de todos los nodos
     */
    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    /**
     * Elimina todos los nodos y conexiones del grafo.
     */
    public void clear() {
        nodes.clear();
    }

    /**
     * Guarda el grafo actual en un archivo usando el DAO de rutas.
     *
     * @param path Ruta del archivo donde se guardará el grafo
     */
    public void saveGraph(String path) {
        GraphData data = new GraphData(new ArrayList<>(nodes.values()));
        routeDAO.save(data, path);
    }

    /**
     * Carga un grafo desde un archivo usando el DAO de rutas.
     * Reemplaza cualquier nodo existente en la memoria.
     *
     * @param path Ruta del archivo XML o binario del grafo
     */
    public void loadGraph(String path) {
        GraphData data = (GraphData) routeDAO.load(path);
        if (data != null) {
            nodes.clear();
            for (Node n : data.getNodes()) {
                nodes.put(n.getId(), n);
            }
        }
    }

    /**
     * Edita una estación existente.
     *
     * @param id   Identificador de la estación a editar
     * @param newName Nuevo nombre de la estación
     * @return true si la estación se actualizó correctamente, false si no existe
     */
    public boolean editStation(String id, String newName) {
        if (!isValidStationName(newName)) {
            System.out.println("[DEBUG editStation] Invalid name format");
            return false;
        }

        Node node = nodes.get(id);
        if (node == null) return false;

        String normalizedNew = normalizeName(newName);
        // Evitar duplicar nombre con otro nodo (comparando normalizados)
        for (Node n : nodes.values()) {
            if (!n.getId().equals(id)) {
                if (normalizeName(n.getName()).equals(normalizedNew)) {
                    System.out.println("[DEBUG editStation] Duplicate name detected (normalized match)");
                    return false;
                }
            }
        }

        node.setName(newName.trim());
        return true;
    }

    /**
     * Elimina una estación del grafo.
     * También elimina todas las conexiones que apuntan a esta estación.
     *
     * @param id Identificador de la estación a eliminar
     * @return true si la estación se eliminó correctamente, false si no existe
     */
    public boolean deleteStation(String id) {
        Node node = nodes.remove(id);
        if (node == null) return false;

        // Eliminar aristas que apunten a esta estación
        for (Node n : nodes.values()) {
            n.removeEdgeTo(id);
        }
        return true;
    }

    /**
     * Edita la distancia de una conexión existente entre dos estaciones.
     *
     * @param fromId ID de la estación de origen
     * @param toId   ID de la estación de destino
     * @param newDistance Nueva distancia (debe ser mayor que 0)
     * @return true si la conexión se actualizó correctamente, false si no existe o distancia inválida
     */
    public boolean editConnection(String fromId, String toId, double newDistance) {
        if (newDistance <= 0) return false;

        Node from = nodes.get(fromId);
        Node to = nodes.get(toId);
        if (from == null || to == null) return false;

        boolean updatedFrom = from.updateEdgeDistance(toId, newDistance);
        boolean updatedTo = to.updateEdgeDistance(fromId, newDistance);
        return updatedFrom && updatedTo;
    }

    /**
     * Elimina una conexión entre dos estaciones.
     *
     * @param fromId ID de la estación de origen
     * @param toId   ID de la estación de destino
     * @return true si la conexión se eliminó correctamente, false si no existe
     */
    public boolean deleteConnection(String fromId, String toId) {
        Node from = nodes.get(fromId);
        Node to = nodes.get(toId);
        if (from == null || to == null) return false;

        boolean removedFrom = from.removeEdgeTo(toId);
        boolean removedTo = to.removeEdgeTo(fromId);
        return removedFrom && removedTo;
    }

    public boolean stationNameExists(String name) {
        if (name == null) return false;
        String normalized = normalizeName(name);
        for (Node n : nodes.values()) {
            if (normalizeName(n.getName()).equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidStationName(String name) {
        if (name == null) return false;

        name = name.trim();

        if (name.isEmpty()) return false;

        // No permitir doble espacio
        if (name.contains("  ")) return false;

        // Solo permitir letras, espacios, tildes y ñ
        if (!name.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) return false;

        return true;
    }

    /**
     * Normaliza un nombre eliminando tildes, múltiple espacio y pasando a minúsculas.
     */
    private String normalizeName(String name) {
        if (name == null) return "";

        // minusculas
        String cleaned = name.toLowerCase();

        // quitar acentos / unicode
        cleaned = java.text.Normalizer.normalize(cleaned, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // espacios múltiples → uno solo
        cleaned = cleaned.replaceAll("\\s+", " ");

        // trim
        return cleaned.trim();
    }
}   