package app.domain;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private Integer cartId;
    private final List<Item> items = new ArrayList<>();
    private Float total = 0F;

    public ShoppingCart(Integer cartId, Float total) {
        this.cartId = cartId;
        this.total = total;
    }

    public ShoppingCart() {
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    //metodos

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("El item no puede ser nulo.");
        }
        items.add(item);
        calculateTotal();
    }

    public void removeItem(Integer productId) {
        items.removeIf(item -> productId != null && productId.equals(item.getProductId()));
        calculateTotal();
    }

    public Float calculateTotal() {
        total = 0F;
        for (Item item : items) {
            total += item.getTotal();
        }
        return total;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public void clearCart() {
        items.clear();
        total = 0F;
    }
}
