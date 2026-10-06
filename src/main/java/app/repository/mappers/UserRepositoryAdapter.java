package app.repository.mappers;

import app.domain.User;
import app.service.outputports.UserRepositoryPort;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryAdapter implements UserRepositoryPort {

    List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        users.add(user);
        return user;
    }

    @Override
    public User selectById(int id) {
        for(User user : users) {
            if(user.getId() != null && user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> selectAll() {
        for(User user : users) {
            System.out.println(user.getId() + " " +
                    user.getName() + " " + user.getLastName() + " " + user.getEmail() + " " +
                    user.getPhone() + " " + user.getPassword() + " " +
                    " " + user.getCity());
        }
        return users;
    }

    @Override
    public User updateUser(User user) {
        for(int index = 0; index < users.size(); index++) {
            User currentUser = users.get(index);
            if(currentUser.getId() != null && currentUser.getId().equals(user.getId())) {
                users.set(index, user);
                return user;
            }
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        users.removeIf(user -> user.getId() != null && user.getId() == id);
    }

    @Override
    public User create(User user) {
        return save(user);
    }

    @Override
    public int countUsers() {
        return users.size();
    }
}
