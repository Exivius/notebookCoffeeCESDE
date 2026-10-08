package app.view;


import app.domain.Order;
import app.domain.Payment;
import app.domain.PaymentMethod;
import app.domain.User;
import app.service.helpers.SetPaymentMethod;
import app.service.inputports.PaymentServiceInterface;
import app.service.validators.DataTypeValidator;

import java.time.LocalDateTime;

public class PaymentView {

    //Intección de dependencia servicio
    private final PaymentServiceInterface paymentServiceInterface;

    public PaymentView(PaymentServiceInterface paymentServiceInterface) {
        this.paymentServiceInterface = paymentServiceInterface;
    }

    public void createPayment() {
        int userId = DataTypeValidator.validateInt("Ingrese el ID del usuario: ");
        int orderId = DataTypeValidator.validateInt("Ingrese el ID de la orden: ");
        createPayment(userId, orderId);
    }

    public void createPayment(User user) {
        int orderId = DataTypeValidator.validateInt("Ingrese el ID de la orden: ");
        createPayment(user.getId(), orderId);
    }

    private void createPayment(int userId, int orderId) {
        String method = SetPaymentMethod.getPaymentMethod();
        Payment payment = paymentServiceInterface.createPayment(null, userId, orderId, 0D,
                method, "paid", LocalDateTime.now().toString());
        if (payment == null) {
            System.out.println("No existe la orden indicada.");
            return;
        }
        double total = 0;
        for (int i = 0; i < payment.getOrder().getProductList().size(); i++) {
            total += payment.getOrder().getProductList().get(i).getProductPrice()
                    * payment.getOrder().getQuantities().get(i);
        }
        payment.setAmount(total);
        paymentServiceInterface.updatePayment(payment);
        System.out.println("Pago registrado correctamente. ID: " + payment.getPaymentId());
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
        Payment updated = new Payment(currentPayment.getPaymentId(),
                new PaymentMethod(null, method), currentPayment.getOrder());
        updated.setUserId(currentPayment.getUserId());
        updated.setAmount(currentPayment.getAmount());
        updated.setPaymentDate(currentPayment.getPaymentDate());
        updated.setPaymentStatus(currentPayment.getPaymentStatus());
        paymentServiceInterface.updatePayment(updated);
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
        System.out.println("ID del Usuario: " + payment.getUserId());
        for (int i = 0; i < payment.getOrder().getProductList().size(); i++) {
            System.out.println("Producto: " + payment.getOrder().getProductList().get(i).getProductName()
                    + " | Cantidad: " + payment.getOrder().getQuantities().get(i)
                    + " | Precio: " + payment.getOrder().getProductList().get(i).getProductPrice());
        }
        System.out.println("Total: " + payment.getAmount());
    }
}
