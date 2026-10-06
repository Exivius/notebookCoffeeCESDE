package app.view;

import app.domain.Admin;
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
        adminService.selectAdminById(id);        // Implementación para seleccionar un administrador por ID
    }

    public void update(){
        int id = DataTypeValidator.validateInt("Ingrese el ID del administrador a actualizar: ");
        Admin currentAdmin = adminService.selectAdminById(id);
        if (currentAdmin == null) {
            System.out.println("No se encontró un administrador con ese ID");
            return;
        }
        String name = DataTypeValidator.validateString("Ingrese el nuevo nombre del administrador: ");
        String lastName = DataTypeValidator.validateString("Ingrese el nuevo apellido del administrador: ");
        String email = DataTypeValidator.validateString("Ingrese el nuevo correo del administrador: ");
        String password = DataTypeValidator.validateString("Ingrese la nueva contraseña del administrador: ");
        System.out.println("Ingrese el nuevo estado del administrador: ");
        String state = SetAdminState.getAdminState();
        Admin updatedAdmin = new Admin(id, name, lastName, email, password, state);
        if (adminService.update(updatedAdmin) == null) {
            System.out.println("Error al actualizar el administrador.");
        }
    }

    public void selectAdmins(){
        adminService.selectAdmins();
    }

    public void deleteAdmin(int id){
        adminService.deleteAdmin(id);
    }

}
