package app.service.inputports;

import app.domain.Payment;
import java.util.List;

public interface PaymentServiceInterface {
    Payment createPayment(Integer userId, Integer orderId, String paymentType, Integer methodId);
    Payment createPayment(Integer id, Integer userId, Integer orderId, Double amount,
                          String paymentMethod, String paymentStatus, String paymentDate);
    List<Payment> selectAllPayments();
    Payment selectPaymentById(int id);
    Payment deletePaymentdById(int id);
    Payment updatePayment(Payment payment);
    Payment generateReceipt(int id);
    Payment generateReceipt(int id, Integer userId);

    // El recibo siempre requiere identificar el pago.
    default Payment generateReceipt() {
        throw new IllegalArgumentException("Debe indicar el ID del pago para generar el recibo.");
    }
}
