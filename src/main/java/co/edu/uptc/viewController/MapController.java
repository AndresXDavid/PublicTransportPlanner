package co.edu.uptc.viewController;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import co.edu.uptc.controller.GraphController;
import co.edu.uptc.model.Node;
import javafx.application.Platform;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para la vista del mapa interactivo.
 * 
 * <p>Este controlador gestiona la visualización de estaciones en un mapa
 * utilizando Leaflet a través de un componente WebView de JavaFX.</p>
 * 
 * <p>Características principales:</p>
 * <ul>
 *   <li>Inicialización segura del mapa evitando problemas de timing</li>
 *   <li>Carga dinámica de estaciones desde el controlador del grafo</li>
 *   <li>Manejo de redimensionamiento automático del mapa</li>
 *   <li>Gestión de errores de JavaScript</li>
 * </ul>
 * 
 * @author Sistema de Transporte Público
 * @version 1.0
 * @since 2025-01-01
 */
public class MapController {

    /** WebView que contiene el mapa de Leaflet */
    @FXML 
    private WebView webView;
    
    /** Motor de JavaScript del WebView */
    private WebEngine engine;
    
    /** Controlador del grafo con las estaciones */
    private GraphController graphController;
    
    /** Bundle de recursos para internacionalización */
    private ResourceBundle bundle;
    
    /** Flag para evitar múltiples inicializaciones */
    private boolean mapInitialized = false;

    /**
     * Inicializa el controlador después de que se hayan inyectado los elementos FXML.
     * 
     * <p>Este método configura:</p>
     * <ul>
     *   <li>El bundle de internacionalización</li>
     *   <li>La instancia del GraphController</li>
     *   <li>El WebEngine y sus listeners</li>
     *   <li>La carga del HTML del mapa</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        bundle = ResourceBundle.getBundle("co.edu.uptc.i18n.messages");
        graphController = GraphController.getInstance();

        engine = webView.getEngine();
        
        // Habilitar JavaScript (por defecto está habilitado, pero es buena práctica)
        engine.setJavaScriptEnabled(true);
        
        // Cargar el HTML del mapa
        String mapUrl = getClass().getResource("/co/edu/uptc/view/map_template.html").toExternalForm();
        engine.load(mapUrl);
        
        // Listener principal: ejecutar cuando la página esté completamente cargada
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                onPageLoadSucceeded();
            } else if (newState == Worker.State.FAILED) {
                System.err.println("❌ Failed to load map HTML");
            }
        });
        
        // Listener adicional para manejar cambios de tamaño del WebView
        webView.widthProperty().addListener((obs, oldVal, newVal) -> invalidateMapSize());
        webView.heightProperty().addListener((obs, oldVal, newVal) -> invalidateMapSize());
    }

    /**
     * Método ejecutado cuando la página HTML se ha cargado exitosamente.
     * 
     * <p>Este método:</p>
     * <ol>
     *   <li>Espera un pequeño delay para asegurar que todo esté renderizado</li>
     *   <li>Inicializa el mapa de Leaflet</li>
     *   <li>Carga las estaciones desde el GraphController</li>
     *   <li>Fuerza un redibujo del mapa</li>
     * </ol>
     */
    private void onPageLoadSucceeded() {
        // Esperar un pequeño delay para asegurar que el DOM esté completamente listo
        Platform.runLater(() -> {
            try {
                // Delay adicional para asegurar renderizado
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            initializeMap();
            sendStationsToMap();
            
            // Forzar redibujo después de cargar estaciones
            Platform.runLater(() -> {
                try {
                    Thread.sleep(200);
                    invalidateMapSize();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });
    }

    /**
     * Inicializa el mapa de Leaflet en el WebView.
     * 
     * <p>Solo inicializa el mapa una vez para evitar múltiples instancias.
     * La función JavaScript {@code initMap()} debe estar definida en el HTML.</p>
     */
    private void initializeMap() {
        if (mapInitialized) {
            return;
        }
        
        try {
            engine.executeScript("initMap();");
            mapInitialized = true;
            System.out.println("✅ Mapa inicializado correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error al inicializar mapa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envía las estaciones al mapa de Leaflet para su visualización.
     * 
     * <p>Este método:</p>
     * <ul>
     *   <li>Obtiene todas las estaciones del GraphController</li>
     *   <li>Filtra solo las que tienen coordenadas válidas</li>
     *   <li>Convierte los datos a formato JSON</li>
     *   <li>Ejecuta JavaScript para añadir los marcadores al mapa</li>
     * </ul>
     */
    private void sendStationsToMap() {
        List<Node> nodes = graphController.getAllNodes();
        
        // Construir JSON manualmente para evitar problemas de escape
        String json = nodes.stream()
            .filter(n -> n.getLatitude() != null && n.getLongitude() != null)
            .map(n -> String.format(
                "{id:'%s', name:'%s', lat:%s, lng:%s}",
                escape(n.getId()), 
                escape(n.getName()), 
                n.getLatitude(), 
                n.getLongitude()
            ))
            .collect(Collectors.joining(", ", "[", "]"));

        try {
            engine.executeScript("loadStations(" + json + ");");
            System.out.println("✅ Estaciones cargadas en el mapa: " + nodes.size());
        } catch (Exception e) {
            System.err.println("❌ Error al cargar estaciones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fuerza el redibujo del mapa de Leaflet.
     * 
     * <p>Este método es útil cuando:</p>
     * <ul>
     *   <li>El tamaño del WebView cambia</li>
     *   <li>La pestaña se hace visible después de estar oculta</li>
     *   <li>Se necesita corregir problemas de renderizado</li>
     * </ul>
     */
    private void invalidateMapSize() {
        if (!mapInitialized) {
            return;
        }
        
        Platform.runLater(() -> {
            try {
                engine.executeScript("if (map) { map.invalidateSize(); }");
            } catch (Exception e) {
                // Silenciar errores si el mapa aún no está listo
                System.err.println("⚠️ No se pudo invalidar tamaño del mapa: " + e.getMessage());
            }
        });
    }

    /**
     * Escapa caracteres especiales para uso seguro en JavaScript.
     * 
     * @param s Cadena a escapar
     * @return Cadena con caracteres especiales escapados
     */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Método público para recargar las estaciones en el mapa.
     * 
     * <p>Útil para actualizar el mapa cuando se añaden o modifican estaciones.</p>
     */
    public void refreshStations() {
        if (mapInitialized) {
            sendStationsToMap();
            invalidateMapSize();
        }
    }
}