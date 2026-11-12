package app.service.inputports;

import app.domain.Item;
import app.domain.ShoppingCart;
import java.util.List;

public interface ShoppingCartServiceInterface {
    ShoppingCart createCart();
    ShoppingCart selectById(Integer cartId);
    List<ShoppingCart> selectAll();
    ShoppingCart updateCart(ShoppingCart cart);
    void deleteById(Integer cartId);
    Item addItem(Integer cartId, Item item);
    void removeItem(Integer cartId, Integer itemId);
}
