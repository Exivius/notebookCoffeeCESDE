package app.persistence.repository;

import app.domain.ShoppingCart;
import app.service.outputports.ShoppingCartRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ShoppingCartRepositoryAdapterDB implements ShoppingCartRepositoryPort {

    private final Connection connection;

    public ShoppingCartRepositoryAdapterDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO shopping_cart (total) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setFloat(1, cart.calculateTotal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new IllegalStateException("No se obtuvo el ID del carrito.");
                cart.setCartId(keys.getInt(1));
            }
            return cart;
        } catch (SQLException e) {
            throw databaseError("crear el carrito", e);

        }
    }

    @Override
    public ShoppingCart selectById(Integer cartId) {
        return null;
    }

    @Override
    public List<ShoppingCart> selectAll() {
        return List.of();
    }

    @Override
    public ShoppingCart update(ShoppingCart cart) {
        return null;
    }

    @Override
    public void deleteById(Integer cartId) {

    }

    private IllegalStateException databaseError(String action, SQLException e) {
        return new IllegalStateException("No se pudo " + action + " en MySQL.", e);
    }
}
