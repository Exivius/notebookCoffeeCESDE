package app.view;

import app.domain.Item;
import app.domain.Order;
import app.domain.Payment;
import app.domain.PaymentMethod;
import app.domain.User;
import app.service.inputports.PaymentMethodService;
import app.service.inputports.PaymentServiceInterface;
import app.service.validators.DataTypeValidator;
import java.util.List;

public class PaymentView {
    private final PaymentServiceInterface paymentServiceInterface;
    private final PaymentMethodService methodService;

    public PaymentView(PaymentServiceInterface paymentServiceInterface) {
        this(paymentServiceInterface, null);
    }

    public PaymentView(PaymentServiceInterface paymentServiceInterface, PaymentMethodService methodService) {
        this.paymentServiceInterface = paymentServiceInterface;
        this.methodService = methodService;
    }

    public void createPayment() {
        int userId = DataTypeValidator.validateInt("Ingrese el ID del usuario de la orden: ");
        int orderId = DataTypeValidator.validateInt("Ingrese el ID de la orden registrada: ");
        createPayment(userId, orderId);
    }

    public void createPayment(User user) {
        int orderId = DataTypeValidator.validateInt("Ingrese el ID de la orden registrada: ");
        createPayment(user.getId(), orderId);
    }

    private void createPayment(int userId, int orderId) {
        try {
            PaymentMethod method = selectMethod();
            if (method == null) {
                return;
            }
            String type = selectType();
            Payment payment = paymentServiceInterface.createPayment(userId, orderId, type, method.getMethodId());
            System.out.println("Pago registrado correctamente. ID: " + payment.getPaymentId()
                    + " | Total: " + payment.getAmount());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectAllPayments() {
        try {
            List<Payment> payments = paymentServiceInterface.selectAllPayments();
            if (payments.isEmpty()) {
                System.out.println("No hay pagos registrados.");
            }
            for (Payment payment : payments) {
                printPayment(payment);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectPaymentById() {
        try {
            int id = DataTypeValidator.validateInt("Ingrese el ID del pago: ");
            Payment payment = paymentServiceInterface.selectPaymentById(id);
            if (payment == null) {
                System.out.println("No existe un pago con ese ID.");
                return;
            }
            printPayment(payment);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updatePayment() {
        try {
            int id = DataTypeValidator.validateInt("Ingrese el ID del pago a actualizar: ");
            Payment current = paymentServiceInterface.selectPaymentById(id);
            if (current == null) {
                System.out.println("No existe un pago con ese ID.");
                return;
            }
            PaymentMethod method = selectMethod();
            if (method == null) {
                return;
            }
            int orderId = DataTypeValidator.validateInt("Ingrese el ID de la orden registrada: ");
            Order order = new Order();
            order.setOrderId(orderId);
            Payment updated = new Payment(id, method, order);
            updated.setPaymentType(selectType());
            Payment result = paymentServiceInterface.updatePayment(updated);
            System.out.println(result == null ? "No existe un pago con ese ID." : "Pago actualizado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deletePayment() {
        try {
            int id = DataTypeValidator.validateInt("Ingrese el ID del pago a eliminar: ");
            Payment payment = paymentServiceInterface.deletePaymentdById(id);
            System.out.println(payment == null ? "No existe un pago con ese ID." : "Pago eliminado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void generateReceipt() {
        generateReceiptForUser(null);
    }

    public void generateReceipt(User user) {
        generateReceiptForUser(user.getId());
    }

    private void generateReceiptForUser(Integer userId) {
        try {
            int id = DataTypeValidator.validateInt("Ingrese el ID del pago para generar el recibo: ");
            Payment payment = paymentServiceInterface.generateReceipt(id, userId);
            if (payment == null) {
                System.out.println("No existe un pago con ese ID.");
                return;
            }
            System.out.println("Recibo de Pago");
            printPayment(payment);
            System.out.println("ID del Usuario: " + payment.getUserId());
            if (payment.getOrder().getPlace() != null) {
                System.out.println("Sede: " + payment.getOrder().getPlace().getPlaceName());
            }
            for (Item item : payment.getOrder().getShoppingCart().getItems()) {
                System.out.println("Producto: " + item.getProduct().getProductName()
                        + " | Cantidad: " + item.getQuantity()
                        + " | Precio: " + item.getProduct().getProductPrice()
                        + " | Subtotal: " + item.getTotal());
            }
            System.out.println("Total: " + payment.getAmount());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private PaymentMethod selectMethod() {
        if (methodService == null) {
            throw new IllegalStateException("El catálogo de métodos de pago no está conectado.");
        }
        List<PaymentMethod> methods = methodService.selectAll();
        if (methods.isEmpty()) {
            System.out.println("No hay métodos de pago registrados. Solicite su registro al administrador.");
            return null;
        }
        for (PaymentMethod method : methods) {
            System.out.println(method.getMethodId() + " | " + method.getMethodName());
        }
        int id = DataTypeValidator.validateInt("Seleccione el ID del método de pago: ");
        PaymentMethod method = methodService.selectById(id);
        if (method == null) {
            throw new IllegalArgumentException("No existe el método de pago indicado.");
        }
        return method;
    }

    private String selectType() {
        while (true) {
            int option = DataTypeValidator.validateInt("Tipo de pago: 1. Pago Presencial 2. Pago Digital");
            if (option == 1) {
                return "Pago Presencial";
            }
            if (option == 2) {
                return "Pago Digital";
            }
            System.out.println("Seleccione una opción válida.");
        }
    }

    private void printPayment(Payment payment) {
        System.out.println("Pago: " + payment.getPaymentId()
                + " | Tipo: " + payment.getPaymentType()
                + " | Método: " + payment.getPaymentMethod().getMethodName()
                + " | Orden: " + payment.getOrder().getOrderId()
                + " | Total: " + payment.getAmount());
    }
}
