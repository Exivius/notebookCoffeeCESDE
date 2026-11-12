package app;

import app.domain.PaymentMethod;
import app.service.PaymentMethodService;
import app.service.outputports.PaymentMethodRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Ejecutar con java -ea app.PaymentMethodChecks después de compilar las fuentes. */
public class PaymentMethodChecks {
    public static void main(String[] args) {
        MemoryRepository repository = new MemoryRepository();
        PaymentMethodService service = new PaymentMethodService(repository);
        PaymentMethod first = service.create("  Nequi  ");
        check(first.getMethodId() == 1, "Debe conservar el ID generado por el repositorio");
        check("Nequi".equals(first.getMethodName()), "Debe normalizar espacios");
        PaymentMethod second = service.create("Bancolombia A la Mano");
        check(service.selectAll().size() == 2, "Debe listar el catálogo completo");
        check(service.selectById(first.getMethodId()) == first, "Debe consultar por ID");
        check("Efectivo".equals(service.update(1, "Efectivo").getMethodName()), "Debe actualizar el nombre");
        check(service.update(99, "Tarjeta") == null, "Actualizar un ID inexistente no debe crearlo");
        check(service.selectById(99) == null, "Una consulta sin resultado debe devolver null");
        check(service.deleteById(second.getMethodId()), "Debe informar eliminación exitosa");
        check(!service.deleteById(second.getMethodId()), "Debe informar un ID inexistente al eliminar");
        int saves = repository.saveCalls;
        expectInvalid(() -> service.create(" "));
        expectInvalid(() -> service.create(null));
        expectInvalid(() -> service.create("a".repeat(101)));
        expectInvalid(() -> service.selectById(0));
        expectInvalid(() -> service.update(-1, "Nequi"));
        expectInvalid(() -> service.update(1, ""));
        expectInvalid(() -> service.deleteById(-1));
        check(repository.saveCalls == saves, "Datos inválidos no deben llegar al repositorio");
        System.out.println("PaymentMethodChecks: correcto");
    }

    private static void expectInvalid(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Se esperaba una validación fallida");
        } catch (IllegalArgumentException expected) {
            // Validación esperada.
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static class MemoryRepository implements PaymentMethodRepository {
        private final Map<Integer, PaymentMethod> methods = new LinkedHashMap<>();
        private int nextId = 1;
        private int saveCalls;

        public PaymentMethod save(PaymentMethod method) {
            saveCalls++;
            method.setMethodId(nextId++);
            methods.put(method.getMethodId(), method);
            return method;
        }

        public PaymentMethod findById(int id) { return methods.get(id); }
        public List<PaymentMethod> findAll() { return new ArrayList<>(methods.values()); }

        public PaymentMethod update(PaymentMethod method) {
            if (!methods.containsKey(method.getMethodId())) return null;
            methods.put(method.getMethodId(), method);
            return method;
        }

        public boolean deleteById(int id) { return methods.remove(id) != null; }
    }
}
