package app.service.outputports;

import app.domain.PaymentMethod;
import java.util.List;

public interface PaymentMethodRepository {
    PaymentMethod save(PaymentMethod method);
    PaymentMethod findById(int id);
    List<PaymentMethod> findAll();
    PaymentMethod update(PaymentMethod method);
    boolean deleteById(int id);
}
