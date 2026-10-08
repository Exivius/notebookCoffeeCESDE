package app.service.inputports;

import app.domain.Admin;
import app.domain.User;

import java.util.List;

public interface AdminService {
    public Admin create(Integer id , String name, String lastName , String email , String password, String state);
    public Admin update(Admin admin);
    public List<Admin> selectAdmins();
    public Admin selectAdminById(int id);
    public void deleteAdmin(int id);
}
