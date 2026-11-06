package app.persistence.repository;

import app.domain.Order;
import app.service.outputports.OrderRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Read-only order adapter for the administration list and receipt lookups. */
public final class OrderRepositoryAdapterDB implements OrderRepositoryPort {
    private final Connection connection;
    private final PaymentRepositoryAdapterDB receipts;
    public OrderRepositoryAdapterDB(Connection connection, PaymentRepositoryAdapterDB receipts) {
        this.connection = Objects.requireNonNull(connection);
        this.receipts = Objects.requireNonNull(receipts);
    }
    @Override public Order selectByOrderId(Integer id) { return receipts.findOrderById(id); }
    @Override public List<Order> selectAllOrders() {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT order_id FROM `order` ORDER BY order_id");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) ids.add(result.getInt("order_id"));
        } catch (SQLException error) { throw new IllegalStateException("No se pudieron consultar las órdenes en MySQL.", error); }
        List<Order> orders = new ArrayList<>();
        for (Integer id : ids) {
            Order order = receipts.findOrderById(id);
            if (order != null) orders.add(order);
        }
        return orders;
    }
    @Override public Order save(Order order) { throw new UnsupportedOperationException("El panel consulta órdenes; su creación se realiza desde el flujo de compra."); }
    @Override public Order updateOrder(Order order) { throw new UnsupportedOperationException("El panel no modifica órdenes."); }
    @Override public void deleteById(Integer id) { throw new UnsupportedOperationException("El panel no elimina órdenes."); }
}
