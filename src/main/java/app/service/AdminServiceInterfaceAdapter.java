package app.service;

import app.domain.Admin;
import app.service.inputports.AdminServiceInterface;
import app.service.outputports.AdminRepositoryPort;

import java.util.List;
import java.util.Optional;

public class AdminServiceInterfaceAdapter implements AdminServiceInterface {

    private final AdminRepositoryPort adminRepositoryPort;

    public AdminServiceInterfaceAdapter(AdminRepositoryPort adminRepositoryPort) {
        this.adminRepositoryPort = adminRepositoryPort;
    }

    @Override
    public Admin create(Integer id, String name, String lastName, String email, String password, String state) {
        Admin admin = new Admin(id, name, lastName, email, password, state);
        return adminRepositoryPort.save(admin);
    }

    @Override
    public Admin update(Admin admin) {
        return adminRepositoryPort.update(admin);
    }

    @Override
    public List<Admin> selectAdmins() {
        return adminRepositoryPort.selectAll();
    }

    @Override
    public Optional<Admin> selectAdminById(int id) {
        return adminRepositoryPort.selectById(id);
    }

    @Override
    public void deleteAdmin(int id) {
        adminRepositoryPort.deleteById(id);
    }
}
