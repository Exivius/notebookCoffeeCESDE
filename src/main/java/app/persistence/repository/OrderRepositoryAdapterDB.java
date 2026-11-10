package app.persistence.repository;

import app.domain.Order;
import app.domain.Place;
import app.domain.ShoppingCart;
import app.domain.User;
import app.persistence.mappers.RowMapper;
import app.service.outputports.OrderRepositoryPort;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryAdapterDB implements OrderRepositoryPort {
    private final Connection connection;
    private final RowMapper<Order> rowMapper;

    public OrderRepositoryAdapterDB(Connection connection, RowMapper<Order> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Order save(Order order) {
        requireOrder(order);
        if (order.getShoppingCart().getCartId() == null) {
            throw new IllegalArgumentException("La orden debe tener un carrito guardado.");
        }
        String sql = "INSERT INTO `order` (place_id, user_id, cart_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setId(ps, 1, order.getPlace() == null ? null : order.getPlace().getPlaceId());
            setId(ps, 2, order.getUser() == null ? null : order.getUser().getId());
            ps.setInt(3, order.getShoppingCart().getCartId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new IllegalStateException("No se obtuvo el ID de la orden.");
                order.setOrderId(keys.getInt(1));
            }
            return order;
        } catch (SQLException e) {
            throw databaseError("crear la orden", e);
        }
    }

    @Override
    public Order selectByOrderId(Integer orderId) {
        try (PreparedStatement ps = connection.prepareStatement(selectSql() + " WHERE o.order_id = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapOrder(rs) : null;
            }
        } catch (SQLException e) {
            throw databaseError("seleccionar la orden", e);
        }
    }

    @Override
    public List<Order> selectAllOrders() {
        return selectOrders(null);
    }

    @Override
    public List<Order> selectOrdersByUserId(Integer userId) {
        if (userId == null || userId < 1) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor que cero.");
        }
        return selectOrders(userId);
    }

    private List<Order> selectOrders(Integer userId) {
        List<Order> orders = new ArrayList<>();
        String sql = selectSql();
        if (userId != null) {
            sql += " WHERE o.user_id = ?";
        }
        sql += " ORDER BY o.order_id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (userId != null) {
                ps.setInt(1, userId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) orders.add(mapOrder(rs));
            return orders;
        } catch (SQLException e) {
            throw databaseError("seleccionar todas las órdenes", e);
        }
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }

    @Override
    public void deleteById(Integer Id) {

    }

    //Metodos auxiliares

    private String selectSql() {
        return "SELECT o.order_id, o.cart_id, o.place_id, o.user_id, "
                + "p.place_name, p.place_type, u.id, u.name, u.last_name, u.email, "
                + "u.phone, u.password, u.state, u.rol, u.city "
                + "FROM `order` o LEFT JOIN place p ON o.place_id = p.place_id "
                + "LEFT JOIN person u ON o.user_id = u.id";
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Place place = rs.getObject("place_id") == null ? null
                : new Place(rs.getInt("place_id"), rs.getString("place_name"), rs.getString("place_type"));
        User user = rs.getObject("user_id") == null ? null
                : new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("last_name"),
                rs.getString("email"), rs.getString("phone"), rs.getString("password"),
                rs.getString("state"), rs.getString("rol"), rs.getString("city"));
        ShoppingCart cart = new ShoppingCart();
        if (rs.getObject("cart_id") != null) cart.setCartId(rs.getInt("cart_id"));
        Order order = rowMapper.mapRow(rs);
        order.setPlace(place);
        order.setUser(user);
        order.setShoppingCart(cart);
        return order;
    }

    private void requireOrder(Order order) {
        if (order == null || order.getShoppingCart() == null) {
            throw new IllegalArgumentException("Debe indicar una orden válida.");
        }
    }

    private void setId(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) ps.setNull(index, Types.INTEGER);
        else ps.setInt(index, value);
    }

    private IllegalStateException databaseError(String action, SQLException e) {
        return new IllegalStateException("No se pudo " + action + " en MySQL.", e);
    }
}
