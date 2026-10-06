package app.ui;

import app.domain.Payment;
import app.repository.mappers.AdminRepositoryImplCollection;
import app.service.AdminServiceImpl;
import app.service.inputports.AdminService;
import app.service.outputports.AdminRepository;
import app.service.validators.DataTypeValidator;
import app.view.AdminView;
import app.view.PaymentView;
import app.view.ProductView;

public class CliUserInterface {

    private final AdminView adminView;
    private final PaymentView paymentView;
    // Vista que permite gestionar productos desde el menú.
    private final ProductView productView;

    public CliUserInterface(AdminView adminView, PaymentView paymentView, ProductView productView) {
        this.adminView = adminView;
        this.paymentView = paymentView;
        this.productView = productView;
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
        int option = DataTypeValidator.validateInt("Seleccione:\n1. Registrar admin\n" +
                "2. Consultar Admin por id\n" +
                "3. Consultar todos los admins\n" +
                "4. Gestionar productos");
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
            case 4:
                productMenu();
                break;
        }
    }

    private void productMenu() {
        int option;
        // Repite el menú hasta que el usuario seleccione cero.
        do {
            option = DataTypeValidator.validateInt(
                    "Gestión de productos:\n" +
                    "1. Registrar producto\n" +
                    "2. Consultar producto por ID\n" +
                    "3. Consultar todos los productos\n" +
                    "4. Actualizar producto\n" +
                    "5. Eliminar producto\n" +
                    "0. Volver"
            );
            // Ejecuta la operación elegida mediante la vista.
            switch (option) {
                case 1:
                    productView.createProduct();
                    break;
                case 2:
                    productView.selectById();
                    break;
                case 3:
                    productView.selectAllProducts();
                    break;
                case 4:
                    productView.updateProduct();
                    break;
                case 5:
                    productView.deleteById();
                    break;
                case 0:
                    System.out.println("Saliendo del menú de productos.");
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
                    break;
            }
        } while (option != 0);
    }
}
