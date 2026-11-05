package app.repository;

import app.domain.User;
import app.service.outputports.UserRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepositoryAdapter implements UserRepositoryPort {

    List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            int id = 1;
            while (selectById(id) != null) id++;
            user.setId(id);
        }
        if (selectById(user.getId()) != null) {
            return null;
        }
        users.add(user);
        return user;
    }

    @Override
    public User selectById(int id) {
        for(User user : users) {
            if(Objects.equals(user.getId(), id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> selectAll() {
        return new ArrayList<>(users);
    }

    @Override
    public User updateUser(User user) {
        for(int index = 0; index < users.size(); index++) {
            User currentUser = users.get(index);
            if(Objects.equals(currentUser.getId(), user.getId())) {
                users.set(index, user);
                return user;
            }
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        users.removeIf(user -> Objects.equals(user.getId(), id));
    }

    @Override
    public User create(User user) {
        return save(user);
    }

    @Override
    public int countUsers() {
        return users.size();
    }
    @Override
    public User authenticate(String email, String password) {
        for (User user : users) {
            if (Objects.equals(email, user.getEmail()) && Objects.equals(password, user.getPassword())
                    && "Activo".equals(user.getState())) {
                return user;
            }
        }
        return null;
    }

}
