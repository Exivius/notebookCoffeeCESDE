package app.persistence.repository;

import app.domain.ShoppingCart;
import app.service.outputports.ShoppingCartRepositoryPort;

import java.sql.Connection;
import java.util.List;

public class ShoppingCartRepositoryAdapterDB implements ShoppingCartRepositoryPort {

    private final Connection connection;

    public ShoppingCartRepositoryAdapterDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        return null;
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
}
