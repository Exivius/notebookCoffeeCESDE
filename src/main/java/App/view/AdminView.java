package app.view;

import app.service.AdminServiceImpl;
import app.service.helpers.SetAdminState;
import app.service.inputports.AdminService;
import app.service.validators.DataTypeValidator;

public class AdminView {
    private final AdminService adminService;

    public AdminView(AdminService adminService) {
        this.adminService = adminService;
    }

    public void createAdmin(){
        int id = DataTypeValidator.validateInt("Ingrese el ID del administrador: ");
        String name = DataTypeValidator.validateString("Ingrese el nombre del administrador: ");
        String lastName = DataTypeValidator.validateString("Ingrese el apellido del administrador: ");
        String email = DataTypeValidator.validateString("Ingrese el correo del administrador: ");
        String password = DataTypeValidator.validateString("Ingrese la contraseña del administrador: ");
        System.out.println("Ingrese el estado del administrador: ");
        String state = SetAdminState.getAdminState();

        adminService.create(id, name, lastName, email, password, state);
    }

    public void selectById(int id) {
        // Implementación para seleccionar un administrador por ID
    }

    public void selectAdmins(){
        adminService.selectAdmins();
    }
}
