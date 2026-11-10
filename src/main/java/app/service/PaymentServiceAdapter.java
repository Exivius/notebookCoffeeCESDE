package app.service;

import app.domain.Payment;
import app.domain.PaymentMethod;
import app.domain.Order;
import app.domain.Item;
import app.service.inputports.PaymentServiceInterface;
import app.service.inputports.PaymentMethodService;
import app.service.outputports.PaymentRepositoryPort;
import app.service.inputports.OrderServiceInterface;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class PaymentServiceAdapter implements PaymentServiceInterface {
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final Function<Integer, Order> orderLookup;
    private final PaymentMethodService methodService;

    public PaymentServiceAdapter(PaymentRepositoryPort repository, OrderServiceInterface orders) {
        this(repository, orders::selectByOrderId, null);
    }

    public PaymentServiceAdapter(PaymentRepositoryPort repository,
                                 Function<Integer, Order> orderLookup,
                                 PaymentMethodService methodService) {
        this.paymentRepositoryPort = repository;
        this.orderLookup = orderLookup;
        this.methodService = methodService;
    }

    @Override
    public Payment createPayment(Integer userId, Integer orderId, String paymentType, Integer methodId) {
        Order order = findOrder(orderId, userId);
        rejectPaidOrder(orderId);
        PaymentMethod method = findMethod(methodId);
        Payment payment = new Payment(null, method, order);
        payment.setPaymentType(validateType(paymentType));
        prepareReceipt(payment, order);
        return paymentRepositoryPort.save(payment);
    }

    // Conserva el contrato anterior para las integraciones en memoria.
    @Override
    public Payment createPayment(Integer id, Integer userId, Integer orderId, Double amount,
                                 String paymentMethod, String paymentStatus, String paymentDate) {
        Order order = findOrder(orderId, userId);
        rejectPaidOrder(orderId);
        PaymentMethod method;
        if (methodService == null) {
            if (paymentMethod == null || paymentMethod.isBlank()) {
                throw new IllegalArgumentException("Debe seleccionar un método de pago.");
            }
            method = new PaymentMethod(null, paymentMethod.trim());
        } else {
            method = methodService.selectAll().stream()
                    .filter(value -> value.getMethodName().equalsIgnoreCase(paymentMethod))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("No existe el método de pago."));
        }
        Payment payment = new Payment(id, method, order);
        payment.setPaymentType("Pago Presencial");
        prepareReceipt(payment, order);
        // Fecha y estado no se almacenan en el esquema SQL recibido.
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
        requireId(id, "pago");
        return paymentRepositoryPort.findById(id);
    }

    @Override
    public Payment deletePaymentdById(int id) {
        Payment payment = selectPaymentById(id);
        if (payment != null) {
            paymentRepositoryPort.deleteById(id);
        }
        return payment;
    }

    @Override
    public Payment updatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Debe indicar el pago.");
        }
        requireId(payment.getPaymentId(), "pago");
        if (selectPaymentById(payment.getPaymentId()) == null) {
            return null;
        }
        if (payment.getOrder() == null || payment.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Debe indicar la orden y el método de pago.");
        }
        Payment current = selectPaymentById(payment.getPaymentId());
        Integer currentOrderId = current.getOrder() == null ? null : current.getOrder().getOrderId();
        Order order = findOrder(payment.getOrder().getOrderId(), null);
        if (!Objects.equals(currentOrderId, order.getOrderId())
                && paymentRepositoryPort.existsByOrderId(order.getOrderId())) {
            throw new IllegalArgumentException("La orden ya tiene un pago registrado.");
        }
        if (methodService != null) {
            payment.setPaymentMethod(findMethod(payment.getPaymentMethod().getMethodId()));
        }
        payment.setPaymentType(validateType(payment.getPaymentType()));
        prepareReceipt(payment, order);
        return paymentRepositoryPort.update(payment);
    }

    @Override
    public Payment generateReceipt(int id) {
        return generateReceipt(id, null);
    }

    @Override
    public Payment generateReceipt(int id, Integer userId) {
        Payment payment = selectPaymentById(id);
        if (payment != null) {
            Order order = findOrder(payment.getOrder().getOrderId(), userId);
            prepareReceipt(payment, order);
        }
        return payment;
    }

    private Order findOrder(Integer orderId, Integer userId) {
        requireId(orderId, "orden");
        Order order = orderLookup.apply(orderId);
        if (order == null) {
            throw new IllegalArgumentException("No existe la orden indicada en el almacenamiento de órdenes.");
        }
        if (order.getUser() == null || order.getUser().getId() == null) {
            throw new IllegalArgumentException("La orden no tiene un usuario válido.");
        }
        if (userId != null) {
            requireId(userId, "usuario");
            if (!Objects.equals(order.getUser().getId(), userId)) {
                throw new IllegalArgumentException("La orden no pertenece al usuario indicado.");
            }
        }
        return order;
    }

    private PaymentMethod findMethod(Integer methodId) {
        requireId(methodId, "método");
        if (methodService == null) {
            throw new IllegalStateException("El catálogo de métodos de pago no está conectado.");
        }
        PaymentMethod method = methodService.selectById(methodId);
        if (method == null) {
            throw new IllegalArgumentException("No existe el método de pago indicado.");
        }
        return method;
    }

    private void prepareReceipt(Payment payment, Order order) {
        double total = 0;
        for (Item item : order.getShoppingCart().getItems()) {
            double price = item.getProduct().getProductPrice();
            if (!Double.isFinite(price) || price < 0) {
                throw new IllegalArgumentException("La orden contiene un precio inválido.");
            }
            total += price * item.getQuantity();
        }
        if (!Double.isFinite(total) || total <= 0) {
            throw new IllegalArgumentException("La orden debe contener productos con un total mayor que cero.");
        }
        payment.setOrder(order);
        payment.setUserId(order.getUser().getId());
        payment.setAmount(total);
    }

    //Verifica que la orden no tenga un pago registrado antes de crear o actualizar un pago.
    private void rejectPaidOrder(Integer orderId) {
        if (paymentRepositoryPort.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("La orden ya tiene un pago registrado.");
        }
    }

    private String validateType(String type) {
        if (type == null || type.isBlank() || type.trim().length() > 100) {
            throw new IllegalArgumentException("El tipo de pago es obligatorio y admite hasta 100 caracteres.");
        }
        return type.trim();
    }

    private void requireId(Integer id, String entity) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("El ID de " + entity + " debe ser mayor que cero.");
        }
    }
}
