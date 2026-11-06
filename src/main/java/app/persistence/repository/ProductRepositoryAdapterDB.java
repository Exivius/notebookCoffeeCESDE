package app.persistence.repository;

import app.domain.Products;
import app.persistence.mappers.RowMapper;
import app.service.outputports.ProductRepositoryPort;

import java.sql.Connection;
import java.util.List;

public class ProductRepositoryAdapterDB implements ProductRepositoryPort {
    private final Connection connection;
    private final RowMapper<Products> rowMapper;

    public ProductRepositoryAdapterDB(Connection connection, RowMapper<Products> rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public Products save(Products products) {
        return null;
    }

    @Override
    public Products selectById(Integer id) {
        return null;
    }

    @Override
    public List<Products> selectAllProducts() {
        return List.of();
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
