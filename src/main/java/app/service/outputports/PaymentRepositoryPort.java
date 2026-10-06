package app.service.outputports;

import app.domain.Payment;

import java.util.List;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    List<Payment> findAll();
    Payment findById(int id);
    void deleteById(int id);
    Payment update(Payment payment);
}