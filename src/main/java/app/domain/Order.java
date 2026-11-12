package app.domain;

import java.util.List;

public class Order {
    private Integer orderId;
    private Place place;
    private User user;
    private ShoppingCart shoppingCart;

    public Order() {
        shoppingCart = new ShoppingCart();
    }

    public Order(Integer orderId, Place place, User user, Products product) {
        this(orderId, place, user, new ShoppingCart());
        if (product != null) {
            shoppingCart.addItem(new Item(product, 1));
        }
    }

    public Order(Integer orderId, Place place, User user, List<Products> products,
                 List<Integer> quantities) {
        this(orderId, place, user, new ShoppingCart());
        if (products == null || quantities == null || products.size() != quantities.size()) {
            throw new IllegalArgumentException("Productos y cantidades deben tener el mismo tamaño.");
        }
        for (int i = 0; i < products.size(); i++) {
            shoppingCart.addItem(new Item(products.get(i), quantities.get(i)));
        }
    }

    public Order(Integer orderId, Place place, User user, ShoppingCart shoppingCart) {
        this.orderId = orderId;
        this.place = place;
        this.user = user;
        this.shoppingCart = shoppingCart == null ? new ShoppingCart() : shoppingCart;
    }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ShoppingCart getShoppingCart() { return shoppingCart; }
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart == null ? new ShoppingCart() : shoppingCart;
    }

    // Compatibilidad con el contrato usado por los recibos existentes.
    public List<Integer> getQuantities() {
        return shoppingCart.getItems().stream().map(Item::getQuantity).toList();
    }
}
