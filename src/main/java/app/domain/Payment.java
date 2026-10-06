package app.domain;

public class Payment {
    private Integer paymentId;
    private PaymentMethod paymentMethod;
    private Order order;

    public Payment() {
    }

    public Payment(Integer id, PaymentMethod paymentMethod, Order order) {
        this.paymentId = id;
        this.paymentMethod = new PaymentMethod();
        this.order = new Order();
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
