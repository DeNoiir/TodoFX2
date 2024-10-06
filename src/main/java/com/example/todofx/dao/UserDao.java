package com.example.todofx.dao;

import com.example.todofx.entity.User;

import java.sql.*;

public class UserDao extends BaseDao<User> {
    public UserDao(Connection connection) {
        super(connection, "user");
    }

    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toLocalDateTime() : null
        );
    }

    @Override
    protected String generateUpsertSQL() {
        return "INSERT INTO user (id, username, password_hash, created_at, updated_at, last_login) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "username = VALUES(username), password_hash = VALUES(password_hash), " +
                "updated_at = VALUES(updated_at), last_login = VALUES(last_login)";
    }

    @Override
    protected void setParametersForUpsert(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getId());
        stmt.setString(2, user.usernameProperty().get());
        stmt.setString(3, user.passwordHashProperty().get());
        stmt.setTimestamp(4, Timestamp.valueOf(user.createdAtProperty().get()));
        stmt.setTimestamp(5, Timestamp.valueOf(user.updatedAtProperty().get()));
        stmt.setTimestamp(6, user.lastLoginProperty().get() != null ? Timestamp.valueOf(user.lastLoginProperty().get()) : null);
    }
}