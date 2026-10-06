package app.repository.mappers;

import app.domain.Place;
import app.service.outputports.PlaceRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaceRepositoryAdapter implements PlaceRepositoryPort {
    // Lista en memoria, como en el repositorio de asientos del profesor.
    private final List<Place> places = new ArrayList<>();

    @Override
    public Place save(Place place) {
        // Agrega la sede a la lista y devuelve el objeto guardado.
        places.add(place);
        return place;
    }

    @Override
    public Place selectById(Integer id) {
        // Recorre la lista y devuelve la sede cuyo ID coincide.
        for (Place place : places) {
            if (Objects.equals(place.getPlaceId(), id)) {
                return place;
            }
        }
        // null indica que no encontró la sede.
        return null;
    }

    @Override
    public List<Place> selectAllPlaces() {
        // Devuelve una copia de la lista con las sedes guardadas.
        return new ArrayList<>(places);
    }

    @Override
    public Place updatePlace(Place place) {
        // Recorre las posiciones para reemplazar la sede con el mismo ID.
        for (int i = 0; i < places.size(); i++) {
            if (Objects.equals(places.get(i).getPlaceId(), place.getPlaceId())) {
                places.set(i, place);
                return place;
            }
        }
        // Si no existe, no modifica la lista.
        return null;
    }

    @Override
    public void deleteById(Integer id) {
        // Busca la posición de la sede que se quiere eliminar.
        for (int i = 0; i < places.size(); i++) {
            if (Objects.equals(places.get(i).getPlaceId(), id)) {
                places.remove(i);
                // Termina después de eliminar la sede.
                return;
            }
        }
    }
}
