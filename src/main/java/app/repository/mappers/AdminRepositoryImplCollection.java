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

        return null;
    }

    @Override
    public Admin update(Admin admin) {

        return null;
    }

    @Override
    public void delete(int id) {

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


}
