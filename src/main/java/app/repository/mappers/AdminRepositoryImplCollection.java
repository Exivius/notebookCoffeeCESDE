package app.repository.mappers;

import app.domain.Admin;
import app.service.outputports.AdminRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminRepositoryImplCollection implements AdminRepository {

    List<Admin> admins = new ArrayList<>();

    @Override
    public Admin save(Admin admin) {
        admins.add(admin);
        return admin;
    }

    @Override
    public Admin selectById(int id) {
        for (Admin admin : admins) {
            if (admin.getId() != null && admin.getId() == id) {
                return admin;
            }
        }
        return null;
    }

    @Override
    public Admin update(Admin admin) {
        for (int index = 0; index < admins.size(); index++) {
            Admin currentAdmin = admins.get(index);
            if (currentAdmin.getId() != null && currentAdmin.getId().equals(admin.getId())) {
                admins.set(index, admin);
                return admin;
            }
        }
        return null;
    }

    @Override
    public List<Admin> selectAll() {
        for(Admin admin : admins){
            System.out.println(admin.getId() + "" +
                    " " + admin.getName() + "" + admin.getLastName() + " " + admin.getEmail() + " "
                    + admin.getPassword() + "" + admin.getState());
        }

        return admins;
    }

    @Override
    public void deleteById(int id) {
        admins.removeIf(admin -> admin.getId() != null && admin.getId() == id);
    }

    @Override
    public void deleteAdmin(int id) {
        deleteById(id);
    }


}
