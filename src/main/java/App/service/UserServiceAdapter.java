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
    public User createUser(Integer id, String name, String lastName, String email, String password, String state, String city) {
        User user = new User(id, name, lastName, email, password, state, city);
        return userRepositoryPort.save(user);
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