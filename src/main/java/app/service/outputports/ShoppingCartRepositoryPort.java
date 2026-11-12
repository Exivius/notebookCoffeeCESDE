package app.service.outputports;

import app.domain.ShoppingCart;
import java.util.List;

public interface ShoppingCartRepositoryPort {
    ShoppingCart save(ShoppingCart cart);
    ShoppingCart selectById(Integer cartId);
    List<ShoppingCart> selectAll();
    ShoppingCart update(ShoppingCart cart);
    void deleteById(Integer cartId);
}
