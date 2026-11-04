package app.domain;

public class Item {
    private Integer productId;
    private Products product;
    private Integer quantity;
    private Float priceByQuantity;
    private Float total;

    public Item() {
    }

    public Item(Products product, Integer quantity) {
        createItem(product, quantity);
    }

    public void createItem(Products product, Integer quantity) {
        if (product == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        this.product = product;
        this.productId = product.getProductId();
        this.quantity = quantity;
        updateTotal();
    }

    public Integer getProductId() {
        return productId;
    }

    public Products getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Float getPriceByQuantity() {
        return priceByQuantity;
    }

    public Float getTotal() {
        return total;
    }

    public Float updateQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        this.quantity = quantity;
        updateTotal();
        return total;
    }

    private void updateTotal() {
        priceByQuantity = product.getProductPrice();
        total = priceByQuantity * quantity;
    }
}
