package app.persistence.mappers;

import app.domain.Products;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper implements RowMapper<Products> {
    @Override
    public Products mapRow(ResultSet rs) throws SQLException {
        Products products = new Products();
        return products;
    }
}
