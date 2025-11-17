package co.edu.uptc.persistence;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistenceManager {

    private static final Logger LOGGER = Logger.getLogger(PersistenceManager.class.getName());

    private static PersistenceManager instance;

    private RouteDAO routeDAO;

    private PersistenceManager() {
        try {
            this.routeDAO = new XmlRouteDAO();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inicializando XmlRouteDAO: " + e.getMessage(), e);
            this.routeDAO = null;
        }
    }

    public static synchronized PersistenceManager getInstance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    public RouteDAO getRouteDAO() {
        return routeDAO;
    }

    public void setRouteDAO(RouteDAO routeDAO) {
        this.routeDAO = routeDAO;
    }
}