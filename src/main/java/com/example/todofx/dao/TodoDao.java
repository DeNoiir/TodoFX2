package com.example.todofx.dao;

import com.example.todofx.entity.Todo;

import java.sql.*;

public class TodoDao extends BaseDao<Todo> {
    public TodoDao(Connection connection) {
        super(connection, "todo");
    }

    @Override
    protected Todo mapResultSetToEntity(ResultSet rs) throws SQLException {
        String categoryString = rs.getString("category");
        Todo.Category category = categoryString != null ? Todo.Category.valueOf(categoryString) : null;

        return new Todo(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("description"),
                category,
                Todo.Status.valueOf(rs.getString("status")),
                rs.getDate("due_date") != null ? rs.getDate("due_date").toLocalDate() : null,
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                rs.getString("user_id")
        );
    }

    @Override
    protected String generateUpsertSQL() {
        return "INSERT INTO todo (id, title, description, category, status, due_date, created_at, updated_at, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "title = VALUES(title), description = VALUES(description), category = VALUES(category), " +
                "status = VALUES(status), due_date = VALUES(due_date), updated_at = VALUES(updated_at), " +
                "user_id = VALUES(user_id)";
    }

    @Override
    protected void setParametersForUpsert(PreparedStatement stmt, Todo todo) throws SQLException {
        stmt.setString(1, todo.getId());
        stmt.setString(2, todo.titleProperty().get());
        stmt.setString(3, todo.descriptionProperty().get());
        if (todo.categoryProperty().get() != null) {
            stmt.setString(4, todo.categoryProperty().get().name());
        } else {
            stmt.setNull(4, Types.VARCHAR);
        }
        stmt.setString(5, todo.statusProperty().get().name());
        stmt.setDate(6, todo.dueDateProperty().get() != null ? Date.valueOf(todo.dueDateProperty().get()) : null);
        stmt.setTimestamp(7, Timestamp.valueOf(todo.createdAtProperty().get()));
        stmt.setTimestamp(8, Timestamp.valueOf(todo.updatedAtProperty().get()));
        stmt.setString(9, todo.userIdProperty().get());
    }
}