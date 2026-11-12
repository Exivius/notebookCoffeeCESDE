package app.persistence.repository;

import app.domain.PaymentMethod;
import app.persistence.mappers.PaymentMethodMapper;
import app.persistence.mappers.RowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaymentMethodRepository implements app.service.outputports.PaymentMethodRepository {
    private final Connection connection;
    private final RowMapper<PaymentMethod> rowMapper;

    public PaymentMethodRepository(Connection connection) {
        this(connection, new PaymentMethodMapper());
    }

    public PaymentMethodRepository(Connection connection, RowMapper<PaymentMethod> rowMapper) {
        this.connection = Objects.requireNonNull(connection);
        this.rowMapper = Objects.requireNonNull(rowMapper);
    }

    @Override
    public PaymentMethod save(PaymentMethod method) {
        String sql = "INSERT INTO payment_methods (method_name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, method.getMethodName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new IllegalStateException("No se obtuvo el ID del método de pago creado.");
                }
                method.setMethodId(keys.getInt(1));
            }
            return method;
        } catch (SQLException e) {
            throw databaseError("crear el método de pago", e);
        }
    }

    @Override
    public PaymentMethod findById(int id) {
        String sql = "SELECT method_id, method_name FROM payment_methods WHERE method_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rowMapper.mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw databaseError("consultar el método de pago", e);
        }
    }

    @Override
    public List<PaymentMethod> findAll() {
        String sql = "SELECT method_id, method_name FROM payment_methods ORDER BY method_id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<PaymentMethod> methods = new ArrayList<>();
            while (rs.next()) {
                methods.add(rowMapper.mapRow(rs));
            }
            return methods;
        } catch (SQLException e) {
            throw databaseError("listar los métodos de pago", e);
        }
    }

    @Override
    public PaymentMethod update(PaymentMethod method) {
        String sql = "UPDATE payment_methods SET method_name = ? WHERE method_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, method.getMethodName());
            ps.setInt(2, method.getMethodId());
            if (ps.executeUpdate() > 0) {
                return method;
            }
            // Algunos controladores informan cero si el nombre no cambió.
            return findById(method.getMethodId());
        } catch (SQLException e) {
            throw databaseError("actualizar el método de pago", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM payment_methods WHERE method_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new IllegalStateException("No se puede eliminar un método utilizado en pagos.", e);
            }
            throw databaseError("eliminar el método de pago", e);
        }
    }

    private IllegalStateException databaseError(String action, SQLException cause) {
        return new IllegalStateException("No se pudo " + action + ".", cause);
    }
}
