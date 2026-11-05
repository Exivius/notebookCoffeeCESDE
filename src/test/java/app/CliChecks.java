package app;

import app.domain.Admin;
import app.domain.Order;
import app.domain.Payment;
import app.domain.Products;
import app.repository.PaymentRepositoryAdapter;
import app.service.PaymentServiceAdapter;
import app.domain.PaymentMethod;
import app.domain.User;
import app.repository.PlaceRepositoryAdapter;
import app.service.PlaceServiceAdapter;
import app.service.inputports.PaymentServiceInterface;
import app.service.inputports.PaymentMethodService;
import app.ui.CliUserInterface;
import app.view.AdminView;
import app.view.PaymentMethodView;
import app.view.PaymentView;
import app.view.PlaceView;
import app.view.UserView;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CliChecks {
    public static void main(String[] args) {
        // Dos sesiones: administrador y cliente con el ID persistido, con órdenes propias y ajenas.
        String input = String.join("\n", "3", "admin", "clave", "7", "1", "Nequi", "3", "0",
                "3", "1", "Bello", "Punto de Entrega", "0", "6", "4", "999", "0", "0",
                "3", "cliente", "clave", "4", "11", "7", "1", "5", "2",
                "4", "12", "7", "1", "5", "1", "0", "0") + "\n";
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream previous = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        Catalog catalog = new Catalog();
        User persistedUser = new User();
        persistedUser.setId(42);
        User anotherUser = new User();
        anotherUser.setId(2);
        Order ownOrder = new Order(11, null, persistedUser, new Products(1, "Café", 2500F, "Bebida"));
        Order foreignOrder = new Order(12, null, anotherUser, new Products(2, "Té", 3000F, "Bebida"));
        PaymentRepositoryAdapter repository = new PaymentRepositoryAdapter();
        Payment otherPayment = new Payment(null, new PaymentMethod(7, "Nequi"), foreignOrder);
        otherPayment.setPaymentType("Pago Digital");
        repository.save(otherPayment);
        PaymentServiceInterface payments = new PaymentServiceAdapter(repository,
                id -> id == 11 ? ownOrder : id == 12 ? foreignOrder : null, catalog);
        AdminView admins = new AdminView(null) {
            @Override public Admin login(String email, String password) {
                return email.equals("admin") ? new Admin() : null;
            }
        };
        UserView users = new UserView(null) {
            @Override public User login(String email, String password) {
                return persistedUser;
            }
        };
        PlaceServiceAdapter places = new PlaceServiceAdapter(new PlaceRepositoryAdapter());
        try {
            new CliUserInterface(admins, new PaymentView(payments, catalog), new PlaceView(places),
                    null, null, users, new PaymentMethodView(catalog)).applicationInit();
        } finally {
            System.setOut(previous);
        }
        String text = output.toString(StandardCharsets.UTF_8);
        check(catalog.created != null && catalog.created.getMethodName().equals("Nequi"),
                "El menú debe conectar el registro con el catálogo");
        check(places.selectAllPlaces().size() == 1, "El menú debe crear la sede");
        check(places.selectAllPlaces().getFirst().getPlaceType().equals("Punto de Entrega"),
                "El menú debe propagar el tipo de sede");
        check(repository.findById(2) != null && repository.findById(2).getUserId() == 42,
                "El pago debe usar el ID de la sesión autenticada");
        check(repository.findById(3) == null, "No debe registrar un pago de una orden ajena");
        check(text.contains("Realizar pago") && text.contains("5. Ver recibo"),
                "El cliente autenticado debe poder pagar y consultar recibos");
        check(text.contains("Recibo de Pago") && text.contains("ID del Usuario: 42"),
                "Debe permitir consultar el recibo propio");
        check(text.split("La orden no pertenece al usuario indicado.", -1).length - 1 == 2,
                "Debe rechazar pagar una orden ajena y consultar su recibo");
        check(text.contains("No existe un pago con ese ID."), "Debe mostrar la consulta sin resultado");
        System.out.println("CliChecks: OK (catálogo, sedes, pagos propios y rechazo de órdenes/recibos ajenos).");
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static class Catalog implements PaymentMethodService {
        PaymentMethod created;
        public PaymentMethod create(String name) { return created = new PaymentMethod(7, name); }
        public PaymentMethod selectById(int id) { return id == 7 ? created : null; }
        public List<PaymentMethod> selectAll() { return created == null ? List.of() : List.of(created); }
        public PaymentMethod update(int id, String name) { throw new AssertionError("No se esperaba actualizar"); }
        public boolean deleteById(int id) { throw new AssertionError("No se esperaba eliminar"); }
    }
}
