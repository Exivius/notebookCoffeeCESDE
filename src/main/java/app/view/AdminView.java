package app.view;

import app.domain.Admin;
import app.service.helpers.SetAdminState;
import app.service.inputports.AdminServiceInterface;
import app.service.validators.DataTypeValidator;

public class AdminView {
    private final AdminServiceInterface adminServiceInterface;

    public AdminView(AdminServiceInterface adminServiceInterface) {
        this.adminServiceInterface = adminServiceInterface;
    }

    public void createAdmin(){
        int id = DataTypeValidator.validateInt("Ingrese el ID del administrador: ");
        String name = DataTypeValidator.validateString("Ingrese el nombre del administrador: ");
        String lastName = DataTypeValidator.validateString("Ingrese el apellido del administrador: ");
        String email = DataTypeValidator.validateString("Ingrese el correo del administrador: ");
        String password = DataTypeValidator.validateString("Ingrese la contraseña del administrador: ");
        System.out.println("Ingrese el estado del administrador: ");
        String state = SetAdminState.getAdminState();

        Admin admin = adminServiceInterface.create(id, name, lastName, email, password, state);
        if (admin == null) {
            System.out.println("Ya existe un administrador con ese ID.");
        }
    }

    public void selectById(int id) {
        Admin admin = adminServiceInterface.selectAdminById(id).orElse(null);
        if (admin == null) {
            System.out.println("No se encontró un administrador con ese ID.");
            return;
        }
        System.out.println();
        System.out.println("=============== ADMINISTRADOR ENCONTRADO ===============");
        System.out.printf("%-5s | %-15s | %-15s | %-25s | %-15s | %-10s | %-12s | %-15s%n",
                "ID",
                "NOMBRE",
                "APELLIDO",
                "CORREO",
                "CONTRASEÑA",
                "ROL",
                "ESTADO",
                "ÁREA");

        System.out.println("--------------------------------------------------------------------------");

        System.out.printf("%-5d | %-15s | %-15s | %-25s | %-15s | %-10s | %-12s | %-15s%n",
                admin.getId(),
                admin.getName(),
                admin.getLastName(),
                admin.getEmail(),
                admin.getPassword(),
                admin.getRol(),
                admin.getState(),
                admin.getArea());

        System.out.println("==========================================================");
    }

    public void update(){
        int id = DataTypeValidator.validateInt("Ingrese el ID del administrador a actualizar: ");
        Admin currentAdmin = adminServiceInterface.selectAdminById(id).orElse(null);;
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
        if (adminServiceInterface.update(updatedAdmin) == null) {
            System.out.println("Error al actualizar el administrador.");
        }
    }

    public void selectAdmins() {
        var admins = adminServiceInterface.selectAdmins();

        if (admins.isEmpty()) {
            System.out.println("No hay administradores registrados.");
            return;
        }

        System.out.println();
        System.out.println("=============== LISTADO DE ADMINISTRADORES ===============");
        System.out.printf("%-5s | %-15s | %-15s | %-25s | %-15s | %-10s | %-12s | %-15s%n",
                "ID",
                "NOMBRE",
                "APELLIDO",
                "CORREO",
                "CONTRASEÑA",
                "ROL",
                "ESTADO",
                "ÁREA");

        System.out.println("--------------------------------------------------------------------------");

        for (Admin admin : admins) {
            System.out.printf("%-5d | %-15s | %-15s | %-25s | %-15s | %-10s | %-12s | %-15s%n",
                    admin.getId(),
                    admin.getName(),
                    admin.getLastName(),
                    admin.getEmail(),
                    admin.getPassword(),
                    admin.getRol(),
                    admin.getState(),
                    admin.getArea());
        }

        System.out.println("==========================================================");
    }

    public Admin login(String email, String password) {
        for (Admin admin : adminServiceInterface.selectAdmins()) {
            if (email.equals(admin.getEmail()) && password.equals(admin.getPassword())
                    && "Activo".equals(admin.getState())) {
                return admin;
            }
        }
        return null;
    }

    public void deleteAdmin(int id){
        adminServiceInterface.deleteAdmin(id);
    }

}
