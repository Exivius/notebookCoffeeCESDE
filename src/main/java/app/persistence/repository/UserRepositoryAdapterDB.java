package app.persistence.repository;

import app.domain.User;
import app.persistence.mappers.RowMapper;
import app.persistence.mappers.UserMapper;
import app.service.outputports.UserRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepositoryAdapterDB implements UserRepositoryPort {
    private final Connection connection;
    private final RowMapper<User> rowMapper;
    private static final String SELECT_USERS =
            "SELECT id, name, last_name, email, phone, password, state, rol, city "
            + "FROM person WHERE rol = 'User'";

    public UserRepositoryAdapterDB(Connection connection) {
        this(connection, new UserMapper());
    }

    public UserRepositoryAdapterDB(Connection connection, RowMapper<User> rowMapper) {
        this.connection = Objects.requireNonNull(connection);
        this.rowMapper = Objects.requireNonNull(rowMapper);
    }

    @Override
    public User save(User user) {
        requireUser(user);
        String sql = "INSERT INTO person (name, last_name, email, phone, password, state, rol, city) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'User', ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, user);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("MySQL no devolvió el ID del usuario creado.");
                }
                user.setId(keys.getInt(1));
            }
            user.setRol("User");
            return user;
        } catch (SQLException e) {
            throw databaseError("crear el usuario", e);
        }
    }

    @Override
    public User create(User user) {
        return save(user);
    }

    @Override
    public User selectById(int id) {
        requireId(id);
        try (PreparedStatement ps = connection.prepareStatement(SELECT_USERS + " AND id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rowMapper.mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw databaseError("consultar el usuario", e);
        }
    }

    @Override
    public List<User> selectAll() {
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_USERS + " ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(rowMapper.mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw databaseError("listar los usuarios", e);
        }
    }

    @Override
    public User updateUser(User user) {
        requireUser(user);
        requireId(user.getId());
        String sql = "UPDATE person SET name = ?, last_name = ?, email = ?, phone = ?, "
                + "password = ?, state = ?, city = ? WHERE id = ? AND rol = 'User'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParams(ps, user);
            ps.setInt(8, user.getId());
            if (ps.executeUpdate() == 0 && selectById(user.getId()) == null) {
                return null;
            }
            user.setRol("User");
            return user;
        } catch (SQLException e) {
            throw databaseError("actualizar el usuario", e);
        }
    }

    @Override
    public void deleteById(int id) {
        requireId(id);
        String sql = "DELETE FROM person WHERE id = ? AND rol = 'User'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new IllegalStateException("No se puede eliminar un usuario con órdenes asociadas.", e);
            }
            throw databaseError("eliminar el usuario", e);
        }
    }

    @Override
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM person WHERE rol = 'User'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new IllegalStateException("MySQL no devolvió el total de usuarios.");
            }
            return rs.getInt(1);
        } catch (SQLException e) {
            throw databaseError("contar los usuarios", e);
        }
    }

    @Override
    public User authenticate(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isEmpty()) {
            return null;
        }
        String sql = SELECT_USERS + " AND email = ? AND BINARY password = ? AND state = 'Activo'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rowMapper.mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw databaseError("autenticar el usuario", e);
        }
    }

    private void setParams(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getName());
        ps.setString(2, user.getLastName());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getPhone());
        ps.setString(5, user.getPassword());
        ps.setString(6, user.getState());
        ps.setString(7, user.getCity());
    }

    private void requireUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Debe indicar el usuario.");
        }
    }

    private void requireId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor que cero.");
        }
    }

    private RuntimeException databaseError(String action, SQLException cause) {
        if (cause.getErrorCode() == 1062) {
            return new IllegalArgumentException("Ya existe una persona con ese correo electrónico.", cause);
        }
        if (cause.getSQLState() != null && cause.getSQLState().startsWith("23")) {
            return new IllegalArgumentException("No se pudo " + action + ": revise los datos y sus relaciones.", cause);
        }
        return new IllegalStateException("No se pudo " + action + " en MySQL.", cause);
    }
}
