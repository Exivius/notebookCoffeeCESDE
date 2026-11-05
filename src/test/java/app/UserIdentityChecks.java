package app;

import app.domain.*;
import app.repository.PaymentRepositoryAdapter;
import app.service.PaymentServiceAdapter;
import app.service.inputports.PaymentMethodService;
import app.repository.UserRepositoryAdapterDB;
import app.repository.mappers.UserMapper;
import app.service.UserServiceAdapter;
import app.view.UserView;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Comprueba identidad persistida y autenticación sin conexión a MySQL. */
public class UserIdentityChecks {
    public static void main(String[] args) {
        DatabaseFake database = new DatabaseFake();
        UserRepositoryAdapterDB repository = new UserRepositoryAdapterDB(database.connection(), new UserMapper());
        UserView view = new UserView(new UserServiceAdapter(repository));
        User loggedIn = view.login("cliente42@example.test", "clave-correcta");
        check(loggedIn != null && loggedIn.getId() == 42,
                "La sesión debe conservar el ID42 de person, no un ID de usuarios de demostración.");
        verifyPaymentIdentity(loggedIn);
        check("User".equals(loggedIn.getRol()), "El usuario autenticado debe conservar el rol persistido.");
        check(view.login("cliente42@example.test", "incorrecta") == null,
                "Debe rechazar una contraseña incorrecta.");
        check(view.login("desconocido@example.test", "clave-correcta") == null,
                "Debe rechazar un correo inexistente.");
        database.row.put("state", "Inactivo");
        check(view.login("cliente42@example.test", "clave-correcta") == null,
                "Una cuenta inactiva no debe iniciar sesión.");
        database.row.put("state", "Activo");
        database.row.put("rol", "Admin");
        check(view.login("cliente42@example.test", "clave-correcta") == null,
                "El acceso de usuarios no debe autenticar filas Admin.");
        database.row.put("rol", "User");
        check(view.login("cliente42@example.test' OR '1'='1", "clave-correcta") == null,
                "El correo debe tratarse como valor, no como parte del SQL.");
        check(database.authQueries == 6, "El inicio de sesión debe consultar directamente, sin listar todas las personas.");
        User created = repository.save(new User(2, "Nuevo", "Cliente", "nuevo@example.test",
                "clave-nueva", "Activo", "Medellín"));
        check(created.getId() == 42,
                "Crear usuario debe usar la clave AUTO_INCREMENT, ignorando el ID2 recibido.");
        check("User".equals(database.inserted.get("rol")), "La creación debe persistir el rol User.");
        check(!database.inserted.containsKey("id"), "El INSERT debe dejar que MySQL genere el ID.");
        check(database.statementsClosed == database.statementsOpened,
                "Cada sentencia JDBC debe cerrarse.");
        check(database.resultsClosed == database.resultsOpened,
                "Cada resultado JDBC debe cerrarse.");
        System.out.println("UserIdentityChecks: OK (ID persistido, autenticación, rol, estado y claves generadas).");
    }

    private static void verifyPaymentIdentity(User persistedUser) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new Item(new Products(7, "Café", 2500F, "Bebida"), 2));
        Order persistedOrder = new Order(8, new Place(1, "Centro"), persistedUser, cart);
        PaymentMethod method = new PaymentMethod(3, "Nequi");
        PaymentMethodService catalog = new PaymentMethodService() {
            public PaymentMethod create(String name) { throw new UnsupportedOperationException(); }
            public PaymentMethod selectById(int id) { return id == 3 ? method : null; }
            public List<PaymentMethod> selectAll() { return List.of(method); }
            public PaymentMethod update(int id, String name) { throw new UnsupportedOperationException(); }
            public boolean deleteById(int id) { throw new UnsupportedOperationException(); }
        };
        PaymentServiceAdapter payments = new PaymentServiceAdapter(new PaymentRepositoryAdapter(),
                id -> id == 8 ? persistedOrder : null, catalog);
        Payment payment = payments.createPayment(persistedUser.getId(), 8, "Pago Digital", 3);
        check(payment.getUserId() == 42 && payment.getAmount() == 5000D,
                "El usuario SQL autenticado debe poder pagar su orden con el ID persistido.");
        try {
            payments.createPayment(2, 8, "Pago Digital", 3);
            throw new AssertionError("El ID2 de un usuario de demostración no debe pagar una orden del ID42.");
        } catch (IllegalArgumentException expected) {
            // El ID del usuario SQL es la autoridad para comprobar propiedad.
        }
    }

    private static class DatabaseFake {
        final Map<String, Object> row = new HashMap<>(Map.of("id", 42, "name", "Cliente",
                "last_name", "Persistido", "email", "cliente42@example.test", "password", "clave-correcta",
                "state", "Activo", "rol", "User", "city", "Medellín", "phone", ""));
        final Map<String, Object> inserted = new HashMap<>();
        int authQueries, statementsOpened, statementsClosed, resultsOpened, resultsClosed;

        Connection connection() {
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Connection.class},
                    (proxy, method, args) -> {
                        if (!method.getName().equals("prepareStatement")) {
                            throw new UnsupportedOperationException(method.getName());
                        }
                        statementsOpened++;
                        String sql = (String) args[0];
                        if (sql.stripLeading().toUpperCase(Locale.ROOT).startsWith("INSERT")) {
                            check(args.length > 1 && args[1].equals(Statement.RETURN_GENERATED_KEYS),
                                    "El INSERT debe solicitar la clave generada.");
                        }
                        return statement(sql);
                    });
        }

        private PreparedStatement statement(String sql) {
            Map<Integer, Object> params = new HashMap<>();
            return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class<?>[]{PreparedStatement.class}, (proxy, method, args) -> switch (method.getName()) {
                        case "setString", "setInt", "setObject" -> { params.put((Integer) args[0], args[1]); yield null; }
                        case "setNull" -> { params.put((Integer) args[0], null); yield null; }
                        case "executeQuery" -> {
                            authQueries++;
                            String normalized = sql.toLowerCase(Locale.ROOT);
                            check(normalized.contains("where") && normalized.contains("email")
                                            && normalized.contains("password") && normalized.contains("?"),
                                    "La autenticación debe usar una consulta parametrizada por correo y contraseña.");
                            check(!sql.contains("cliente42@example.test") && !sql.contains("clave-correcta"),
                                    "Las credenciales no deben concatenarse en SQL.");
                            yield result(matches(sql, params) ? row : null);
                        }
                        case "executeUpdate" -> {
                            Matcher insert = Pattern.compile("(?is)INSERT\\s+INTO\\s+person\\s*\\((.*?)\\)\\s*VALUES\\s*\\((.*?)\\)").matcher(sql);
                            check(insert.find(), "Se esperaba INSERT de usuario en person.");
                            String[] columns = insert.group(1).split(",");
                            String[] values = insert.group(2).split(",");
                            int parameter = 1;
                            for (int i = 0; i < columns.length; i++) {
                                String value = values[i].trim();
                                inserted.put(columns[i].trim().replace("`", ""),
                                        value.equals("?") ? params.get(parameter++) : value.replace("'", ""));
                            }
                            yield 1;
                        }
                        case "getGeneratedKeys" -> result(Map.of("id", 42));
                        case "close" -> { statementsClosed++; yield null; }
                        default -> throw new UnsupportedOperationException(method.getName());
                    });
        }

        private boolean matches(String sql, Map<Integer, Object> params) {
            String where = sql.substring(sql.toLowerCase(Locale.ROOT).indexOf("where") + 5);
            Matcher predicate = Pattern.compile("(?i)(?:\\w+\\.)?`?(email|password|state|rol)`?\\s*=\\s*(\\?|'[^']*')").matcher(where);
            int parameter = 1;
            while (predicate.find()) {
                String value = predicate.group(2);
                Object expected = value.equals("?") ? params.get(parameter++) : value.substring(1, value.length() - 1);
                if (!Objects.equals(row.get(predicate.group(1).toLowerCase(Locale.ROOT)), expected)) return false;
            }
            return true;
        }

        private ResultSet result(Map<String, Object> values) {
            resultsOpened++;
            boolean[] first = {true};
            return (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ResultSet.class},
                    (proxy, method, args) -> switch (method.getName()) {
                        case "next" -> { boolean exists = first[0] && values != null; first[0] = false; yield exists; }
                        case "getInt" -> ((Number) values.get(args[0] instanceof Integer ? "id" : args[0])).intValue();
                        case "getString" -> values.get(args[0]);
                        case "getObject" -> values.get(args[0]);
                        case "close" -> { resultsClosed++; yield null; }
                        default -> throw new UnsupportedOperationException(method.getName());
                    });
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
