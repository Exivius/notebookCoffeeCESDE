package app.service;

import app.domain.User;
import app.service.inputports.UserServiceInterface;
import app.service.outputports.UserRepositoryPort;

import java.util.List;

public class UserServiceAdapter implements UserServiceInterface{

    private final UserRepositoryPort userRepositoryPort;

    public UserServiceAdapter(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }


    @Override
    public User createUser(Integer id, String name, String lastName, String email, String phone,
                           String password, String state, String rol, String city) {
        if (!"User".equalsIgnoreCase(rol)) {
            throw new IllegalArgumentException("El módulo de usuarios solo admite el rol User.");
        }
        User user = new User(id, name, lastName, email, phone, password, state, "User", city);
        return userRepositoryPort.save(user);
    }

    @Override
    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        return userRepositoryPort.authenticate(email.trim(), password);
    }

    @Override
    public User selectUserById(int id) {
        return userRepositoryPort.selectById(id);
    }

    @Override
    public User updateUser(User user) {
        return userRepositoryPort.updateUser(user);
    }

    @Override
    public void deleteUser(int id) {
        userRepositoryPort.deleteById(id);

    }

    @Override
    public List<User> selectAllUsers() {
        return userRepositoryPort.selectAll();
    }
}