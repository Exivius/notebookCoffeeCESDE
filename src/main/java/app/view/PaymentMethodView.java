package app.view;

import app.domain.PaymentMethod;
import app.service.inputports.PaymentMethodService;
import app.service.validators.DataTypeValidator;
import java.util.List;

public class PaymentMethodView {
    private final PaymentMethodService paymentMethodService;

    public PaymentMethodView(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    public void create() {
        String name = DataTypeValidator.validateString("Nombre del método de pago:");
        PaymentMethod method = paymentMethodService.create(name);
        System.out.println("Método creado con ID: " + method.getMethodId());
    }

    public void selectById() {
        int id = DataTypeValidator.validateInt("ID del método de pago:");
        PaymentMethod method = paymentMethodService.selectById(id);
        if (method == null) {
            System.out.println("No existe un método de pago con ese ID.");
            return;
        }
        print(method);
    }

    public void selectAll() {
        List<PaymentMethod> methods = paymentMethodService.selectAll();
        if (methods.isEmpty()) {
            System.out.println("No hay métodos de pago registrados.");
            return;
        }
        for (PaymentMethod method : methods) {
            print(method);
        }
    }

    public void update() {
        int id = DataTypeValidator.validateInt("ID del método de pago a actualizar:");
        if (paymentMethodService.selectById(id) == null) {
            System.out.println("No existe un método de pago con ese ID.");
            return;
        }
        String name = DataTypeValidator.validateString("Nuevo nombre del método de pago:");
        PaymentMethod updated = paymentMethodService.update(id, name);
        System.out.println(updated == null ? "No existe un método de pago con ese ID."
                : "Método actualizado correctamente.");
    }

    public void deleteById() {
        int id = DataTypeValidator.validateInt("ID del método de pago a eliminar:");
        System.out.println(paymentMethodService.deleteById(id) ? "Método eliminado correctamente."
                : "No existe un método de pago con ese ID.");
    }

    public PaymentMethod selectForPayment() {
        List<PaymentMethod> methods = paymentMethodService.selectAll();
        if (methods.isEmpty()) {
            System.out.println("No hay métodos de pago registrados.");
            return null;
        }
        for (PaymentMethod method : methods) {
            print(method);
        }
        while (true) {
            int id = DataTypeValidator.validateInt("Seleccione el ID del método de pago (0 para cancelar):");
            if (id == 0) {
                return null;
            }
            if (id > 0) {
                PaymentMethod method = paymentMethodService.selectById(id);
                if (method != null) {
                    return method;
                }
            }
            System.out.println("Seleccione un método de pago existente.");
        }
    }

    private void print(PaymentMethod method) {
        System.out.println(method.getMethodId() + " | " + method.getMethodName());
    }
}
