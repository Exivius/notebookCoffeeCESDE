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
import app.repository.mappers.ProductRepositoryAdapter;
import app.service.ProductServiceAdapter;
import app.service.inputports.ProductServiceInterface;
import app.service.outputports.ProductRepositoryPort;
import app.view.ProductView;

public class Config {
    public static CliUserInterface getCliUserInterface() {

        AdminRepository adminRepository = new AdminRepositoryImplCollection();
        AdminService adminService = new AdminServiceImpl(adminRepository);
        AdminView adminView = new AdminView(adminService);

        PaymentRepositoryPort paymentRepositoryPort = new PaymentRepositoryAdapter();
        PaymentServiceInterface paymentServiceInterface = new PaymentServiceAdapter(paymentRepositoryPort);
        PaymentView paymentView = new PaymentView(paymentServiceInterface);

        // Conecta el repositorio de productos con el servicio y la vista.
        ProductRepositoryPort productRepositoryPort = new ProductRepositoryAdapter();
        ProductServiceInterface productService = new ProductServiceAdapter(productRepositoryPort);
        ProductView productView = new ProductView(productService);

        return new CliUserInterface(adminView, paymentView, productView);
    }
}
