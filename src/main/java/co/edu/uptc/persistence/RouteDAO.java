package co.edu.uptc.persistence;

import co.edu.uptc.model.GraphData;

/**
 * Interfaz que define el contrato para la persistencia de objetos {@link RouteTree}.
 *
 * <p>Permite save y load estructuras de rutas en diferentes medios de almacenamiento
 * (archivos XML, CSV, bases de datos, etc.) según la implementación concreta.</p>
 */
public interface RouteDAO {

    /**
     * Guarda un objeto {@link GraphData} en un archivo o medio de almacenamiento.
     *
     * @param tree     Árbol de rutas a guardar.
     * @param filePath Ruta del archivo donde se debe almacenar la información.
     * @throws PersistenceException en caso de fallo en persistencia.
     */
    void save(GraphData tree, String filePath);

    /**
     * Carga un objeto {@link GraphData} desde un archivo o medio de almacenamiento.
     *
     * @param filePath Ruta del archivo desde donde se debe leer la información.
     * @return Objeto {@link GraphData} cargado.
     * @throws PersistenceException en caso de fallo en persistencia.
     */
    GraphData load(String filePath);
}