package app.ui;

import app.domain.Admin;
import app.domain.User;
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
    private final OrderView orderView;
    private final UserView userView;

    public CliUserInterface(AdminView adminView, PaymentView paymentView, PlaceView placeView,
                            ProductView productView, OrderView orderView, UserView userView) {
        this.adminView = adminView;
        this.paymentView = paymentView;
        this.placeView = placeView;
        this.productView = productView;
        this.orderView = orderView;
        this.userView = userView;
    }

    public void applicationInit() {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nMENÚ PRINCIPAL\n1. Crear usuario\n2. Crear Admin\n3. Login\n0. Salir");
            switch (option) {
                case 1:
                    userView.createUser();
                    break;
                case 2:
                    adminView.createAdmin();
                    break;
                case 3:
                    login();
                    break;
                case 0:
                    System.out.println("Saliendo de la aplicación.");
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }

    private void login() {
        String email = DataTypeValidator.validateString("Correo: ");
        String password = DataTypeValidator.validateString("Contraseña: ");
        Admin admin = adminView.login(email, password);
        if (admin != null) {
            adminMenu();
            return;
        }
        User user = userView.login(email, password);
        if (user != null) {
            userMenu(user);
            return;
        }
        System.out.println("Correo o contraseña incorrectos.");
    }

    private void adminMenu() {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nMENÚ ADMINISTRADOR\n1. Gestionar usuarios\n2. Gestionar admins\n" +
                    "3. Gestionar sedes\n4. Gestionar productos\n5. Gestionar órdenes\n" +
                    "6. Gestionar pagos\n0. Cerrar sesión");
            switch (option) {
                case 1:
                    userMenu();
                    break;
                case 2:
                    adminManagementMenu();
                    break;
                case 3:
                    placeMenu();
                    break;
                case 4:
                    productMenu();
                    break;
                case 5:
                    orderMenu();
                    break;
                case 6:
                    paymentMenu();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }

    private void userMenu(User user) {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nMENÚ USUARIO\n1. Ver productos\n2. Crear orden\n3. Ver mis órdenes\n" +
                    "4. Realizar pago\n5. Ver recibo\n6. Modificar mis datos\n0. Cerrar sesión");
            switch (option) {
                case 1:
                    productView.selectAllProducts();
                    break;
                case 2:
                    orderView.createOrderForUser(user);
                    break;
                case 3:
                    orderView.selectOrdersByUser(user);
                    break;
                case 4:
                    paymentView.createPayment(user);
                    break;
                case 5:
                    paymentView.generateReceipt();
                    break;
                case 6:
                    userView.update(user.getId());
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }

    private void userMenu() {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nUSUARIOS\n1. Crear\n2. Consultar por ID\n3. Consultar todos\n" +
                    "4. Actualizar\n5. Eliminar\n0. Volver");
            switch (option) {
                case 1: userView.createUser(); break;
                case 2: userView.SelectUserById(DataTypeValidator.validateInt("ID: ")); break;
                case 3: userView.selectAll(); break;
                case 4: userView.updateAdmin(); break;
                case 5: userView.delete(DataTypeValidator.validateInt("ID: ")); break;
                case 0: break;
                default: System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }

    private void adminManagementMenu() {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nADMINS\n1. Crear\n2. Consultar por ID\n3. Consultar todos\n" +
                    "4. Actualizar\n5. Eliminar\n0. Volver");
            switch (option) {
                case 1: adminView.createAdmin(); break;
                case 2: adminView.selectById(DataTypeValidator.validateInt("ID: ")); break;
                case 3: adminView.selectAdmins(); break;
                case 4: adminView.update(); break;
                case 5: adminView.deleteAdmin(DataTypeValidator.validateInt("ID: ")); break;
                case 0: break;
                default: System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }

    private void placeMenu() {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nSEDES\n1. Crear\n2. Consultar por ID\n3. Consultar todas\n" +
                    "4. Actualizar\n5. Eliminar\n0. Volver");
            switch (option) {
                case 1: placeView.createPlace(); break;
                case 2: placeView.selectPlaceById(); break;
                case 3: placeView.selectAllPlaces(); break;
                case 4: placeView.updatePlace(); break;
                case 5: placeView.deletePlaceById(); break;
                case 0: break;
                default: System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }

    private void productMenu() {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nPRODUCTOS\n1. Crear\n2. Consultar por ID\n3. Consultar todos\n" +
                    "4. Actualizar\n5. Eliminar\n0. Volver");
            switch (option) {
                case 1: productView.createProduct(); break;
                case 2: productView.selectById(); break;
                case 3: productView.selectAllProducts(); break;
                case 4: productView.updateProduct(); break;
                case 5: productView.deleteById(); break;
                case 0: break;
                default: System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }

    private void orderMenu() {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nÓRDENES\n1. Crear\n2. Consultar por ID\n3. Consultar todas\n" +
                    "4. Actualizar\n5. Eliminar\n0. Volver");
            switch (option) {
                case 1: orderView.createOrder(); break;
                case 2: orderView.selectByOrderId(); break;
                case 3: orderView.selectAllOrders(); break;
                case 4: orderView.updateOrder(); break;
                case 5: orderView.deleteById(); break;
                case 0: break;
                default: System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }

    private void paymentMenu() {
        int option;
        do {
            option = DataTypeValidator.validateInt(
                    "\nPAGOS\n1. Crear pago\n2. Consultar pagos\n3. Ver recibo\n0. Volver");
            switch (option) {
                case 1: paymentView.createPayment(); break;
                case 2: paymentView.selectAllPayments(); break;
                case 3: paymentView.generateReceipt(); break;
                case 0: break;
                default: System.out.println("Seleccione una opción válida.");
            }
        } while (option != 0);
    }
}
