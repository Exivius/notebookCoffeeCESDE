package app.persistence.mappers;

import app.domain.Place;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaceMapper implements RowMapper<Place> {
    @Override
    public Place mapRow(ResultSet rs) throws SQLException {
        Place place = new Place();
        place.setPlaceId(rs.getInt("place_id"));
        place.setPlaceName(rs.getString("place_name"));
        place.setPlaceType(rs.getString("place_type"));
        return place;
    }
}
