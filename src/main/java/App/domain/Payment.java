package App.domain;

public class Payment {
    private Integer paymentId;
    private String paymentType;
    private paymentMethod paymentMethod;
    private Order order;

    public Payment() {
    }

    public Payment(Integer paymentId, String paymentType, paymentMethod paymentMethod, Order order) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.order = order;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public paymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(paymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    //metodos
    public void createPayment() {
    }

    public void selectAllPayments() {
    }

    public void selectPaymentById() {
    }

    public void deletePaymentdById() {
    }

    public void updatePayment() {
    }

    public void generateReceipt(){}
}
