package app.repository.mappers;

import app.domain.Payment;
import app.service.outputports.PaymentRepositoryPort;

import java.util.ArrayList;
import java.util.List;

public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    List<Payment> payments = new ArrayList<>();

    @Override
    public Payment save(Payment payment) {
        payments.add(payment);
        return payment;
    }

    @Override
    public List<Payment> findAll() {
        return List.of();
    }

    @Override
    public Payment findById(int id) {
        return null;
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public Payment update(Payment payment) {
        return null;
    }
}
