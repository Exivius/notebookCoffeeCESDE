package app.service;

import app.domain.Admin;
import app.domain.Person;
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
    public void selectById(int id) {

    }

    @Override
    public void update() {

    }

    @Override
    public void delete() {

    }

    @Override
    public List<Admin> selectAdmins() {
        return adminRepository.selectAll();
    }
}
