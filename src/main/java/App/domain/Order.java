package App.domain;

public class Order {
    private Integer orderId;
    private Place place;
    private User user;
    private Products products;

    // constructor vacio
    public Order() {}

    public Order(Integer orderId, Place place, User user, Products products) {
        this.orderId = orderId;
        this.place = place;
        this.user = user;
        this.products = products;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    //metodos

    public void createOrder(){
    }
    public void selecAllOrders(){
    }
    public void selectOrderById(){
    }
    public void updateOrder(){
    }
    public void deleteOrderById(int id){
    }
    public void generateReceipt(){
    }
    public void applyDiscunt(){
    }



}
