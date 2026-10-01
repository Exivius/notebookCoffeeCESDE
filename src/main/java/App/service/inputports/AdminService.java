package app.service.inputports;

import app.domain.Admin;
import app.domain.Person;

import java.util.List;

public interface AdminService {
    public Person create(Integer id , String name, String lastName , String email , String password, String state);
    public void selectById(int id);
    public void update();
    public void delete();
    public List<Admin> selectAdmins();
}
