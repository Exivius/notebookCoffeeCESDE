package app.service.inputports;

import app.domain.User;

import java.util.List;

public interface UserServiceInterface {
    public User createUser(Integer id , String name, String lastName , String email , String password, String state, String city);
    public User selectUserById(int id);
    public User updateUser(User user);
    public void deleteUser(int id);
    public List<User> selectAllUsers();

}
