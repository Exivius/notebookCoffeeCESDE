package app.configuration;

import app.repository.mappers.AdminRepositoryImplCollection;
import app.repository.mappers.PaymentRepositoryAdapter;
import app.service.AdminServiceImpl;
import app.service.PaymentServiceAdapter;
import app.service.inputports.AdminService;
import app.service.inputports.PaymentServiceInterface;
import app.service.outputports.AdminRepository;
import app.service.outputports.PaymentRepositoryPort;
import app.ui.CliUserInterface;
import app.view.AdminView;
import app.view.PaymentView;
import app.repository.mappers.PlaceRepositoryAdapter;
import app.service.PlaceServiceAdapter;
import app.service.inputports.PlaceServiceInterface;
import app.service.outputports.PlaceRepositoryPort;
import app.view.PlaceView;
import app.repository.mappers.ProductRepositoryAdapter;
import app.service.ProductServiceAdapter;
import app.service.inputports.ProductServiceInterface;
import app.service.outputports.ProductRepositoryPort;
import app.view.ProductView;
import app.repository.mappers.OrderRepositoryAdapter;
import app.service.OrderServiceAdapter;
import app.service.inputports.OrderServiceInterface;
import app.service.outputports.OrderRepositoryPort;
import app.view.OrderView;

import app.repository.mappers.UserRepositoryAdapter;
import app.service.UserServiceAdapter;
import app.service.inputports.UserServiceInterface;
import app.service.outputports.UserRepositoryPort;
import app.view.UserView;
public class Config {
    public static CliUserInterface getCliUserInterface() {

        AdminRepository adminRepository = new AdminRepositoryImplCollection();
        AdminService adminService = new AdminServiceImpl(adminRepository);
        AdminView adminView = new AdminView(adminService);

        PaymentRepositoryPort paymentRepositoryPort = new PaymentRepositoryAdapter();
        PaymentServiceInterface paymentServiceInterface = new PaymentServiceAdapter(paymentRepositoryPort);
        PaymentView paymentView = new PaymentView(paymentServiceInterface);

        // Crea el repositorio que almacenará las sedes en memoria.
        PlaceRepositoryPort placeRepositoryPort = new PlaceRepositoryAdapter();

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
        // Comparte el servicio de usuarios con las dos vistas.
        UserRepositoryPort userRepository = new UserRepositoryAdapter();
        UserServiceInterface userService = new UserServiceAdapter(userRepository);
        UserView userView = new UserView(userService);
        OrderView orderView = new OrderView(orderService, placeService, userService, productService);

        // Entrega las vistas a la interfaz de consola.
        return new CliUserInterface(adminView, paymentView, placeView, productView, orderView, userView);
    }
}
