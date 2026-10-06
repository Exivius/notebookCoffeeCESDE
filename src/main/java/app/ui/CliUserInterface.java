package app.ui;

import app.service.validators.DataTypeValidator;
import app.view.AdminView;
import app.view.PaymentView;
import app.view.PlaceView;
import app.view.ProductView;
import app.view.OrderView;
import app.view.UserView;

public class CliUserInterface {

    private final AdminView adminView;
    private final PaymentView paymentView;
    private final PlaceView placeView;
    private final ProductView productView;
    // Vista que permite consultar y eliminar órdenes desde el menú.
    private final OrderView orderView;
    private final UserView userView;

    public CliUserInterface(AdminView adminView, PaymentView paymentView, PlaceView placeView, ProductView productView, OrderView orderView, UserView userView) {
        this.adminView = adminView;
        this.paymentView = paymentView;
        this.placeView = placeView;
        this.productView = productView;
        this.orderView = orderView;
        this.userView = userView;
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
                "4. Gestionar sedes\n" +
                "5. Gestionar productos\n" +
                "6. Gestionar órdenes\n" +
                "7. Gestionar usuarios");
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
                placeMenu();
                break;
            case 5:
                productMenu();
                break;
            case 6:
                orderMenu();
                break;
            case 7:
                userMenu();
                break;
        }
    }

    private void placeMenu() {
        int option;

        // Muestra el menú al menos una vez y lo repite hasta elegir cero.
        do {
            option = DataTypeValidator.validateInt(
                    "Gestión de sedes:\n" +
                    "1. Registrar sede\n" +
                    "2. Consultar sede por ID\n" +
                    "3. Consultar todas las sedes\n" +
                    "4. Actualizar sede\n" +
                    "5. Eliminar sede\n" +
                    "0. Volver"
            );

            // Llama a la operación de la vista correspondiente a la opción.
            switch (option) {
                case 1:
                    placeView.createPlace();
                    break;
                case 2:
                    placeView.selectPlaceById();
                    break;
                case 3:
                    placeView.selectAllPlaces();
                    break;
                case 4:
                    placeView.updatePlace();
                    break;
                case 5:
                    placeView.deletePlaceById();
                    break;
                case 0:
                    System.out.println("Saliendo del menú de sedes.");
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
                    break;
            }
        } while (option != 0);
    }
    private void orderMenu() {
        int option;
        // Repite el menú hasta que el usuario elija volver.
        do {
            option = DataTypeValidator.validateInt(
                    "Gestión de órdenes:\n" +
                    "1. Registrar orden\n" +
                    "2. Consultar orden por ID\n" +
                    "3. Consultar todas las órdenes\n" +
                    "4. Actualizar orden\n" +
                    "5. Eliminar orden\n" +
                    "0. Volver"
            );
            switch (option) {
                case 1:
                    orderView.createOrder();
                    break;
                case 4:
                    orderView.updateOrder();
                    break;
                case 2:
                    orderView.selectByOrderId();
                    break;
                case 3:
                    orderView.selectAllOrders();
                    break;
                case 5:
                    orderView.deleteById();
                    break;
                case 0:
                    System.out.println("Saliendo del menú de órdenes.");
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
                    break;
            }
        } while (option != 0);
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
    private void userMenu() {
        int option;
        // Utiliza las operaciones de la vista de usuarios.
        do {
            option = DataTypeValidator.validateInt(
                    "Usuarios:\n1. Registrar\n2. Consultar por ID\n3. Actualizar\n4. Eliminar\n0. Volver");
            switch (option) {
                case 1:
                    userView.createUser();
                    break;
                case 2:
                    int searchId = DataTypeValidator.validateInt("Ingrese el ID del usuario:");
                    userView.SelectUserById(searchId);
                    break;
                case 3:
                    userView.update();
                    break;
                case 4:
                    int deleteId = DataTypeValidator.validateInt("Ingrese el ID del usuario a eliminar:");
                    userView.delete(deleteId);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
                    break;
            }
        } while (option != 0);
    }
}
