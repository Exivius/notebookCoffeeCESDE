package app.repository;

import app.domain.Item;
import app.domain.Order;
import app.domain.Payment;
import app.repository.mappers.PaymentMapper;
import app.repository.mappers.RowMapper;
import app.domain.Place;
import app.domain.Products;
import app.domain.ShoppingCart;
import app.domain.User;
import app.service.outputports.PaymentRepositoryPort;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepositoryAdapterDB implements PaymentRepositoryPort {
    private final Connection connection;
    private final RowMapper<Payment> rowMapper;
    private static final String SELECT_PAYMENTS =
            "SELECT p.payment_id, p.payment_type, p.order_id, m.method_id, m.method_name "
            + "FROM payment p JOIN payment_methods m ON p.method_id = m.method_id";

    public PaymentRepositoryAdapterDB(Connection connection) {
        this(connection, new PaymentMapper());
    }

    public PaymentRepositoryAdapterDB(Connection connection, RowMapper<Payment> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payment (payment_type, order_id, method_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, payment);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("MySQL no devolvió el ID del pago.");
                }
                payment.setPaymentId(keys.getInt(1));
            }
            return payment;
        } catch (SQLException e) {
            throw databaseError("registrar el pago", e);
        }
    }

    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_PAYMENTS + " ORDER BY p.payment_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                payments.add(rowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw databaseError("consultar los pagos", e);
        }
        // Cerramos el resultado principal antes de consultar el detalle de cada orden.
        for (Payment payment : payments) {
            hydrateOrder(payment);
        }
        return payments;
    }

    @Override
    public Payment findById(int id) {
        Payment payment = null;
        try (PreparedStatement ps = connection.prepareStatement(SELECT_PAYMENTS + " WHERE p.payment_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    payment = rowMapper.mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw databaseError("consultar el pago", e);
        }
        if (payment != null) {
            hydrateOrder(payment);
        }
        return payment;
    }

    @Override
    public Payment update(Payment payment) {
        String sql = "UPDATE payment SET payment_type = ?, order_id = ?, method_id = ? WHERE payment_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setParams(ps, payment);
            ps.setInt(4, payment.getPaymentId());
            int changed = ps.executeUpdate();
            // Un UPDATE sin cambios puede devolver cero según la configuración del driver.
            if (changed == 0 && findById(payment.getPaymentId()) == null) {
                return null;
            }
            return payment;
        } catch (SQLException e) {
            throw databaseError("actualizar el pago", e);
        }
    }

    @Override
    public void deleteById(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM payment WHERE payment_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw databaseError("eliminar el pago", e);
        }
    }

    // Lee las órdenes persistidas, no los IDs de los repositorios en memoria.
    public Order findOrderById(int id) {
        String sql = "SELECT o.order_id, o.cart_id, u.id AS user_id, u.name, u.last_name, u.email, "
                + "u.phone, u.state, u.rol, u.city, pl.place_id, pl.place_name, pl.place_type "
                + "FROM `order` o LEFT JOIN person u ON o.user_id = u.id "
                + "LEFT JOIN place pl ON o.place_id = pl.place_id WHERE o.order_id = ?";
        Order order;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                User user = null;
                if (rs.getObject("user_id") != null) {
                    user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setName(rs.getString("name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setState(rs.getString("state"));
                    user.setRol(rs.getString("rol"));
                    user.setCity(rs.getString("city"));
                }
                Place place = null;
                if (rs.getObject("place_id") != null) {
                    place = new Place(rs.getInt("place_id"), rs.getString("place_name"));
                    place.setPlaceType(rs.getString("place_type"));
                }
                ShoppingCart cart = new ShoppingCart();
                if (rs.getObject("cart_id") != null) {
                    cart.setCartId(rs.getInt("cart_id"));
                }
                order = new Order(rs.getInt("order_id"), place, user, cart);
            }
        } catch (SQLException e) {
            throw databaseError("consultar la orden del pago", e);
        }
        loadItems(order.getShoppingCart());
        return order;
    }

    private void loadItems(ShoppingCart cart) {
        if (cart.getCartId() == null) {
            return;
        }
        String sql = "SELECT ci.product_id, ci.quantity, ci.price_by_quantity, pr.product_name, pr.product_type "
                + "FROM cart_item ci JOIN products pr ON ci.product_id = pr.product_id "
                + "WHERE ci.cart_id = ? ORDER BY ci.item_id";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cart.getCartId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // El recibo usa el precio registrado en el carrito, no el precio actual del catálogo.
                    Products product = new Products(rs.getInt("product_id"), rs.getString("product_name"),
                            rs.getFloat("price_by_quantity"), rs.getString("product_type"));
                    cart.addItem(new Item(product, rs.getInt("quantity")));
                }
            }
        } catch (SQLException e) {
            throw databaseError("consultar los productos del recibo", e);
        }
    }

    private void hydrateOrder(Payment payment) {
        Order order = findOrderById(payment.getOrder().getOrderId());
        if (order == null) {
            throw new IllegalStateException("El pago referencia una orden inexistente.");
        }
        payment.setOrder(order);
        payment.setUserId(order.getUser() == null ? null : order.getUser().getId());
        double total = 0;
        for (Item item : order.getShoppingCart().getItems()) {
            total += (double) item.getProduct().getProductPrice() * item.getQuantity();
        }
        payment.setAmount(total);
    }

    private void setParams(PreparedStatement ps, Payment payment) throws SQLException {
        ps.setString(1, payment.getPaymentType());
        ps.setInt(2, payment.getOrder().getOrderId());
        ps.setInt(3, payment.getPaymentMethod().getMethodId());
    }

    private RuntimeException databaseError(String action, SQLException e) {
        if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
            return new IllegalArgumentException("No se pudo " + action
                    + ": compruebe que la orden y el método existan y que no haya relaciones que impidan la operación.", e);
        }
        return new IllegalStateException("Error de base de datos al " + action + ".", e);
    }
}
