package app.service;

import app.domain.Item;
import app.domain.ShoppingCart;
import app.service.inputports.ItemServiceInterface;
import app.service.inputports.ShoppingCartServiceInterface;
import app.service.outputports.ShoppingCartRepositoryPort;

import java.io.Serializable;
import java.util.List;

public class ShoppingCartServiceAdapter implements ShoppingCartServiceInterface{

    private final ShoppingCartRepositoryPort cartRepository;
    private final ItemServiceInterface itemService;

    public ShoppingCartServiceAdapter(ShoppingCartRepositoryPort cartRepositoryPort, ItemServiceInterface itemService) {
        this.cartRepository = cartRepositoryPort;
        this.itemService = itemService;
    }

    @Override
    public ShoppingCart createCart() {
        return cartRepository.save(new ShoppingCart());
    }

    @Override
    public ShoppingCart selectById(Integer cartId) {
        return null;
    }

    @Override
    public List<ShoppingCart> selectAll() {
        return List.of();
    }

    @Override
    public ShoppingCart updateCart(ShoppingCart cart) {
        return null;
    }

    @Override
    public void deleteById(Integer cartId) {

    }

    @Override
    public Item addItem(Integer cartId, Item item) {
        return null;
    }

    @Override
    public void removeItem(Integer cartId, Integer itemId) {

    }
}
