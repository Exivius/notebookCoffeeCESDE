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
        validateId(cartId);
        ShoppingCart cart = cartRepository.selectById(cartId);
        return hydrate(cart);
    }

    @Override
    public List<ShoppingCart> selectAll() {
        List<ShoppingCart> carts = cartRepository.selectAll();
        for (ShoppingCart cart : carts) {
            hydrate(cart);
        }
        return carts;
    }

    @Override
    public ShoppingCart updateCart(ShoppingCart cart) {
        validateCart(cart);
        cart.calculateTotal();
        ShoppingCart updated = cartRepository.update(cart);
        if (updated == null) {
            return null;
        }
        for (Item current : itemService.selectByCartId(cart.getCartId())) {
            itemService.deleteById(current.getItemId());
        }
        for (Item item : cart.getItems()) {
            item.setCartId(cart.getCartId());
            itemService.createItem(cart.getCartId(), item.getProduct(), item.getQuantity());
        }
        return hydrate(updated);
    }

    @Override
    public void deleteById(Integer cartId) {
        validateId(cartId);
        cartRepository.deleteById(cartId);
    }

    @Override
    public Item addItem(Integer cartId, Item item) {
        validateId(cartId);
        if (item == null) {
            throw  new IllegalArgumentException("item no puede ser nulo");
        }
        item.setCartId(cartId);
        Item saved = itemService.createItem(cartId, item.getProduct(), item.getQuantity());
        ShoppingCart cart = selectById(cartId);
        cartRepository.update(cart);
        return saved;
    }

    @Override
    public void removeItem(Integer cartId, Integer itemId) {
        validateId(cartId);
        itemService.deleteById(itemId);
        ShoppingCart cart = selectById(cartId);
        cartRepository.update(cart);
    }

    private void validateCart(ShoppingCart cart) {
        if (cart == null || cart.getCartId() == null) {
            throw new IllegalArgumentException("Debe indicar un carrito con ID.");
        }
    }

    //Verifica que el carrito no sea nulo, limpia el carrito y devuelve el carrito actualizado
    private ShoppingCart hydrate(ShoppingCart cart) {
        if (cart == null) {
            cart.clearCart();
            for (Item item : itemService.selectByCartId(cart.getCartId())) {
                cart.addItem(item);
            }
        }
        return cart;
    }

    private void validateId(Integer id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("El ID del carrito debe ser mayor que cero.");
        }
    }
}
