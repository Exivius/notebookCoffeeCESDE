package App.domain;

public class Payment {
    private Integer paymentId;
    private String paymentType;
    private App.domain.paymentMethod paymentMethod;


    public Payment(Integer paymentId, String paymentType, paymentMethod paymentMethod) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
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
