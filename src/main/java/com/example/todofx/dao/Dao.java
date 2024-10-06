package com.example.todofx.dao;

import com.example.todofx.entity.Entity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Dao<T extends Entity<String>> {
    CompletableFuture<Optional<T>> findById(String id);
    CompletableFuture<List<T>> findAll();
    CompletableFuture<T> save(T entity);
    CompletableFuture<Void> delete(T entity);
    CompletableFuture<Void> deleteById(String id);
}
