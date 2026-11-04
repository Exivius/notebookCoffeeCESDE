package app.service.outputports;

import app.domain.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminRepositoryPort {
    public Admin save(Admin admin);
    public Optional<Admin> selectById(int id);
    public Admin update(Admin admin);
    public List<Admin> selectAll();
    void deleteById(int id);
    public void deleteAdmin(int id);
}
