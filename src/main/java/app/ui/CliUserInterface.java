package app.ui;

import app.service.validators.DataTypeValidator;
import app.view.AdminView;
import app.view.PaymentView;
import app.view.PlaceView;

public class CliUserInterface {

    private final AdminView adminView;
    private final PaymentView paymentView;
    private final PlaceView placeView;

    public CliUserInterface(AdminView adminView, PaymentView paymentView, PlaceView placeView) {
        this.adminView = adminView;
        this.paymentView = paymentView;
        this.placeView = placeView;
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
                "4. Gestionar sedes");
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
}
