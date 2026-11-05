package app.persistence.repository;

import app.domain.Admin;
import app.persistence.mappers.RowMapper;
import app.service.outputports.AdminRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        String sql = "INSERT INTO person (name, last_name, email, phone, password, state, rol, area) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setAdminParams(ps, admin);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    admin.setId(keys.getInt(1));
                }
            }

            System.out.println("Se ha guardado con éxito");
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar admin: " + e.getMessage(), e);
        }
        return admin;
    }

    @Override
    public Optional<Admin> selectById(int id) {
        String sql = "SELECT * FROM person WHERE id = ? AND rol = 'ADMIN'";
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
        String sql = "UPDATE person SET name = ?, last_name = ?, email = ?, phone = ?, "
                + "password = ?, state = ?, rol = ?, area = ? "
                + "WHERE id = ? AND rol = 'ADMIN'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setAdminParams(ps, admin);
            ps.setInt(9, admin.getId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Administrador actualizado con éxito.");
                return admin;
            } else {
                System.out.println("No se encontró un administrador con el ID proporcionado.");
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el administrador: " + e.getMessage());
        }
    }

    @Override
    public List<Admin> selectAll() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM person WHERE rol = 'ADMIN'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                admins.add((Admin) rowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar los registros " + e.getMessage());
        }
        return admins;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM person WHERE id = ? AND rol = 'ADMIN'";
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
        ps.setString(1, admin.getName());
        ps.setString(2, admin.getLastName());
        ps.setString(3, admin.getEmail());
        ps.setString(4, admin.getPhone());
        ps.setString(5, admin.getPassword());
        ps.setString(6, admin.getState());
        ps.setString(7, "ADMIN");
        ps.setString(8, admin.getArea());
    }

}
