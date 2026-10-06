package app.service;

import app.domain.Payment;
import app.domain.PaymentMethod;
import app.domain.Order;
import app.service.inputports.PaymentServiceInterface;
import app.service.outputports.PaymentRepositoryPort;

import java.util.List;

public class PaymentServiceAdapter implements PaymentServiceInterface {

    private final PaymentRepositoryPort paymentRepositoryPort;

    public PaymentServiceAdapter(PaymentRepositoryPort paymentRepositoryPort) {
        this.paymentRepositoryPort = paymentRepositoryPort;
    }

    @Override
    public Payment createPayment(Integer id, Integer userId, Integer orderId, Double amount,
                                 String paymentMethod, String paymentStatus, String paymentDate) {
        Payment payment = new Payment(id, new PaymentMethod(null, paymentMethod),
                new Order(orderId, null, null, null));
        return paymentRepositoryPort.save(payment);
    }

    @Override
    public List<Payment> selectAllPayments() {
        return paymentRepositoryPort.findAll();
    }

    @Override
    public Payment selectPaymentById(int id) {
        return paymentRepositoryPort.findById(id);
    }

    @Override
    public Payment deletePaymentdById(int id) {
        paymentRepositoryPort.deleteById(id);
        return null;
    }

    @Override
    public Payment updatePayment(Integer id, Integer userId, Integer orderId, Double amount, String paymentMethod, String paymentStatus, String paymentDate) {
        Payment payment = new Payment(id, new PaymentMethod(null, paymentMethod),
                new Order(orderId, null, null, null));
        return paymentRepositoryPort.update(payment);
    }

    @Override
    public Payment generateReceipt() {
        return null;
    }
}
