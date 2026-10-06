package app.ui;

import app.domain.Payment;
import app.repository.mappers.AdminRepositoryImplCollection;
import app.service.AdminServiceImpl;
import app.service.inputports.AdminService;
import app.service.outputports.AdminRepository;
import app.service.validators.DataTypeValidator;
import app.view.AdminView;
import app.view.PaymentView;

public class CliUserInterface {

    private final AdminView adminView;
    private final PaymentView paymentView;

    public CliUserInterface(AdminView adminView, PaymentView paymentView) {
        this.adminView = adminView;
        this.paymentView = paymentView;
    }

    public void applicationInit(){
        int init = DataTypeValidator.validateInt("Presione 1 para iniciar la aplicación");
        while(init != 0){
            int option = DataTypeValidator.validateInt("1. Registro " +
                    "2. Login" +
                    "3. Salir");
            switch (option){
                case 1:
                    adminView.createAdmin();
                    break;
                case 2:
                    System.out.println("Login");
                    adminMenu();
                    break;
                case 3:
                    System.out.println("Saliendo de la aplicación");
                    init = 0;
                    break;
                default:
                    System.out.println("Seleccione una opción valida");
                    break;
            }
        }
    }

    public void adminMenu(){
        int option = DataTypeValidator.validateInt("Seleccione 1. registrar admin " +
                "2. Consultar Admin por id" +
                "3. Consultar todos los admins");
        switch (option){
            case 1:
                System.out.println("Registrar Usuario");
                adminView.createAdmin();
                break;
            case 2:
                System.out.println("Consultar Usuario por id");
                int id = DataTypeValidator.validateInt("Ingrese el id del admin a consultar");
                adminView.selectById(id);
                break;
            case 3:
                System.out.println("Consultar todos los admins");
                adminView.selectAdmins();
                break;
        }
    }
}
