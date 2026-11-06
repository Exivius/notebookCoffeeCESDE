package app.persistence.repository;

import app.domain.Order;
import app.persistence.mappers.RowMapper;
import app.service.outputports.OrderRepositoryPort;

import java.sql.Connection;
import java.util.List;

public class OrderRepositoryAdapterDB implements OrderRepositoryPort {
    private final Connection connection;
    private final RowMapper rowMapper;

    public OrderRepositoryAdapterDB(Connection connection, RowMapper rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public Order selectByOrderId(Integer orderId) {
        return null;
    }

    @Override
    public List<Order> selectAllOrders() {
        return List.of();
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }

    @Override
    public void deleteById(Integer Id) {

    }
}
