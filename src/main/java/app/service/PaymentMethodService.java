package app.service;

import app.domain.PaymentMethod;
import app.service.outputports.PaymentMethodRepository;
import java.util.List;
import java.util.Objects;

public class PaymentMethodService implements app.service.inputports.PaymentMethodService {
    private final PaymentMethodRepository repository;

    public PaymentMethodService(PaymentMethodRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public PaymentMethod create(String name) {
        return repository.save(new PaymentMethod(null, validateName(name)));
    }

    @Override
    public PaymentMethod selectById(int id) {
        validateId(id);
        return repository.findById(id);
    }

    @Override
    public List<PaymentMethod> selectAll() {
        return repository.findAll();
    }

    @Override
    public PaymentMethod update(int id, String name) {
        validateId(id);
        return repository.update(new PaymentMethod(id, validateName(name)));
    }

    @Override
    public boolean deleteById(int id) {
        validateId(id);
        return repository.deleteById(id);
    }

    private String validateName(String name) {
        if (name == null || name.isBlank() || name.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre del método debe tener entre 1 y 100 caracteres.");
        }
        return name.trim();
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del método debe ser mayor que cero.");
        }
    }
}
