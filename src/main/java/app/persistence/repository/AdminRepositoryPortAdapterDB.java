package app.persistence.repository;

import app.domain.Admin;
import app.persistence.mappers.RowMapper;
import app.service.outputports.AdminRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepositoryPortAdapterDB implements AdminRepositoryPort {

    private final Connection connection;
    private final RowMapper rowMapper;

    public AdminRepositoryPortAdapterDB(Connection connection, RowMapper rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Admin save(Admin admin) {
        String sql = "INSERT INTO person (id, name, last_name, email, phone, password, state, rol, area) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setAdminParams(ps, admin);
            ps.executeUpdate();
            System.out.println("Se ha guardado con éxito");
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar admin" + e.getMessage());
        }
        return admin;
    }

    @Override
    public Optional<Admin> selectById(int id) {
        String sql = "SELECT * FROM person WHERE id = ? AND rol = 'Admin'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of((Admin) rowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el registro " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Admin update(Admin admin) {
        return null;
    }

    @Override
    public List<Admin> selectAll() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM person WHERE rol = 'Admin'";
        try (PreparedStatement ps = connection.prepareStatement(sql)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                admins.add((Admin) rowMapper.mapRow(rs));
            }
        } catch (SQLException e){
            throw new RuntimeException("Error al consultar los registros " + e.getMessage());
        }
        return admins;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM person WHERE id = ? AND rol = 'Admin'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Administrador eliminado con éxito.");
            } else {
                System.out.println("No se encontró un administrador con el ID proporcionado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el administrador: " + e.getMessage());
        }
    }

    @Override
    public void deleteAdmin(int id) {

    }

    private void setAdminParams(PreparedStatement ps, Admin admin) throws SQLException {
        ps.setInt(1, admin.getId());
        ps.setString(2, admin.getName());
        ps.setString(3, admin.getLastName());
        ps.setString(4, admin.getEmail());
        ps.setString(5, admin.getPhone());
        ps.setString(6, admin.getPassword());
        ps.setString(7, admin.getState());
        ps.setString(8, admin.getRol());
        ps.setString(9, admin.getArea());
    }
}
