package app;

import app.domain.*;
import app.persistence.repository.PaymentRepositoryAdapter;
import app.persistence.repository.PaymentRepositoryAdapterDB;
import app.service.PaymentServiceAdapter;
import app.service.inputports.PaymentMethodService;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

// Pruebas ejecutables sin librerías adicionales ni conexión a MySQL.
public class PaymentChecks {
    public static void main(String[] args) {
        User owner = new User();
        owner.setId(2);
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new Item(new Products(4, "Empanada", 3000F, "Snack"), 2));
        Order order = new Order(3, new Place(1, "Centro"), owner, cart);
        PaymentMethod method = new PaymentMethod(3, "Nequi");
        PaymentMethodService methods = new PaymentMethodService() {
            public PaymentMethod create(String name) { throw new UnsupportedOperationException(); }
            public PaymentMethod selectById(int id) { return id == 3 ? method : null; }
            public List<PaymentMethod> selectAll() { return List.of(method); }
            public PaymentMethod update(int id, String name) { throw new UnsupportedOperationException(); }
            public boolean deleteById(int id) { throw new UnsupportedOperationException(); }
        };
        PaymentRepositoryAdapter memory = new PaymentRepositoryAdapter();
        PaymentServiceAdapter service = new PaymentServiceAdapter(memory, id -> id == 3 ? order : null, methods);
        Payment payment = service.createPayment(2, 3, "Pago Digital", 3);
        check(payment.getAmount() == 6000D, "El importe debe calcularse usando cantidades.");
        check(payment.getUserId() == 2, "El propietario debe proceder de la orden.");
        check(payment.getPaymentMethod().getMethodId() == 3, "Debe conservar el ID real del catálogo.");
        check("Pago Digital".equals(payment.getPaymentType()), "Debe conservar el tipo de pago.");
        check(service.generateReceipt(payment.getPaymentId(), 2) == payment, "El propietario puede consultar el recibo.");
        rejects(() -> service.createPayment(7, 3, "Pago Digital", 3));
        rejects(() -> service.generateReceipt(payment.getPaymentId(), 7));
        rejects(() -> service.createPayment(2, 99, "Pago Digital", 3));
        rejects(() -> service.createPayment(2, 3, "Pago Digital", 99));
        rejects(() -> service.createPayment(2, 3, "  ", 3));
        rejects(() -> service.createPayment(2, -1, "Pago Digital", 3));
        cart.clearCart();
        rejects(() -> service.createPayment(2, 3, "Pago Digital", 3));
        cart.addItem(new Item(new Products(4, "Empanada", 3000F, "Snack"), 2));
        payment.setPaymentType("Pago Presencial");
        check(service.updatePayment(payment) == payment, "Debe actualizar un pago existente.");
        check(service.deletePaymentdById(payment.getPaymentId()) == payment, "Eliminar debe devolver el pago eliminado.");
        check(service.selectPaymentById(payment.getPaymentId()) == null, "No debe quedar el pago eliminado.");
        check(service.deletePaymentdById(payment.getPaymentId()) == null, "Un ID inexistente no debe anunciar eliminación.");
        verifyJdbcInsert(payment);
        verifyJdbcReceipt();
        System.out.println("PaymentChecks: OK (servicio, propiedad, total, catálogo, CRUD y contrato INSERT JDBC).");
    }

    private static void verifyJdbcInsert(Payment payment) {
        Map<Integer, Object> parameters = new HashMap<>();
        Set<String> closed = new HashSet<>();
        boolean[] first = {true};
        ResultSet keys = (ResultSet) Proxy.newProxyInstance(PaymentChecks.class.getClassLoader(),
                new Class<?>[]{ResultSet.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "next" -> { boolean value = first[0]; first[0] = false; yield value; }
                    case "getInt" -> 42;
                    case "close" -> { closed.add("keys"); yield null; }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        PreparedStatement statement = (PreparedStatement) Proxy.newProxyInstance(PaymentChecks.class.getClassLoader(),
                new Class<?>[]{PreparedStatement.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "setString", "setInt" -> { parameters.put((Integer) args[0], args[1]); yield null; }
                    case "executeUpdate" -> 1;
                    case "getGeneratedKeys" -> keys;
                    case "close" -> { closed.add("statement"); yield null; }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        Connection connection = (Connection) Proxy.newProxyInstance(PaymentChecks.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (proxy, method, args) -> {
                    if (!method.getName().equals("prepareStatement")) {
                        throw new UnsupportedOperationException(method.getName());
                    }
                    check(args[0].equals("INSERT INTO payment (payment_type, order_id, method_id) VALUES (?, ?, ?)"),
                            "El INSERT debe corresponder al esquema SQL entregado.");
                    check(args[1].equals(Statement.RETURN_GENERATED_KEYS), "Debe solicitar la clave AUTO_INCREMENT.");
                    return statement;
                });
        Payment saved = new PaymentRepositoryAdapterDB(connection).save(payment);
        check(saved.getPaymentId() == 42, "Debe usar el ID generado por MySQL.");
        check(parameters.equals(Map.of(1, "Pago Presencial", 2, 3, 3, 3)), "Los parámetros del INSERT no coinciden.");
        check(closed.equals(Set.of("keys", "statement")), "Debe cerrar los recursos JDBC.");
    }

    private static void verifyJdbcReceipt() {
        Map<String, Object> paymentRow = Map.of("payment_id", 42, "payment_type", "Pago Digital",
                "order_id", 3, "method_id", 3, "method_name", "Nequi");
        Map<String, Object> orderRow = new HashMap<>();
        orderRow.put("order_id", 3);
        orderRow.put("cart_id", 8);
        orderRow.put("user_id", 2);
        orderRow.put("name", "Cliente");
        orderRow.put("place_id", 1);
        orderRow.put("place_name", "Centro");
        orderRow.put("place_type", "Punto de Venta");
        // El precio actual del producto es distinto del precio registrado en cart_item.
        Map<String, Object> itemRow = Map.of("product_id", 4, "quantity", 2,
                "price_by_quantity", 3000F, "product_price", 9999F,
                "product_name", "Empanada", "product_type", "Snack");
        Map<String, Integer> resourceCounts = new HashMap<>();
        Connection connection = (Connection) Proxy.newProxyInstance(PaymentChecks.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (proxy, method, args) -> {
                    if (!method.getName().equals("prepareStatement")) {
                        throw new UnsupportedOperationException(method.getName());
                    }
                    String sql = (String) args[0];
                    Map<String, Object> row;
                    int expectedId;
                    if (sql.contains("FROM payment p")) {
                        row = paymentRow;
                        expectedId = 42;
                    } else if (sql.contains("FROM `order` o")) {
                        row = orderRow;
                        expectedId = 3;
                    } else if (sql.contains("FROM cart_item ci")) {
                        row = itemRow;
                        expectedId = 8;
                    } else {
                        throw new AssertionError("Consulta inesperada: " + sql);
                    }
                    return Proxy.newProxyInstance(PaymentChecks.class.getClassLoader(),
                            new Class<?>[]{PreparedStatement.class}, (statement, operation, values) -> {
                                return switch (operation.getName()) {
                                    case "setInt" -> {
                                        check(values[0].equals(1) && values[1].equals(expectedId),
                                                "La consulta debe usar la clave relacionada correcta.");
                                        yield null;
                                    }
                                    case "executeQuery" -> resultRow(row, resourceCounts);
                                    case "close" -> { resourceCounts.merge("statements", 1, Integer::sum); yield null; }
                                    default -> throw new UnsupportedOperationException(operation.getName());
                                };
                            });
                });
        Payment receipt = new PaymentRepositoryAdapterDB(connection).findById(42);
        check(receipt.getAmount() == 6000D, "El recibo debe usar el precio histórico de cart_item.");
        check(receipt.getOrder().getQuantities().equals(List.of(2)), "Debe reconstruir cantidades del carrito SQL.");
        check(receipt.getUserId() == 2, "Debe obtener el propietario desde la orden SQL.");
        check(receipt.getPaymentMethod().getMethodName().equals("Nequi"), "Debe hidratar el método del catálogo.");
        check(receipt.getOrder().getPlace().getPlaceType().equals("Punto de Venta"), "Debe hidratar el tipo de sede.");
        check(receipt.getPaymentDate() == null && receipt.getPaymentStatus() == null,
                "No debe inventar fecha ni estado ausentes del esquema.");
        check(resourceCounts.equals(Map.of("statements", 3, "results", 3)),
                "Debe cerrar las tres consultas y sus resultados.");
    }

    private static ResultSet resultRow(Map<String, Object> row, Map<String, Integer> closed) {
        boolean[] first = {true};
        return (ResultSet) Proxy.newProxyInstance(PaymentChecks.class.getClassLoader(),
                new Class<?>[]{ResultSet.class}, (proxy, method, args) -> switch (method.getName()) {
                    case "next" -> { boolean value = first[0]; first[0] = false; yield value; }
                    case "getObject" -> row.get((String) args[0]);
                    case "getString" -> (String) row.get((String) args[0]);
                    case "getInt" -> ((Number) row.get((String) args[0])).intValue();
                    case "getFloat" -> ((Number) row.get((String) args[0])).floatValue();
                    case "close" -> { closed.merge("results", 1, Integer::sum); yield null; }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    private static void rejects(Runnable operation) {
        try {
            operation.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Se esperaba rechazar la operación inválida.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
