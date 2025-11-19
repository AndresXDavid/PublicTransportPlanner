package co.edu.uptc.persistence;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.edu.uptc.model.GraphData;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class XmlRouteDAO implements RouteDAO {

    private static final Logger LOGGER = Logger.getLogger(XmlRouteDAO.class.getName());

    @Override
    public void save(GraphData tree, String filePath) {
        try {
            if (tree == null) {
                throw new PersistenceException("RouteTree nulo al intentar guardar.");
            }
            JAXBContext context = JAXBContext.newInstance(GraphData.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            File out = new File(filePath);
            File parent = out.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            marshaller.marshal(tree, out);
        } catch (PersistenceException p) {
            throw p;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error guardando RouteTree en XML: " + e.getMessage(), e);
            throw new PersistenceException("Error guardando RouteTree en XML: " + e.getMessage(), e);
        }
    }

    @Override
    public GraphData load(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                LOGGER.log(Level.INFO, "Archivo de persistencia no existe: " + filePath);
                return null;
            }

            JAXBContext context = JAXBContext.newInstance(GraphData.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (GraphData) unmarshaller.unmarshal(f);
        } catch (PersistenceException p) {
            throw p;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error cargando RouteTree desde XML: " + e.getMessage(), e);
            throw new PersistenceException("Error cargando RouteTree desde XML: " + e.getMessage(), e);
        }
    }
}