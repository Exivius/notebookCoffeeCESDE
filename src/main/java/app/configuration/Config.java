package app.configuration;

import app.persistence.dbConnection.DataBaseConnection;
import app.persistence.repository.*;
import app.persistence.mappers.AdminMapper;
import app.persistence.mappers.RowMapper;
import app.persistence.mappers.PlaceMapper;
import app.persistence.mappers.PaymentMethodMapper;
import app.persistence.mappers.PaymentMapper;
import app.persistence.mappers.UserMapper;
import app.service.AdminServiceInterfaceAdapter;
import app.service.PaymentServiceAdapter;
import app.service.inputports.AdminServiceInterface;
import app.service.inputports.PaymentServiceInterface;
import app.service.outputports.AdminRepositoryPort;
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
        RowMapper<app.domain.Admin> adminRowMapper = new AdminMapper();

        AdminRepositoryPort adminRepository = new AdminRepositoryPortAdapterDB(connection, adminRowMapper);
        AdminServiceInterface adminService = new AdminServiceInterfaceAdapter(adminRepository);
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
        app.service.outputports.PaymentMethodRepository methodRepository = new app.persistence.repository.PaymentMethodRepository(connection, new PaymentMethodMapper());
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
        return new CliUserInterface(adminView, paymentView, placeView, productView, orderView, userView, methodView);
    }

}
