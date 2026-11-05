package app.persistence.mappers;

import app.domain.Admin;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminMapper implements RowMapper<Admin> {
    @Override
    public Admin mapRow(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt("id"));
        admin.setName(rs.getString("name"));
        admin.setLastName(rs.getString("last_name"));
        admin.setEmail(rs.getString("email"));
        admin.setPhone(rs.getString("phone"));
        admin.setPassword(rs.getString("password"));
        admin.setState(rs.getString("state"));
        admin.setRol(rs.getString("rol"));
        admin.setArea(rs.getString("area"));

        return admin;
    }
}
