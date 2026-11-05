package app.service;

import app.domain.Place;
import app.service.inputports.PlaceServiceInterface;
import app.service.outputports.PlaceRepositoryPort;
import java.util.List;

public class PlaceServiceAdapter implements PlaceServiceInterface {
    private final PlaceRepositoryPort placeRepositoryPort;

    public PlaceServiceAdapter(PlaceRepositoryPort placeRepositoryPort) {
        this.placeRepositoryPort = placeRepositoryPort;
    }

    @Override
    public Place createPlace(Integer placeId, String placeName) {
        return createPlace(placeId, placeName, "Punto de Venta");
    }

    @Override
    public Place createPlace(Integer placeId, String placeName, String placeType) {
        Place place = new Place(null, validateText(placeName, "nombre"),
                validateText(placeType, "tipo"));
        return placeRepositoryPort.save(place);
    }

    @Override
    public Place selectPlaceById(Integer id) {
        validateId(id);
        return placeRepositoryPort.selectById(id);
    }

    @Override
    public List<Place> selectAllPlaces() {
        return placeRepositoryPort.selectAllPlaces();
    }

    @Override
    public Place updatePlace(Integer placeId, String placeName) {
        Place current = selectPlaceById(placeId);
        if (current == null) {
            return null;
        }
        return updatePlace(placeId, placeName, current.getPlaceType());
    }

    @Override
    public Place updatePlace(Integer placeId, String placeName, String placeType) {
        validateId(placeId);
        Place place = new Place(placeId, validateText(placeName, "nombre"),
                validateText(placeType, "tipo"));
        return placeRepositoryPort.updatePlace(place);
    }

    @Override
    public void deletePlaceById(Integer id) {
        validateId(id);
        placeRepositoryPort.deleteById(id);
    }

    private String validateText(String value, String field) {
        if (value == null || value.isBlank() || value.trim().length() > 100) {
            throw new IllegalArgumentException("El " + field + " de la sede debe tener entre 1 y 100 caracteres.");
        }
        return value.trim();
    }

    private void validateId(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("El ID de la sede debe ser mayor que cero.");
        }
    }
}
