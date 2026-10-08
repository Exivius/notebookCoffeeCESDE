package app.view;

import app.domain.User;
import app.service.inputports.UserServiceInterface;
import app.service.helpers.SetAdminState;
import app.service.validators.DataTypeValidator;

public class UserView {
    private final UserServiceInterface userServiceInterface;

    public UserView(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    public void createUser() {
        int id = DataTypeValidator.validateInt("Ingrese el id del usuario: ");
        String name = DataTypeValidator.validateString("Ingrese el nombre del usuario: ");
        String lastName = DataTypeValidator.validateString("Ingrese el apellido del usuario: ");
        String email = DataTypeValidator.validateString("Ingrese el correo del usuario: ");
        String password = DataTypeValidator.validateString("Ingrese la contraseña del usuario: ");
        System.out.println("Seleccione el estado del usuario:");
        String city = DataTypeValidator.validateString("Ingrese la ciudad del usuario: ");
        User user = userServiceInterface.createUser(id, name, lastName, email, password, "Activo", city);
        System.out.println(user == null ? "Ya existe un usuario con ese ID." : "Usuario registrado correctamente.");
    }

    public void SelectUserById(int id) {
        User user = userServiceInterface.selectUserById(id);
        if (user == null) {
            System.out.println("No se encontró un usuario con ese id");
            return;
        }
        print(user);
    }

    public void update() {
        int id = DataTypeValidator.validateInt("Ingrese el id del usuario a actualizar: ");
        update(id);
    }

    public void updateAdmin() {
        int id = DataTypeValidator.validateInt("Ingrese el id del usuario a actualizar: ");
        User currentUser = userServiceInterface.selectUserById(id);
        if (currentUser == null) {
            System.out.println("No se encontró un usuario con ese id");
            return;
        }
        String name = DataTypeValidator.validateString("Ingrese el nombre: ");
        String lastName = DataTypeValidator.validateString("Ingrese el apellido: ");
        String email = DataTypeValidator.validateString("Ingrese el correo: ");
        String password = DataTypeValidator.validateString("Ingrese la contraseña: ");
        String city = DataTypeValidator.validateString("Ingrese la ciudad: ");
        String state = SetAdminState.getAdminState();
        userServiceInterface.updateUser(new User(id, name, lastName, email, password,
                state, city, true));
        System.out.println("Usuario actualizado correctamente.");
    }

    public void update(int id) {
        User currentUser = userServiceInterface.selectUserById(id);
        if (currentUser == null) {
            System.out.println("No se encontró un usuario con ese id");
            return;
        }
        String name = DataTypeValidator.validateString("Ingrese el nombre: ");
        String lastName = DataTypeValidator.validateString("Ingrese el apellido: ");
        String email = DataTypeValidator.validateString("Ingrese el correo: ");
        String password = DataTypeValidator.validateString("Ingrese la contraseña: ");
        String city = DataTypeValidator.validateString("Ingrese la ciudad: ");
        User updatedUser = new User(id, name, lastName, email, password,
                currentUser.getState(), city);
        if (userServiceInterface.updateUser(updatedUser) == null) {
            System.out.println("No se pudo actualizar el usuario");
        } else {
            System.out.println("Datos actualizados correctamente.");
        }
    }

    public void selectAll() {
        for (User user : userServiceInterface.selectAllUsers()) {
            print(user);
        }
    }

    public void delete(int id) {
        userServiceInterface.deleteUser(id);
    }

    public User login(String email, String password) {
        for (User user : userServiceInterface.selectAllUsers()) {
            if (email.equals(user.getEmail()) && password.equals(user.getPassword())
                    && "Activo".equals(user.getState())) {
                return user;
            }
        }
        return null;
    }

    private void print(User user) {
        System.out.println(user.getId() + " | " + user.getName() + " " + user.getLastName()
                + " | " + user.getEmail() + " | " + user.getCity()
                + " | Rol: " + user.getRol() + " | Estado: " + user.getState());
    }
}
