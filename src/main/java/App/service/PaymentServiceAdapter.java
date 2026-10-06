package app.service;

import app.domain.Payment;
import app.service.inputports.PaymentServiceInterface;
import app.service.outputports.PaymentRepositoryPort;

import java.util.List;

public class PaymentServiceAdapter implements PaymentServiceInterface {

    private final PaymentRepositoryPort paymentRepositoryPort;

    public PaymentServiceAdapter(PaymentRepositoryPort paymentRepositoryPort) {
        this.paymentRepositoryPort = paymentRepositoryPort;
    }

    @Override
    public Payment createPayment(Integer id, String paymentMethod, String paymentStatus, String paymentDate) {
        Payment payment = new Payment(id, userId, orderId, amount, paymentMethod, paymentStatus, paymentDate);
        return paymentRepositoryPort.save(payment);
    }

    @Override
    public List<Payment> selectAllPayments() {
        return List.of();
    }

    @Override
    public Payment selectPaymentById(int id) {
        return null;
    }

    @Override
    public Payment deletePaymentdById(int id) {
        return null;
    }

    @Override
    public Payment updatePayment(Integer id, Integer userId, Integer orderId, Double amount, String paymentMethod, String paymentStatus, String paymentDate) {
        return null;
    }

    @Override
    public Payment generateReceipt() {
        return null;
    }
}
