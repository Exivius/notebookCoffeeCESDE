package app.view;


import app.domain.Order;
import app.domain.Payment;
import app.domain.PaymentMethod;
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
        Order order = new Order();
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
        int id = DataTypeValidator.validateInt("Ingrese el ID del pago a actualizar: ");
        Payment currentPayment = paymentServiceInterface.selectPaymentById(id);
        if (currentPayment == null) {
            System.out.println("No se encontró un pago con ese id");
            return;
        }
        String method = SetPaymentMethod.getPaymentMethod();
        double amount = DataTypeValidator.validateDouble("Ingrese el monto del pago: ");
        Order order = new Order();
        paymentServiceInterface.updatePayment(new Payment(currentPayment.getPaymentId(), new PaymentMethod(null, method), order));
    }

    public void generateReceipt(){
        int id = DataTypeValidator.validateInt("Ingrese el ID del pago para generar el recibo: ");
        Payment payment = paymentServiceInterface.selectPaymentById(id);
        if (payment == null) {
            System.out.println("No se encontró un pago con ese id");
            return;
        }
        System.out.println("Recibo de Pago");
        System.out.println("ID del Pago: " + payment.getPaymentId());
        System.out.println("Método de Pago: " + payment.getPaymentMethod().getMethodName());
        System.out.println("Fecha: " + payment.getPaymentDate());
        System.out.println("Numero de Orden: " + payment.getOrder().getOrderId());
    }
}
