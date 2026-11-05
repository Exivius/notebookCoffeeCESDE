package app.persistence.repository;

import app.domain.User;
import app.persistence.mappers.RowMapper;
import app.service.outputports.UserRepositoryPort;

import java.sql.Connection;
import java.util.List;

public class UserRepositoryAdapterDB implements UserRepositoryPort {
    private final Connection connection;
    private final RowMapper rowMapper;

    public UserRepositoryAdapterDB(Connection connection, RowMapper rowMapper) {
        this.connection = connection;
        this.rowMapper = rowMapper;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User selectById(int id) {
        return null;
    }

    @Override
    public List<User> selectAll() {
        return List.of();
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public int countUsers() {
        return 0;
    }
}
