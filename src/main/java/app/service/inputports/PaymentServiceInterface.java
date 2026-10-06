package app.service.inputports;

import app.domain.Payment;

import java.util.List;

public interface PaymentServiceInterface {
    Payment createPayment(Integer id, Integer userId, Integer orderId, Double amount, String paymentMethod, String paymentStatus, String paymentDate);

    public List<Payment> selectAllPayments();

    public Payment selectPaymentById(int id);

    public Payment deletePaymentdById(int id);

    public Payment updatePayment(Integer id, Integer userId, Integer orderId, Double amount, String paymentMethod, String paymentStatus, String paymentDate);

    public Payment generateReceipt();
}
