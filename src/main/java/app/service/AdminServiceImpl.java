package app.service;

import app.domain.Admin;
import app.service.inputports.AdminService;
import app.service.outputports.AdminRepository;

import java.util.List;

public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin create(Integer id, String name, String lastName, String email, String password, String state) {
        Admin admin = new Admin(id, name, lastName, email, password, state);
        return adminRepository.save(admin);
    }

    @Override
    public Admin update(Admin admin) {
        return adminRepository.update(admin);
    }

    @Override
    public List<Admin> selectAdmins() {
        return adminRepository.selectAll();
    }

    @Override
    public Admin selectAdminById(int id) {
        return adminRepository.selectById(id);
    }

    @Override
    public void deleteAdmin(int id) {
        adminRepository.deleteById(id);
    }
}
