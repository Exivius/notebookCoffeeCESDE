package app.persistence.repository;

import app.domain.Item;
import app.persistence.mappers.RowMapper;
import app.service.outputports.ItemRepositoryPort;

import java.sql.Connection;
import java.util.List;

public class ItemRepositoryAdapterDB implements ItemRepositoryPort {

    private final Connection connection;
    private final RowMapper<Item> rowMapper;

    public ItemRepositoryAdapterDB(Connection connection, RowMapper<Item> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Item save(Item item) {
        return null;
    }

    @Override
    public Item selectById(Integer itemId) {
        return null;
    }

    @Override
    public List<Item> selectByCartId(Integer cartId) {
        return List.of();
    }

    @Override
    public Item update(Item item) {
        return null;
    }

    @Override
    public void deleteById(Integer itemId) {

    }
}
