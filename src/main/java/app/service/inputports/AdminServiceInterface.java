package app.service.inputports;

import app.domain.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminServiceInterface {
    public Admin create(Integer id , String name, String lastName , String email , String password, String state);
    public Admin update(Admin admin);
    public List<Admin> selectAdmins();
    public Optional<Admin> selectAdminById(int id);
    public void deleteAdmin(int id);
}
