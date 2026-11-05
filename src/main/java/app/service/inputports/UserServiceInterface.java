package app.service.inputports;

import app.domain.User;

import java.util.List;

public interface UserServiceInterface {
    public User createUser(Integer id , String name, String lastName , String email, String phone, String password, String state, String rol, String city);
    public User selectUserById(int id);
    public User updateUser(User user);
    public void deleteUser(int id);
    public List<User> selectAllUsers();
    User login(String email, String password);

}
