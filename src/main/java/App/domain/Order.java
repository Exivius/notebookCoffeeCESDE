package App.domain;

public class Order {
    private Integer orderId;
    private String place;
    private String user;
    private String products;

    // constructor vacio
    public Order() {}

    public Order(Integer orderId, String place, String user, String products) {
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
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
