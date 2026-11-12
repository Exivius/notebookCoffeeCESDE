package app.service.outputports;

import app.domain.Item;
import java.util.List;

public interface ItemRepositoryPort {
    Item save(Item item);
    Item selectById(Integer itemId);
    List<Item> selectByCartId(Integer cartId);
    Item update(Item item);
    void deleteById(Integer itemId);
}
