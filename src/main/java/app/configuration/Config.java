package app.configuration;

import app.domain.Admin;
import app.domain.Products;
import app.persistence.dbConnection.DataBaseConnection;
import app.persistence.mappers.*;
import app.persistence.repository.*;
import app.persistence.repository.PaymentMethodRepository;
import app.service.*;
import app.service.inputports.*;
import app.service.inputports.PaymentMethodService;
import app.service.outputports.*;
import app.ui.CliUserInterface;
import app.view.*;

import java.sql.Connection;

public class Config {
    public static CliUserInterface getCliUserInterface() {

        Connection connection = DataBaseConnection.getInstance().getConnection();
        RowMapper<Admin> adminRowMapper = new AdminMapper();
        RowMapper<Products> productRowMapper = new ProductMapper();

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
        ProductRepositoryPort productRepositoryPort = new ProductRepositoryAdapterDB(connection, productRowMapper);
        ProductServiceInterface productService = new ProductServiceAdapter(productRepositoryPort);
        ProductView productView = new ProductView(productService);

        ItemRepositoryPort itemRepository = new ItemRepositoryAdapterDB(connection, new ItemMapper());
        ItemServiceInterface itemService = new ItemServiceAdapter(itemRepository);
        ShoppingCartRepositoryPort cartRepository = new ShoppingCartRepositoryAdapterDB(connection);
        ShoppingCartServiceInterface cartService = new ShoppingCartServiceAdapter(cartRepository, itemService);

        // Conecta el repositorio de órdenes con su servicio y su vista.
        OrderRepositoryPort orderRepositoryPort = new OrderRepositoryAdapterDB(connection, new OrderMapper());
        OrderServiceInterface orderService = new OrderServiceAdapter(orderRepositoryPort, cartService);
        PaymentMethodRepository methodRepository = new app.persistence.repository.PaymentMethodRepository(connection, new PaymentMethodMapper());
        PaymentMethodService methodService = new app.service.PaymentMethodService(methodRepository);
        PaymentMethodView methodView = new app.view.PaymentMethodView(methodService);
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
