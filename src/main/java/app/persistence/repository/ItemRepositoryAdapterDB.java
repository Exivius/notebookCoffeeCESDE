package app.persistence.repository;

import app.domain.Item;
import app.persistence.mappers.RowMapper;
import app.service.outputports.ItemRepositoryPort;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRepositoryAdapterDB implements ItemRepositoryPort {

    private final Connection connection;
    private final RowMapper<Item> rowMapper;

    public ItemRepositoryAdapterDB(Connection connection, RowMapper<Item> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    private String selectSql() {
        return "SELECT ci.item_id, ci.cart_id, ci.product_id, ci.quantity, ci.price_by_quantity, "
                + "pr.product_name, pr.product_type FROM cart_item ci "
                + "JOIN products pr ON ci.product_id = pr.product_id";
    }

    @Override
    public Item save(Item item) {
        String sql = "INSERT INTO cart_item (cart_id, product_id, quantity, price_by_quantity, total) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            SetItemParams(ps, item);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if(!keys.next()) throw new RuntimeException("No se obtuvo el ID del item");
                item.setItemId(keys.getInt(1));
            }
            return item;
        } catch (SQLException e) {
            throw databaseError("crear el item", e);
        }
    }

    @Override
    public Item selectById(Integer itemId) {
        try (PreparedStatement ps = connection.prepareStatement(selectSql() + " WHERE ci.item_id = ?")) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rowMapper.mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw databaseError("consultar el item", e);

        }
    }

    @Override
    public List<Item> selectByCartId(Integer cartId) {
        List<Item> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                selectSql() + " WHERE ci.cart_id = ? ORDER BY ci.item_id")) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) items.add(rowMapper.mapRow(rs));
            }
            return items;
        } catch (SQLException e){
            throw databaseError("listar los items", e);
        }
    }

    @Override
    public Item update(Item item) {
        String sql = "UPDATE cart_item SET quantity = ?, price_by_quantity = ?, total = ? WHERE item_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, item.getQuantity());
            ps.setDouble(2, item.getPriceByQuantity());
            ps.setDouble(3, item.getTotal());
            ps.setInt(4, item.getItemId());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No se encontró el item con ID: " + item.getItemId());
            }
            return item;
        } catch (SQLException e) {
            throw databaseError("actualizar el item", e);
        }
    }

    @Override
    public void deleteById(Integer itemId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM cart_item WHERE item_id = ?")) {
            ps.setInt(1, itemId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No se encontró el item con ID: " + itemId);
            }
        } catch (SQLException e) {
            throw databaseError("eliminar el item", e);
        }
    }

    private void SetItemParams(PreparedStatement ps, Item item) throws SQLException {
        ps.setInt(1, item.getCartId());
        ps.setInt(2, item.getProductId());
        ps.setInt(3, item.getQuantity());
        ps.setDouble(4, item.getPriceByQuantity());
        ps.setDouble(5, item.getTotal());
    }

    private IllegalStateException databaseError(String action, SQLException e) {
        return new IllegalStateException("No se pudo " + action + " en MySQL.", e);
    }
}
