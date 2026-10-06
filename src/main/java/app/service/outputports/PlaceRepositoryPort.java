package app.service.outputports;

import app.domain.Place;
import java.util.List;

public interface PlaceRepositoryPort {
    Place save(Place place);
    Place selectById(Integer id);
    List<Place> selectAllPlaces();
    Place updatePlace(Place place);
    void deleteById(Integer id);


}

