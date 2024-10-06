package com.example.todofx.dao;

import com.example.todofx.entity.Entity;

import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class BaseDao<T extends Entity<String>> implements Dao<T> {
    protected final Connection connection;
    protected final Executor executor;
    protected final String tableName;
    protected final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public BaseDao(Connection connection, String tableName) {
        this.connection = connection;
        this.executor = Executors.newSingleThreadExecutor();
        this.tableName = tableName;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected <U> CompletableFuture<U> executeAsync(DbOperation<U> operation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return operation.execute(connection);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Optional<T>> findById(String id) {
        return executeAsync(conn -> {
            String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<T>> findAll() {
        return executeAsync(conn -> {
            String sql = "SELECT * FROM " + tableName;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                List<T> entities = new ArrayList<>();
                while (rs.next()) {
                    entities.add(mapResultSetToEntity(rs));
                }
                return entities;
            }
        });
    }

    @Override
    public CompletableFuture<T> save(T entity) {
        return executeAsync(conn -> {
            String sql = generateUpsertSQL();
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setParametersForUpsert(stmt, entity);
                stmt.executeUpdate();
                if (entity.getId() == null) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            entity.setId(generatedKeys.getString(1));
                        }
                    }
                }
                return entity;
            }
        });
    }

    @Override
    public CompletableFuture<Void> delete(T entity) {
        return deleteById(entity.getId());
    }

    @Override
    public CompletableFuture<Void> deleteById(String id) {
        return executeAsync(conn -> {
            String sql = "DELETE FROM " + tableName + " WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
                return null;
            }
        });
    }

    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    protected abstract String generateUpsertSQL();
    protected abstract void setParametersForUpsert(PreparedStatement stmt, T entity) throws SQLException;

    @FunctionalInterface
    protected interface DbOperation<U> {
        U execute(Connection connection) throws Exception;
    }
}