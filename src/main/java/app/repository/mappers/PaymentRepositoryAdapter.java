package app.repository.mappers;

import app.domain.Payment;
import app.service.outputports.PaymentRepositoryPort;

import java.util.ArrayList;
import java.util.List;

public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    List<Payment> payments = new ArrayList<>();

    @Override
    public Payment save(Payment payment) {
        payment.setPaymentId(nextId());
        payments.add(payment);
        return payment;
    }

    private int nextId() {
        int id = 1;
        while (findById(id) != null) {
            id++;
        }
        return id;
    }

    @Override
    public List<Payment> findAll() {
        for(Payment payment : payments) {
            System.out.println(payment.getPaymentId() + " " + payment.getPaymentMethod() + " " + payment.getOrder().getOrderId());
        }
        return payments;
    }

    @Override
    public Payment findById(int id) {
        for (Payment payment : payments) {
            if (payment.getPaymentId() != null && payment.getPaymentId().equals(id)) {
                return payment;
            }
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        payments.removeIf(payment -> payment.getPaymentId() != null && payment.getPaymentId().equals(id));
    }

    @Override
    public Payment update(Payment payment) {
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getPaymentId() != null
                    && payments.get(i).getPaymentId().equals(payment.getPaymentId())) {
                payments.set(i, payment);
                return payment;
            }
        }
        return null;
    }
}
