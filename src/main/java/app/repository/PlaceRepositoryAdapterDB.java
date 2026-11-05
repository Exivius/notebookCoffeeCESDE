package app.repository;

import app.domain.Place;
import app.repository.mappers.PlaceMapper;
import app.repository.mappers.RowMapper;
import app.service.outputports.PlaceRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlaceRepositoryAdapterDB implements PlaceRepositoryPort {
    private final Connection connection;
    private final RowMapper<Place> rowMapper;

    public PlaceRepositoryAdapterDB(Connection connection) {
        this(connection, new PlaceMapper());
    }

    public PlaceRepositoryAdapterDB(Connection connection, RowMapper<Place> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Place save(Place place) {
        String sql = "INSERT INTO place (place_name, place_type) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, place.getPlaceName());
            ps.setString(2, place.getPlaceType());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("MySQL no devolvió el ID de la sede creada.");
                }
                place.setPlaceId(keys.getInt(1));
            }
            return place;
        } catch (SQLException e) {
            throw databaseError("guardar la sede", e);
        }
    }

    @Override
    public Place selectById(Integer id) {
        String sql = "SELECT place_id, place_name, place_type FROM place WHERE place_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rowMapper.mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw databaseError("consultar la sede", e);
        }
    }

    @Override
    public List<Place> selectAllPlaces() {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT place_id, place_name, place_type FROM place ORDER BY place_id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                places.add(rowMapper.mapRow(rs));
            }
            return places;
        } catch (SQLException e) {
            throw databaseError("listar las sedes", e);
        }
    }

    @Override
    public Place updatePlace(Place place) {
        String sql = "UPDATE place SET place_name = ?, place_type = ? WHERE place_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, place.getPlaceName());
            ps.setString(2, place.getPlaceType());
            ps.setInt(3, place.getPlaceId());
            int updated = ps.executeUpdate();
            // MySQL puede informar cero si los valores no cambiaron.
            return updated > 0 || selectById(place.getPlaceId()) != null ? place : null;
        } catch (SQLException e) {
            throw databaseError("actualizar la sede", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM place WHERE place_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new IllegalStateException("No se puede eliminar la sede porque tiene órdenes asociadas.", e);
            }
            throw databaseError("eliminar la sede", e);
        }
    }

    private IllegalStateException databaseError(String action, SQLException e) {
        return new IllegalStateException("No se pudo " + action + " en MySQL.", e);
    }
}
