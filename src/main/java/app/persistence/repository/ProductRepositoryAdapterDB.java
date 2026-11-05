package app.persistence.repository;

import app.domain.Products;
import app.service.outputports.ProductRepositoryPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** MySQL product catalog adapter used by the HTTP admin API. */
public final class ProductRepositoryAdapterDB implements ProductRepositoryPort {
    private final Connection connection;
    public ProductRepositoryAdapterDB(Connection connection) { this.connection = Objects.requireNonNull(connection); }

    @Override public Products save(Products value) {
        String sql = "INSERT INTO products (product_name, product_price, product_type, product_description, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            set(statement, value); statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("MySQL no devolvió el ID del producto.");
                value.setProductId(keys.getInt(1)); return value;
            }
        } catch (SQLException error) { throw databaseError("crear el producto", error); }
    }

    @Override public Products selectById(Integer id) {
        String sql = "SELECT product_id, product_name, product_price, product_type, product_description, stock FROM products WHERE product_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) { return result.next() ? map(result) : null; }
        } catch (SQLException error) { throw databaseError("consultar el producto", error); }
    }

    @Override public List<Products> selectAllProducts() {
        List<Products> values = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT product_id, product_name, product_price, product_type, product_description, stock FROM products ORDER BY product_id");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) values.add(map(result));
            return values;
        } catch (SQLException error) { throw databaseError("listar los productos", error); }
    }

    @Override public Products updateProduct(Products value) {
        String sql = "UPDATE products SET product_name = ?, product_price = ?, product_type = ?, product_description = ?, stock = ? WHERE product_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            set(statement, value); statement.setInt(6, value.getProductId());
            if (statement.executeUpdate() == 0) return selectById(value.getProductId());
            return value;
        } catch (SQLException error) { throw databaseError("actualizar el producto", error); }
    }

    @Override public void deleteById(Integer id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE product_id = ?")) {
            statement.setInt(1, id); statement.executeUpdate();
        } catch (SQLException error) {
            if (error.getErrorCode() == 1451) throw new IllegalStateException("No se puede eliminar un producto asociado a órdenes.", error);
            throw databaseError("eliminar el producto", error);
        }
    }

    private Products map(ResultSet row) throws SQLException {
        int stock = row.getInt("stock");
        Integer available = row.wasNull() ? null : stock;
        return new Products(row.getInt("product_id"), row.getString("product_name"),
                row.getFloat("product_price"), row.getString("product_type"),
                row.getString("product_description"), available);
    }
    private void set(PreparedStatement statement, Products value) throws SQLException {
        statement.setString(1, value.getProductName()); statement.setFloat(2, value.getProductPrice());
        statement.setString(3, value.getProductType()); statement.setString(4, value.getDescription());
        if (value.getStock() == null) statement.setNull(5, Types.INTEGER);
        else statement.setInt(5, value.getStock());
    }
    private RuntimeException databaseError(String action, SQLException error) {
        if (error.getSQLState() != null && error.getSQLState().startsWith("23"))
            return new IllegalArgumentException("No se pudo " + action + " por una restricción de la base de datos.", error);
        return new IllegalStateException("No se pudo " + action + " en MySQL.", error);
    }
}
