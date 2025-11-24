package co.edu.uptc.validation;

import co.edu.uptc.model.Node;
import java.util.List;

public class StationValidator {

    public static void validate(
            String id,
            String name,
            String latS,
            String lngS,
            String originalId,
            List<Node> existingNodes,
            java.util.ResourceBundle bundle
    ) throws Exception {

        // === ID ===
        if (id == null || id.isBlank()) {
            throw new Exception(bundle.getString("error.id.empty"));
        }

        if (!id.matches("EST\\d{3}")) {
            throw new Exception(bundle.getString("error.id.invalid"));
        }

        if (originalId == null || !originalId.equalsIgnoreCase(id)) {
            boolean duplicateId = existingNodes.stream()
                    .anyMatch(n -> n.getId().equalsIgnoreCase(id));
            if (duplicateId) {
                throw new Exception(bundle.getString("error.id.duplicate"));
            }
        }

        // === NAME ===
        if (name == null || name.isBlank()) {
            throw new Exception(bundle.getString("error.name.empty"));
        }

        if (!name.matches("[A-Za-zÁÉÍÓÚÜáéíóúüÑñ0-9 ]+")) {
            throw new Exception(bundle.getString("error.name.invalid"));
        }

        if (name.length() < 3 || name.length() > 50) {
            throw new Exception(bundle.getString("error.name.invalid_length"));
        }

        boolean duplicateName = existingNodes.stream()
                .anyMatch(n -> n.getName().equalsIgnoreCase(name));
        if (duplicateName) {
            throw new Exception(bundle.getString("error.duplicate.name"));
        }

        // === LATITUDE ===
        if (!latS.isBlank()) {
            try {
                double lat = Double.parseDouble(latS);
                if (lat < -90 || lat > 90)
                    throw new Exception(bundle.getString("error.lat.out_of_range"));
            } catch (NumberFormatException ex) {
                throw new Exception(bundle.getString("error.lat.invalid_format"));
            }
        }

        // === LONGITUDE ===
        if (!lngS.isBlank()) {
            try {
                double lng = Double.parseDouble(lngS);
                if (lng < -180 || lng > 180)
                    throw new Exception(bundle.getString("error.lon.out_of_range"));
            } catch (NumberFormatException ex) {
                throw new Exception(bundle.getString("error.lon.invalid_format"));
            }
        }
    }
}