package app.service.outputports;

import app.domain.Admin;
import app.domain.Person;

import java.util.List;

public interface AdminRepository {
    public Admin save(Admin admin);
    public Admin selectById(int id);
    public Admin update(Admin admin);
    public void delete(int id);
    public List<Admin> selectAll();
}
