package app.view;


import app.service.helpers.SetPaymentMethod;
import app.service.inputports.PaymentServiceInterface;
import app.service.validators.DataTypeValidator;

import java.sql.Date;

public class PaymentView {

    //Intección de dependencia servicio
    private final PaymentServiceInterface paymentServiceInterface;

    public PaymentView(PaymentServiceInterface paymentServiceInterface) {
        this.paymentServiceInterface = paymentServiceInterface;
    }

    public void createPayment() {
        int id = Math.toIntExact(System.currentTimeMillis());
        String method = SetPaymentMethod.getPaymentMethod();
        double amount = DataTypeValidator.validateDouble("Ingrese el monto del pago: ");
        Date date = new Date(System.currentTimeMillis());
        paymentServiceInterface.createPayment(id, null, null, amount, method, "pending", date.toString());
    }

    public void selectAllPayments() {
        for (var payment : paymentServiceInterface.selectAllPayments()) {
            System.out.println(payment.getPaymentId() + " "
                    + payment.getPaymentMethod().getMethodName() + " "
                    + payment.getOrder().getOrderId());
        }
    }

    public void updatePayment() {
    }

    public void generateReceipt(){}
}
