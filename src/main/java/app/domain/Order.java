package app.domain;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer orderId;
    private Place place;
    private User user;
    private List<Products> products = new ArrayList<>();
    private List<Integer> quantities = new ArrayList<>();

    public Order() {}

    public Order(Integer orderId, Place place, User user, Products product) {
        this.orderId = orderId;
        this.place = place;
        this.user = user;
        if (product != null) {
            products.add(product);
            quantities.add(1);
        }
    }

    public Order(Integer orderId, Place place, User user, List<Products> products,
                 List<Integer> quantities) {
        this.orderId = orderId;
        this.place = place;
        this.user = user;
        this.products = new ArrayList<>(products);
        this.quantities = new ArrayList<>(quantities);
    }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Products getProducts() {
        return products.isEmpty() ? null : products.get(0);
    }

    public void setProducts(Products product) {
        products.clear();
        quantities.clear();
        if (product != null) {
            products.add(product);
            quantities.add(1);
        }
    }

    public List<Products> getProductList() { return new ArrayList<>(products); }
    public List<Integer> getQuantities() { return new ArrayList<>(quantities); }
}
