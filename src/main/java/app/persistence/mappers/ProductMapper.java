package app.persistence.mappers;

import app.domain.Products;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper implements RowMapper<Products> {
    @Override
    public Products mapRow(ResultSet rs) throws SQLException {
        Products products = new Products();
        products.setProductId(rs.getInt("product_id"));
        products.setProductName(rs.getString("product_name"));
        products.setProductPrice(rs.getFloat("product_price"));
        products.setProductType(rs.getString("product_type"));
        return products;
    }
}
