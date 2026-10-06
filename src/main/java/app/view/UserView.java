package app.view;

import app.domain.User;
import app.service.inputports.UserServiceInterface;
import app.service.validators.DataTypeValidator;

public class UserView {
    private final UserServiceInterface userServiceInterface;

    public UserView(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    public void createUser(){
        int id = DataTypeValidator.validateInt("Ingrese el id del usuario: ");
        String name = DataTypeValidator.validateString("Ingrese el nombre del usuario: ");
        String lastName = DataTypeValidator.validateString("Ingrese el apellido del usuario: ");
        String email = DataTypeValidator.validateString("Ingrese el correo del usuario: ");
        String password = DataTypeValidator.validateString("Ingrese la contraseña del usuario: ");
        String state = DataTypeValidator.validateString("Ingrese el estado del usuario: ");
        String city = DataTypeValidator.validateString("Ingrese la ciudad del usuario: ");
        userServiceInterface.createUser(id, name, lastName, email, password, state, city);
    }
    public void SelectUserById(int id){
        userServiceInterface.selectUserById(id);
    }

    public void update(){
        int id = DataTypeValidator.validateInt("Ingrese el id del usuario a actualizar: ");
        User currentUser = userServiceInterface.selectUserById(id);
        if (currentUser == null) {
            System.out.println("No se encontró un usuario con ese id");
            return;
        }
        String name = DataTypeValidator.validateString("Ingrese el nombre del usuario: ");
        String lastName = DataTypeValidator.validateString("Ingrese el apellido del usuario: ");
        String email = DataTypeValidator.validateString("Ingrese el correo del usuario: ");
        String password = DataTypeValidator.validateString("Ingrese la contraseña del usuario: ");
        String state = DataTypeValidator.validateString("Ingrese el estado del usuario: ");
        String city = DataTypeValidator.validateString("Ingrese la ciudad del usuario: ");
        User updatedUser = new User(id, name, lastName, email, password, state, city);
        if (userServiceInterface.updateUser(updatedUser) == null) {
            System.out.println("No se pudo actualizar el usuario");
        }
    }

    public void delete(int id){
        userServiceInterface.deleteUser(id);
    }
}
