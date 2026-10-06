package app.service;

import app.domain.Place;
import app.service.inputports.PlaceServiceInterface;
import app.service.outputports.PlaceRepositoryPort;
import java.util.List;

public class PlaceServiceAdapter implements PlaceServiceInterface {
    // Referencia al repositorio que utiliza el servicio.
    private final PlaceRepositoryPort placeRepositoryPort;

    // Recibe el repositorio por el constructor.
    public PlaceServiceAdapter(PlaceRepositoryPort placeRepositoryPort) {
        this.placeRepositoryPort = placeRepositoryPort;
    }

    @Override
    public Place createPlace(Integer placeId, String placeName, String placeType) {
        // Construye la sede con los datos recibidos.
        Place place = new Place(placeId, placeName, placeType);
        // Solicita al repositorio guardarla y devuelve el resultado.
        return placeRepositoryPort.save(place);
    }

    @Override
    public Place selectPlaceById(Integer id) {
        // Solicita al repositorio buscar la sede por su ID.
        return placeRepositoryPort.selectById(id);
    }

    @Override
    public List<Place> selectAllPlaces() {
        // Solicita al repositorio todas las sedes guardadas.
        return placeRepositoryPort.selectAllPlaces();
    }

    @Override
    public Place updatePlace(Integer placeId, String placeName, String placeType) {
        // Construye la sede con los nuevos datos y conserva el ID.
        Place place = new Place(placeId, placeName, placeType);
        return placeRepositoryPort.updatePlace(place);
    }

    @Override
    public void deletePlaceById(Integer id) {
        // Solicita al repositorio eliminar la sede indicada.
        placeRepositoryPort.deleteById(id);
    }
}
