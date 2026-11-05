package app;

import app.domain.Admin;
import app.persistence.mappers.AdminMapper;
import app.persistence.repository.AdminRepositoryPortAdapterDB;
import app.service.AdminServiceInterfaceAdapter;
import app.view.AdminView;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Regresión del módulo Admin del compañero, sin modificar su implementación ni una base real. */
public class AdminIntegrationChecks {
    public static void main(String[] args) {
        FakeDatabase db = new FakeDatabase();
        AdminRepositoryPortAdapterDB repository = new AdminRepositoryPortAdapterDB(db.connection(), new AdminMapper());
        AdminServiceInterfaceAdapter service = new AdminServiceInterfaceAdapter(repository);
        Admin created = service.create("Ana", "Prueba", "admin@example.test", "123", "clave", "Activo", "Ventas");
        check(created.getId() == 42, "Debe conservar el ID generado por MySQL");
        check("ADMIN".equals(created.getRol()), "El servicio debe crear el rol ADMIN");
        check("ADMIN".equals(db.rows.get(42).get("rol")), "El INSERT debe guardar ADMIN");
        check("Ventas".equals(service.selectAdminById(42).orElseThrow().getArea()), "El mapper debe recuperar el área");

        // Un usuario normal comparte la tabla person, pero no pertenece al CRUD de Admin.
        Map<String, Object> normal = person(99, "User", "Activo", "user@example.test");
        db.rows.put(99, normal);
        check(service.selectAdminById(99).isEmpty(), "Consulta de Admin no debe devolver usuarios");
        check(service.selectAdmins().size() == 1, "El listado debe filtrar el rol");
        Admin foreign = new Admin(99, "Alterado", "X", "x@example.test", "0", "x", "Activo", "ADMIN", "X");
        check(service.update(foreign) == null, "Admin no debe actualizar una persona User");
        service.deleteAdmin(99);
        check(db.rows.get(99) == normal, "Admin no debe eliminar personas User");

        created.setArea("Soporte");
        check(service.update(created) == created, "Debe devolver el administrador actualizado");
        check("Soporte".equals(service.selectAdminById(42).orElseThrow().getArea()), "Debe persistir el área");

        AdminView view = new AdminView(service);
        Admin authenticated = view.login("admin@example.test", "clave");
        check(authenticated != null && authenticated.getId() == 42, "Login debe conservar identidad persistida");
        check(view.login("admin@example.test", "incorrecta") == null, "Login debe rechazar contraseña incorrecta");
        check(view.login("user@example.test", "clave") == null, "Login de Admin debe rechazar usuarios");
        db.rows.get(42).put("state", "Inactivo");
        check(view.login("admin@example.test", "clave") == null, "Login debe rechazar administradores inactivos");
        service.deleteAdmin(42);
        check(service.selectAdminById(42).isEmpty(), "Debe eliminar al administrador indicado");
        System.out.println("AdminIntegrationChecks: OK");
    }

    private static Map<String, Object> person(int id, String role, String state, String email) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", id);
        row.put("name", "Persona");
        row.put("last_name", "Prueba");
        row.put("email", email);
        row.put("phone", "123");
        row.put("password", "clave");
        row.put("state", state);
        row.put("rol", role);
        row.put("area", "Ventas");
        return row;
    }

    private static class FakeDatabase {
        final Map<Integer, Map<String, Object>> rows = new LinkedHashMap<>();

        Connection connection() {
            return proxy(Connection.class, (method, args) -> {
                if (method.equals("prepareStatement")) {
                    String sql = (String) args[0];
                    if (sql.startsWith("INSERT")) {
                        check(args.length == 2 && args[1].equals(Statement.RETURN_GENERATED_KEYS),
                                "INSERT debe solicitar claves generadas");
                    }
                    return statement(sql);
                }
                return defaultValue(method);
            });
        }

        PreparedStatement statement(String sql) {
            Map<Integer, Object> params = new HashMap<>();
            return proxy(PreparedStatement.class, (method, args) -> {
                if (method.equals("setString") || method.equals("setInt")) {
                    params.put((Integer) args[0], args[1]);
                    return null;
                }
                if (method.equals("getGeneratedKeys")) return result(List.of(Map.of("1", 42)));
                if (method.equals("executeQuery")) {
                    check(sql.contains("rol = 'ADMIN'"), "Consultas deben filtrar por rol ADMIN");
                    List<Map<String, Object>> selected = new ArrayList<>();
                    for (Map<String, Object> row : rows.values()) {
                        if ("ADMIN".equals(row.get("rol"))
                                && (!sql.contains("id = ?") || row.get("id").equals(params.get(1)))) selected.add(row);
                    }
                    return result(selected);
                }
                if (method.equals("executeUpdate")) {
                    if (sql.startsWith("INSERT")) {
                        check("ADMIN".equals(params.get(7)), "Rol ADMIN debe enlazarse al parámetro 7");
                        rows.put(42, fromParams(42, params));
                        return 1;
                    }
                    check(sql.contains("WHERE id = ? AND rol = 'ADMIN'"), "Mutaciones deben proteger el rol");
                    int id = (Integer) params.get(sql.startsWith("UPDATE") ? 9 : 1);
                    Map<String, Object> existing = rows.get(id);
                    if (existing == null || !"ADMIN".equals(existing.get("rol"))) return 0;
                    if (sql.startsWith("UPDATE")) rows.put(id, fromParams(id, params));
                    else rows.remove(id);
                    return 1;
                }
                return defaultValue(method);
            });
        }

        Map<String, Object> fromParams(int id, Map<Integer, Object> params) {
            String[] fields = {"name", "last_name", "email", "phone", "password", "state", "rol", "area"};
            Map<String, Object> row = new HashMap<>();
            row.put("id", id);
            for (int i = 0; i < fields.length; i++) row.put(fields[i], params.get(i + 1));
            return row;
        }
    }

    private static ResultSet result(List<? extends Map<String, Object>> rows) {
        int[] index = {-1};
        return proxy(ResultSet.class, (method, args) -> {
            if (method.equals("next")) return ++index[0] < rows.size();
            if (method.equals("getString") || method.equals("getInt")) {
                Object value = rows.get(index[0]).get(String.valueOf(args[0]));
                return method.equals("getInt") ? ((Number) value).intValue() : value;
            }
            return defaultValue(method);
        });
    }

    private interface Call { Object apply(String method, Object[] args); }

    private static <T> T proxy(Class<T> type, Call call) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> call.apply(method.getName(), args)));
    }

    private static Object defaultValue(String method) {
        if (method.equals("isClosed") || method.equals("isWrapperFor")) return false;
        return null;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
