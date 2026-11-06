package app;

import app.api.AdminHttpServer;
import app.persistence.dbConnection.DataBaseConnection;
import app.persistence.mappers.AdminMapper;
import app.persistence.mappers.PaymentMapper;
import app.persistence.mappers.PaymentMethodMapper;
import app.persistence.mappers.PlaceMapper;
import app.persistence.mappers.RowMapper;
import app.persistence.mappers.UserMapper;
import app.persistence.repository.AdminRepositoryPortAdapterDB;
import app.persistence.repository.OrderRepositoryAdapterDB;
import app.persistence.repository.PaymentMethodRepository;
import app.persistence.repository.PaymentRepositoryAdapterDB;
import app.persistence.repository.PlaceRepositoryAdapterDB;
import app.persistence.repository.ProductRepositoryAdapterDB;
import app.persistence.repository.UserRepositoryAdapterDB;
import app.service.AdminServiceInterfaceAdapter;
import app.service.OrderServiceAdapter;
import app.service.PaymentServiceAdapter;
import app.service.PlaceServiceAdapter;
import app.service.ProductServiceAdapter;
import app.service.UserServiceAdapter;
import app.service.inputports.AdminServiceInterface;
import app.service.inputports.OrderServiceInterface;
import app.service.inputports.PaymentMethodService;
import app.service.inputports.PaymentServiceInterface;
import app.service.inputports.PlaceServiceInterface;
import app.service.inputports.ProductServiceInterface;
import app.service.inputports.UserServiceInterface;

import java.sql.Connection;

/** Starts the Java REST boundary used by the frontend admin panel. */
public final class ApiApplication {
    private ApiApplication() {}

    public static void main(String[] args) throws Exception {
        Connection connection = DataBaseConnection.getInstance().getConnection();
        RowMapper<app.domain.Admin> adminMapper = new AdminMapper();
        AdminServiceInterface admins = new AdminServiceInterfaceAdapter(
                new AdminRepositoryPortAdapterDB(connection, adminMapper));
        UserServiceInterface users = new UserServiceAdapter(
                new UserRepositoryAdapterDB(connection, new UserMapper()));
        PlaceServiceInterface places = new PlaceServiceAdapter(
                new PlaceRepositoryAdapterDB(connection, new PlaceMapper()));
        PaymentMethodService methods = new app.service.PaymentMethodService(
                new PaymentMethodRepository(connection, new PaymentMethodMapper()));
        PaymentRepositoryAdapterDB paymentRepository = new PaymentRepositoryAdapterDB(connection, new PaymentMapper());
        ProductServiceInterface products = new ProductServiceAdapter(new ProductRepositoryAdapterDB(connection));
        OrderServiceInterface orders = new OrderServiceAdapter(new OrderRepositoryAdapterDB(connection, paymentRepository));
        PaymentServiceInterface payments = new PaymentServiceAdapter(
                paymentRepository, orders::selectByOrderId, methods);

        int port = Integer.parseInt(System.getenv().getOrDefault("NOTEBOOK_API_PORT", "8080"));
        String origin = System.getenv().getOrDefault("NOTEBOOK_API_ORIGIN", "http://localhost:8780,http://localhost:63342");
        AdminHttpServer server = new AdminHttpServer(admins, users, places, products, orders,
                payments, methods, origin);
        server.start(port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}
