package app.service.inputports;

import app.domain.Item;
import app.domain.Products;
import java.util.List;

public interface ItemServiceInterface {
    Item createItem(Integer cartId, Products product, Integer quantity);
    Item selectById(Integer itemId);
    List<Item> selectByCartId(Integer cartId);
    Item updateItem(Item item);
    void deleteById(Integer itemId);
}
