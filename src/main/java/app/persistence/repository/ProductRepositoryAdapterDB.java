package app.persistence.repository;

import app.domain.Products;
import app.persistence.mappers.RowMapper;
import app.service.outputports.ProductRepositoryPort;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryAdapterDB implements ProductRepositoryPort {
    private final Connection connection;
    private final RowMapper<Products> rowMapper;

    public ProductRepositoryAdapterDB(Connection connection, RowMapper<Products> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Products save(Products products) {
        String sql = "INSERT INTO products (product_name, product_price, product_type) VALUES (?, ?, ?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            setProductParams(ps, products);
            ps.executeUpdate();
            System.out.println("Se ha guardado con éxito");
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el producto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<Products> selectById(Integer id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return Optional.of(rowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el producto: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Products> selectAllProducts() {
        List<Products> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                products.add(rowMapper.mapRow(rs));

            }
        } catch (SQLException e){
            throw new RuntimeException("Error al consultar los productos: " + e.getMessage());
        }
        return products;
    }

    @Override
    public Products updateProduct(Products product) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }

    private void setProductParams(java.sql.PreparedStatement ps, Products product) throws java.sql.SQLException {
        ps.setString(1, product.getProductName());
        ps.setDouble(2, product.getProductPrice());
        ps.setString(3, product.getProductType());

    }
}
