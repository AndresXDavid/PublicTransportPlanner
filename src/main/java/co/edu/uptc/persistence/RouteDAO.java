package co.edu.uptc.persistence;

import co.edu.uptc.model.GraphData;

public interface RouteDAO {

    void save(GraphData tree, String filePath);

    GraphData load(String filePath);
}