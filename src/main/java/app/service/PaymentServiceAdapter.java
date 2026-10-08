package app.service;

import app.domain.Payment;
import app.domain.PaymentMethod;
import app.domain.Order;
import app.service.inputports.PaymentServiceInterface;
import app.service.outputports.PaymentRepositoryPort;
import app.service.inputports.OrderServiceInterface;

import java.util.List;

public class PaymentServiceAdapter implements PaymentServiceInterface {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final OrderServiceInterface orderService;

    public PaymentServiceAdapter(PaymentRepositoryPort paymentRepositoryPort, OrderServiceInterface orderService) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.orderService = orderService;
    }

    @Override
    public Payment createPayment(Integer id, Integer userId, Integer orderId, Double amount,
                                 String paymentMethod, String paymentStatus, String paymentDate) {
        Order order = orderService.selectByOrderId(orderId);
        if (order == null) {
            return null;
        }
        Payment payment = new Payment(id, new PaymentMethod(null, paymentMethod), order);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setPaymentStatus(paymentStatus);
        payment.setPaymentDate(paymentDate);
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
    public Payment updatePayment(Payment payment) {
        return paymentRepositoryPort.update(payment);
    }

    @Override
    public Payment generateReceipt() {
        return null;
    }
}
