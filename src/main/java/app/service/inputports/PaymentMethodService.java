package app.service.inputports;

import app.domain.PaymentMethod;
import java.util.List;

public interface PaymentMethodService {
    PaymentMethod create(String name);
    PaymentMethod selectById(int id);
    List<PaymentMethod> selectAll();
    PaymentMethod update(int id, String name);
    boolean deleteById(int id);
}
