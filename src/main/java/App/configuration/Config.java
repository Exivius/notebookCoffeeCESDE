package app.configuration;

import app.repository.mappers.AdminRepositoryImplCollection;
import app.repository.mappers.PaymentRepositoryAdapter;
import app.repository.mappers.UserRepositoryAdapter;
import app.service.AdminServiceImpl;
import app.service.PaymentServiceAdapter;
import app.service.UserServiceAdapter;
import app.service.inputports.AdminService;
import app.service.inputports.PaymentServiceInterface;
import app.service.inputports.UserServiceInterface;
import app.service.outputports.AdminRepository;
import app.service.outputports.PaymentRepositoryPort;
import app.service.outputports.UserRepositoryPort;
import app.ui.CliUserInterface;
import app.view.AdminView;
import app.view.PaymentView;
import app.view.UserView;

public class Config {
    public static CliUserInterface getCliUserInterface() {

        AdminRepository adminRepository = new AdminRepositoryImplCollection();
        AdminService adminService = new AdminServiceImpl(adminRepository);
        AdminView adminView = new AdminView(adminService);

        PaymentRepositoryPort paymentRepositoryPort = new PaymentRepositoryAdapter();
        PaymentServiceInterface paymentServiceInterface = new PaymentServiceAdapter(paymentRepositoryPort);
        PaymentView paymentView = new PaymentView(paymentServiceInterface);

        UserRepositoryPort userRepositoryPort = new UserRepositoryAdapter();
        UserServiceInterface userServiceInterface = new UserServiceAdapter(userRepositoryPort);
        UserView userView = new UserView(userServiceInterface);

        return new CliUserInterface(adminView, paymentView, userView);
    }
}
