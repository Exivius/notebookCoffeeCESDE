package app.configuration;

import app.persistence.dbConnection.DataBaseConnection;
import app.repository.*;
import app.repository.mappers.AdminMapper;
import app.repository.mappers.RowMapper;
import app.repository.mappers.PlaceMapper;
import app.repository.mappers.PaymentMethodMapper;
import app.repository.mappers.PaymentMapper;
import app.repository.mappers.UserMapper;
import app.service.AdminServiceImpl;
import app.service.PaymentServiceAdapter;
import app.service.inputports.AdminService;
import app.service.inputports.PaymentServiceInterface;
import app.service.outputports.AdminRepository;
import app.ui.CliUserInterface;
import app.view.AdminView;
import app.view.PaymentView;
import app.service.PlaceServiceAdapter;
import app.service.inputports.PlaceServiceInterface;
import app.service.outputports.PlaceRepositoryPort;
import app.view.PlaceView;
import app.service.ProductServiceAdapter;
import app.service.inputports.ProductServiceInterface;
import app.service.outputports.ProductRepositoryPort;
import app.view.ProductView;
import app.service.OrderServiceAdapter;
import app.service.inputports.OrderServiceInterface;
import app.service.outputports.OrderRepositoryPort;
import app.view.OrderView;

import app.service.UserServiceAdapter;
import app.service.inputports.UserServiceInterface;
import app.service.outputports.UserRepositoryPort;
import app.view.UserView;

import java.sql.Connection;

public class Config {
    public static CliUserInterface getCliUserInterface() {

        Connection connection = DataBaseConnection.getInstance().getConnection();
        RowMapper adminRowMapper = new AdminMapper();

        AdminRepository adminRepository = new AdminRepositoryAdapterDB(connection, adminRowMapper);
        AdminService adminService = new AdminServiceImpl(adminRepository);
        AdminView adminView = new AdminView(adminService);

        // Las sedes se consultan y guardan en la base de datos compartida.
        PlaceRepositoryPort placeRepositoryPort = new PlaceRepositoryAdapterDB(connection, new PlaceMapper());

        // Conecta el servicio con ese repositorio.
        PlaceServiceInterface placeService = new PlaceServiceAdapter(placeRepositoryPort);

        // Conecta la vista con el servicio.
        PlaceView placeView = new PlaceView(placeService);

        // Conecta los productos con su repositorio, servicio y vista.
        ProductRepositoryPort productRepositoryPort = new ProductRepositoryAdapter();
        ProductServiceInterface productService = new ProductServiceAdapter(productRepositoryPort);
        ProductView productView = new ProductView(productService);

        // Conecta el repositorio de órdenes con su servicio y su vista.
        OrderRepositoryPort orderRepositoryPort = new OrderRepositoryAdapter();
        OrderServiceInterface orderService = new OrderServiceAdapter(orderRepositoryPort);
        app.service.outputports.PaymentMethodRepository methodRepository = new app.repository.PaymentMethodRepository(connection, new PaymentMethodMapper());
        app.service.inputports.PaymentMethodService methodService = new app.service.PaymentMethodService(methodRepository);
        app.view.PaymentMethodView methodView = new app.view.PaymentMethodView(methodService);
        PaymentRepositoryAdapterDB paymentRepository = new PaymentRepositoryAdapterDB(connection, new PaymentMapper());
        PaymentServiceInterface paymentServiceInterface = new PaymentServiceAdapter(
                paymentRepository, paymentRepository::findOrderById, methodService);
        PaymentView paymentView = new PaymentView(paymentServiceInterface, methodService);
        // Comparte el servicio de usuarios con las dos vistas.
        UserRepositoryPort userRepository = new UserRepositoryAdapterDB(connection, new UserMapper());
        UserServiceInterface userService = new UserServiceAdapter(userRepository);
        UserView userView = new UserView(userService);
        OrderView orderView = new OrderView(orderService, placeService, userService, productService);

        // Entrega las vistas a la interfaz de consola.
        seedProducts(productService);
        return new CliUserInterface(adminView, paymentView, placeView, productView, orderView, userView, methodView);
    }

    private static void seedProducts(ProductServiceInterface productService) {
        productService.createProduct(null, "Café americano", 4500F, "Bebida");
        productService.createProduct(null, "Capuchino", 7000F, "Bebida");
        productService.createProduct(null, "Croissant", 5500F, "Alimento");
    }
}
