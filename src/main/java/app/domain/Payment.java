package app.domain;

public class Payment {
    private Integer paymentId;
    private PaymentMethod paymentMethod;
    private Order order;
    private String paymentDate;
    private Integer userId;
    private Double amount;
    private String paymentStatus;

    public Payment() {
    }

    public Payment(Integer id, PaymentMethod paymentMethod, Order order) {
        this.paymentId = id;
        this.paymentMethod = paymentMethod;
        this.order = order;
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

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
