package app.ui;

import app.service.validators.DataTypeValidator;
import app.view.AdminView;
import app.view.PaymentView;
import app.view.UserView;

public class CliUserInterface {

    private final AdminView adminView;
    private final PaymentView paymentView;
    private final UserView userView;

    public CliUserInterface(AdminView adminView, PaymentView paymentView, UserView userView) {
        this.adminView = adminView;
        this.paymentView = paymentView;
        this.userView = userView;
    }

    public void applicationInit(){
        int init = DataTypeValidator.validateInt("Presione 1 para iniciar la aplicaci├│n");
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
                    System.out.println("Saliendo de la aplicaci├│n");
                    init = 0;
                    break;
                default:
                    System.out.println("Seleccione una opci├│n valida");
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
                System.out.println("Registrar Admin");
                adminView.createAdmin();
                break;
            case 2:
                System.out.println("Consultar Admin por id");
                int id = DataTypeValidator.validateInt("Ingrese el id del admin a consultar");
                adminView.selectById(id);
                break;
            case 3:
                System.out.println("Consultar todos los admins");
                adminView.selectAdmins();
                break;
            case 4:
                System.out.println();
        }
    }
}
