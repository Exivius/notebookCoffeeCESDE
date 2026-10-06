package app.service.outputports;

import app.domain.User;

import java.util.List;

public interface UserRepositoryPort {
    public User save(User user);
    public User selectById(int id);
    public List<User> selectAll();
    public User updateUser(User user);
    public void deleteById(int id);
    User create(User user);
    public int countUsers();
}
