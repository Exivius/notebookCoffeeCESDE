package app.persistence.mappers;

import app.domain.Item;
import app.domain.Products;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs) throws SQLException {
        Products product = new Products(rs.getInt("product_id"),
                rs.getString("product_name"), rs.getFloat("price_by_quantity"),
                rs.getString("product_type"));
        Item item = new Item(product, rs.getInt("quantity"));
        item.setItemId(rs.getInt("item_id"));
        item.setCartId(rs.getInt("cart_id"));
        return item;
    }
}
