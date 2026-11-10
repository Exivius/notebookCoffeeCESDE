package app.persistence.repository;

import app.domain.ShoppingCart;
import app.service.outputports.ShoppingCartRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT cart_id, total FROM shopping_cart WHERE cart_id = ?")) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new ShoppingCart(rs.getInt("cart_id"), rs.getFloat("total"));
            }
        } catch (SQLException e) {
            throw databaseError("consultar el carrito", e);
        }
    }

    @Override
    public List<ShoppingCart> selectAll() {
        List<ShoppingCart> carts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT cart_id, total FROM shopping_cart ORDER BY cart_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) carts.add(new ShoppingCart(rs.getInt("cart_id"), rs.getFloat("total")));
            return carts;
        } catch (SQLException e) {
            throw databaseError("listar los carritos", e);
        }
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
