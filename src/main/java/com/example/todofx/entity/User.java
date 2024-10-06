package com.example.todofx.entity;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;

public class User implements Entity<String> {
    private SimpleStringProperty id = new SimpleStringProperty();
    private SimpleStringProperty username = new SimpleStringProperty();
    private SimpleStringProperty passwordHash = new SimpleStringProperty();
    private SimpleObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private SimpleObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private SimpleObjectProperty<LocalDateTime> lastLogin = new SimpleObjectProperty<>();

    public User() {}

    public User(String id, String username, String passwordHash, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLogin) {
        this.id.set(id);
        this.username.set(username);
        this.passwordHash.set(passwordHash);
        this.createdAt.set(createdAt);
        this.updatedAt.set(updatedAt);
        this.lastLogin.set(lastLogin);
    }

    @Override
    public String getId() {
        return id.get();
    }

    @Override
    public void setId(String id) {
        this.id.set(id);
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPasswordHash() {
        return passwordHash.get();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash.set(passwordHash);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public LocalDateTime getLastLogin() {
        return lastLogin.get();
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin.set(lastLogin);
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public SimpleStringProperty passwordHashProperty() {
        return passwordHash;
    }

    public SimpleObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public SimpleObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public SimpleObjectProperty<LocalDateTime> lastLoginProperty() {
        return lastLogin;
    }
}