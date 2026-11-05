package app.api;

import app.domain.Admin;
import app.domain.Item;
import app.domain.Order;
import app.domain.Payment;
import app.domain.PaymentMethod;
import app.domain.Place;
import app.domain.Products;
import app.domain.User;
import app.service.inputports.AdminServiceInterface;
import app.service.inputports.PaymentMethodService;
import app.service.inputports.PaymentServiceInterface;
import app.service.inputports.PlaceServiceInterface;
import app.service.inputports.ProductServiceInterface;
import app.service.inputports.UserServiceInterface;
import app.service.inputports.OrderServiceInterface;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Local HTTP boundary for the frontend admin panel. */
public final class AdminHttpServer {
    private final AdminServiceInterface admins;
    private final UserServiceInterface users;
    private final PlaceServiceInterface places;
    private final ProductServiceInterface products;
    private final OrderServiceInterface orders;
    private final PaymentServiceInterface payments;
    private final PaymentMethodService methods;
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Set<String> allowedOrigins;
    private HttpServer server;

    public AdminHttpServer(AdminServiceInterface admins, UserServiceInterface users,
                           PlaceServiceInterface places, ProductServiceInterface products,
                           OrderServiceInterface orders, PaymentServiceInterface payments,
                           PaymentMethodService methods, String allowedOrigin) {
        this.admins = admins;
        this.users = users;
        this.places = places;
        this.products = products;
        this.orders = orders;
        this.payments = payments;
        this.methods = methods;
        this.allowedOrigins = java.util.Arrays.stream(allowedOrigin.split(","))
                .map(String::trim).filter(value -> !value.isEmpty()).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api", this::handle);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        server.start();
        System.out.println("API administrativa disponible en http://127.0.0.1:" + port + "/api");
    }

    public void stop() { if (server != null) server.stop(1); }

    private void handle(HttpExchange exchange) throws IOException {
        cors(exchange);
        if ("OPTIONS".equals(exchange.getRequestMethod())) { send(exchange, 204, null); return; }
        try {
            String path = exchange.getRequestURI().getPath().substring("/api".length());
            String method = exchange.getRequestMethod();
            Map<String, Object> body = "GET".equals(method) || "DELETE".equals(method)
                    ? Map.of() : Json.object(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            if ("/session".equals(path) && "POST".equals(method)) { createSession(exchange, body); return; }
            if ("/health".equals(path) && "GET".equals(method)) { send(exchange, 200, Map.of("status", "ok")); return; }
            Admin admin = authenticate(exchange);
            if (admin == null) { send(exchange, 401, Map.of("message", "La sesión no es válida.")); return; }
            if ("/session".equals(path) && "DELETE".equals(method)) {
                sessions.remove(bearer(exchange)); send(exchange, 204, null); return;
            }
            String[] segments = path.substring(1).split("/");
            if (segments.length < 1 || segments.length > 2) { send(exchange, 404, Map.of("message", "Ruta no encontrada.")); return; }
            Object result = resource(segments[0], segments.length == 2 ? segments[1] : null, method, body, admin);
            send(exchange, result == null ? 204 : ("POST".equals(method) ? 201 : 200), result);
        } catch (IllegalArgumentException error) {
            send(exchange, 400, Map.of("message", safeMessage(error)));
        } catch (IllegalStateException error) {
            send(exchange, 409, Map.of("message", safeMessage(error)));
        } catch (Exception error) {
            error.printStackTrace(System.err);
            send(exchange, 500, Map.of("message", "No se pudo completar la operación en el backend."));
        } finally {
            exchange.close();
        }
    }

    private void createSession(HttpExchange exchange, Map<String, Object> body) throws IOException {
        String email = text(body, "email", true);
        String password = text(body, "password", true);
        Admin admin = admins.selectAdmins().stream()
                .filter(value -> value.getEmail() != null && value.getEmail().equalsIgnoreCase(email))
                .filter(value -> value.getPassword() != null && value.getPassword().equals(password))
                .filter(value -> "ACTIVO".equalsIgnoreCase(value.getState()))
                .findFirst().orElse(null);
        if (admin == null) { send(exchange, 401, Map.of("message", "Correo o contraseña incorrectos, o cuenta inactiva.")); return; }
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(admin, Instant.now().plusSeconds(8 * 60 * 60)));
        send(exchange, 200, Map.of("token", token, "admin", Map.of(
                "id", admin.getId(), "name", admin.getName(), "email", admin.getEmail())));
    }

    private Object resource(String resource, String rawId, String method, Map<String, Object> body, Admin current) {
        Integer id = rawId == null ? null : integer(rawId, "ID");
        if ("users".equals(resource)) return people(false, id, method, body, current);
        if ("admins".equals(resource)) return people(true, id, method, body, current);
        if ("places".equals(resource)) return place(id, method, body);
        if ("products".equals(resource)) return product(id, method, body);
        if ("payment-methods".equals(resource)) return paymentMethod(id, method, body);
        if ("orders".equals(resource) && "GET".equals(method) && id == null) return orderRows();
        if ("payments".equals(resource) && "GET".equals(method) && id == null) return paymentRows();
        throw new IllegalArgumentException("La ruta solicitada no existe o es de solo lectura.");
    }

    private Object people(boolean administrator, Integer id, String method, Map<String, Object> body, Admin current) {
        if (id == null && "GET".equals(method)) {
            if (administrator) return admins.selectAdmins().stream().map(this::adminRow).toList();
            return users.selectAllUsers().stream().map(this::userRow).toList();
        }
        if (id == null && "POST".equals(method)) {
            String name = text(body, "name", true);
            String lastName = text(body, "lastName", false);
            String email = text(body, "email", true);
            String phone = text(body, "phone", false);
            String password = text(body, "password", true);
            String state = state(body);
            if (administrator) {
                Admin saved = admins.create(name, lastName, email, phone, password, state, text(body, "area", false));
                return adminRow(saved);
            }
            User saved = users.createUser(null, name, lastName, email, phone, password, state, "User", text(body, "city", false));
            return userRow(saved);
        }
        if (id == null) throw new IllegalArgumentException("Falta el ID de la persona.");
        if ("GET".equals(method)) return administrator
                ? admins.selectAdminById(id).map(this::adminRow).orElse(null)
                : userRow(users.selectUserById(id));
        if ("DELETE".equals(method)) {
            if (administrator) {
                if (current.getId().equals(id)) throw new IllegalArgumentException("No puedes desactivar tu propia cuenta.");
                Admin saved = admins.selectAdminById(id).orElse(null);
                if (saved == null) return null;
                saved.setState("INACTIVO");
                return adminRow(admins.update(saved));
            }
            User saved = users.selectUserById(id);
            if (saved == null) return null;
            saved.setState("Inactivo");
            return userRow(users.updateUser(saved));
        }
        if ("PUT".equals(method)) {
            String state = state(body);
            if (administrator) {
                Admin saved = admins.selectAdminById(id).orElse(null);
                if (saved == null) return null;
                saved.setName(text(body, "name", true)); saved.setLastName(text(body, "lastName", false));
                saved.setEmail(text(body, "email", true)); saved.setPhone(text(body, "phone", false));
                saved.setState(state.toUpperCase()); saved.setArea(text(body, "area", false));
                return adminRow(admins.update(saved));
            }
            User saved = users.selectUserById(id);
            if (saved == null) return null;
            saved.setName(text(body, "name", true)); saved.setLastName(text(body, "lastName", false));
            saved.setEmail(text(body, "email", true)); saved.setPhone(text(body, "phone", false));
            saved.setState(state); saved.setCity(text(body, "city", false));
            return userRow(users.updateUser(saved));
        }
        throw new IllegalArgumentException("Operación no permitida para personas.");
    }

    private Object place(Integer id, String method, Map<String, Object> body) {
        if (id == null && "GET".equals(method)) return places.selectAllPlaces().stream().map(this::placeRow).toList();
        if (id == null && "POST".equals(method)) return placeRow(places.createPlace(null, text(body, "placeName", true), text(body, "placeType", true)));
        if (id == null) throw new IllegalArgumentException("Falta el ID de la sede.");
        if ("DELETE".equals(method)) { places.deletePlaceById(id); return null; }
        if ("PUT".equals(method)) return placeRow(places.updatePlace(id, text(body, "placeName", true), text(body, "placeType", true)));
        throw new IllegalArgumentException("Operación no permitida para sedes.");
    }

    private Object product(Integer id, String method, Map<String, Object> body) {
        if (id == null && "GET".equals(method)) return products.selectAllProducts().stream().map(this::productRow).toList();
        if (id == null && "POST".equals(method)) return productRow(products.createProduct(null,
                text(body, "productName", true), (float) decimal(body, "productPrice"), text(body, "productType", true),
                text(body, "description", false), nonNegativeInteger(body, "stock")));
        if (id == null) throw new IllegalArgumentException("Falta el ID del producto.");
        if ("DELETE".equals(method)) { products.deleteById(id); return null; }
        if ("PUT".equals(method)) return productRow(products.updateProduct(id, text(body, "productName", true),
                (float) decimal(body, "productPrice"), text(body, "productType", true),
                text(body, "description", false), nonNegativeInteger(body, "stock")));
        throw new IllegalArgumentException("Operación no permitida para productos.");
    }

    private Object paymentMethod(Integer id, String method, Map<String, Object> body) {
        if (id == null && "GET".equals(method)) return methods.selectAll().stream().map(this::methodRow).toList();
        if (id == null && "POST".equals(method)) return methodRow(methods.create(text(body, "methodName", true)));
        if (id == null) throw new IllegalArgumentException("Falta el ID del método de pago.");
        if ("DELETE".equals(method)) return methods.deleteById(id) ? null : null;
        if ("PUT".equals(method)) return methodRow(methods.update(id, text(body, "methodName", true)));
        throw new IllegalArgumentException("Operación no permitida para métodos de pago.");
    }

    private List<Map<String, Object>> orderRows() {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Order order : orders.selectAllOrders()) {
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("orderId", order.getOrderId()); row.put("userId", order.getUser() == null ? null : order.getUser().getId());
            row.put("placeId", order.getPlace() == null ? null : order.getPlace().getPlaceId());
            row.put("items", order.getShoppingCart().getItems().stream().map(item -> Map.of(
                    "productId", item.getProductId(), "quantity", item.getQuantity(), "product", productRow(item.getProduct()))).toList());
            row.put("total", order.getShoppingCart().getTotal()); rows.add(row);
        }
        return rows;
    }
    private List<Map<String, Object>> paymentRows() {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Payment value : payments.selectAllPayments()) {
            Map<String, Object> row = new java.util.LinkedHashMap<>();
            row.put("paymentId", value.getPaymentId()); row.put("paymentType", value.getPaymentType());
            row.put("orderId", value.getOrder() == null ? null : value.getOrder().getOrderId());
            row.put("methodId", value.getPaymentMethod() == null ? null : value.getPaymentMethod().getMethodId());
            row.put("amount", value.getAmount()); rows.add(row);
        }
        return rows;
    }

    private Map<String, Object> adminRow(Admin value) {
        if (value == null) return null;
        return Map.of("id", value.getId(), "name", nil(value.getName()), "lastName", nil(value.getLastName()),
                "email", nil(value.getEmail()), "phone", nil(value.getPhone()), "state", nil(value.getState()), "area", nil(value.getArea()));
    }
    private Map<String, Object> userRow(User value) {
        if (value == null) return null;
        return Map.of("id", value.getId(), "name", nil(value.getName()), "lastName", nil(value.getLastName()),
                "email", nil(value.getEmail()), "phone", nil(value.getPhone()), "state", nil(value.getState()), "city", nil(value.getCity()));
    }
    private Map<String, Object> placeRow(Place value) { return Map.of("placeId", value.getPlaceId(), "placeName", value.getPlaceName(), "placeType", value.getPlaceType()); }
    private Map<String, Object> productRow(Products value) {
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("productId", value.getProductId()); row.put("productName", value.getProductName());
        row.put("productPrice", value.getProductPrice()); row.put("productType", value.getProductType());
        row.put("description", nil(value.getDescription())); row.put("stock", value.getStock());
        return row;
    }
    private Map<String, Object> methodRow(PaymentMethod value) { return Map.of("methodId", value.getMethodId(), "methodName", value.getMethodName()); }
    private Object nil(String value) { return value == null ? "" : value; }

    private Admin authenticate(HttpExchange exchange) {
        String token = bearer(exchange);
        Session session = token == null ? null : sessions.get(token);
        if (session == null) return null;
        if (Instant.now().isAfter(session.expiresAt())) { sessions.remove(token); return null; }
        return session.admin();
    }
    private String bearer(HttpExchange exchange) {
        String value = exchange.getRequestHeaders().getFirst("Authorization");
        return value != null && value.startsWith("Bearer ") ? value.substring(7) : null;
    }
    private void cors(HttpExchange exchange) {
        String origin = exchange.getRequestHeaders().getFirst("Origin");
        if (origin != null && (allowedOrigins.contains("*") || allowedOrigins.contains(origin))) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
            exchange.getResponseHeaders().set("Vary", "Origin");
        }
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Authorization, Content-Type");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
    }
    private void send(HttpExchange exchange, int status, Object value) throws IOException {
        byte[] data = value == null ? new byte[0] : Json.stringify(value).getBytes(StandardCharsets.UTF_8);
        if (data.length > 0) exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, status == 204 ? -1 : data.length);
        if (data.length > 0) exchange.getResponseBody().write(data);
    }
    private String safeMessage(RuntimeException error) { return error.getMessage() == null ? "Solicitud no válida." : error.getMessage(); }
    private String text(Map<String, Object> body, String key, boolean required) {
        Object value = body.get(key);
        String text = value == null ? "" : String.valueOf(value).trim();
        if (required && text.isEmpty()) throw new IllegalArgumentException("El campo " + key + " es obligatorio.");
        int max = switch (key) {
            case "phone" -> 20;
            case "email" -> 150;
            case "name", "lastName", "city", "area", "placeName", "placeType", "methodName" -> 100;
            case "productName" -> 150;
            case "productType" -> 100;
            case "state" -> 10;
            case "description" -> 500;
            default -> 255;
        };
        if (text.length() > max) throw new IllegalArgumentException("El campo " + key + " admite hasta " + max + " caracteres.");
        return text;
    }
    private String state(Map<String, Object> body) {
        String value = text(body, "state", true);
        if (!List.of("Activo", "Inactivo", "ACTIVO", "INACTIVO").contains(value)) throw new IllegalArgumentException("Estado no válido.");
        return value;
    }
    private int integer(String value, String label) {
        try { int id = Integer.parseInt(value); if (id < 1) throw new NumberFormatException(); return id; }
        catch (NumberFormatException error) { throw new IllegalArgumentException(label + " debe ser un entero positivo."); }
    }
    private double decimal(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (!(value instanceof Number number) || !Double.isFinite(number.doubleValue()) || number.doubleValue() <= 0) {
            throw new IllegalArgumentException("El campo " + key + " debe ser un número mayor que cero.");
        }
        return number.doubleValue();
    }
    private int nonNegativeInteger(Map<String, Object> body, String key) {
        Object value = body.get(key);
        if (!(value instanceof Number number) || number.doubleValue() < 0
                || number.doubleValue() != Math.rint(number.doubleValue()) || number.doubleValue() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("El campo " + key + " debe ser un entero mayor o igual que cero.");
        }
        return number.intValue();
    }

    private record Session(Admin admin, Instant expiresAt) {}
}
