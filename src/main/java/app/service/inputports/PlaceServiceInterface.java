package app.service.inputports;

import app.domain.Place;
import java.util.List;

public interface PlaceServiceInterface {

    //Crea una sede con los datos recibidos.
    Place createPlace(Integer placeId, String placeName);

    // Busca una sede por su ID.
    Place selectPlaceById(Integer id);

    // Consulta todas las sedes guardadas.
    List<Place> selectAllPlaces();

    // Actualiza el nombre y el tipo de una sede identificada por su ID.
    Place updatePlace(Integer placeId, String placeName);

    // Elimina una sede usando su ID.
    void deletePlaceById(Integer id);
}
