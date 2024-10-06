package com.example.todofx.service;

import com.example.todofx.dao.UserDao;
import com.example.todofx.entity.User;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserService {
    private final UserDao userDao;
    private final ObjectProperty<User> currentUser = new SimpleObjectProperty<>();

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public CompletableFuture<Boolean> register(String username, String password) throws Exception {
        return userDao.findAll().thenCompose(users -> {
            if (users.stream().anyMatch(u -> u.usernameProperty().get().equals(username))) {
                return CompletableFuture.completedFuture(false);
            }
            User newUser = new User();
            newUser.usernameProperty().set(username);
            try {
                newUser.passwordHashProperty().set(hashPassword(password));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            newUser.createdAtProperty().set(LocalDateTime.now());
            newUser.updatedAtProperty().set(LocalDateTime.now());
            return userDao.save(newUser).thenApply(u -> true);
        });
    }

    public CompletableFuture<Boolean> login(String username, String password) throws Exception {
        return userDao.findAll().thenCompose(users -> {
            Optional<User> user = users.stream()
                    .filter(u -> {
                        try {
                            return u.usernameProperty().get().equals(username) &&
                                    u.passwordHashProperty().get().equals(hashPassword(password));
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .findFirst();
            if (user.isPresent()) {
                User loggedInUser = user.get();
                loggedInUser.lastLoginProperty().set(LocalDateTime.now());
                return userDao.save(loggedInUser).thenApply(u -> {
                    currentUser.set(u);
                    return true;
                });
            }
            return CompletableFuture.completedFuture(false);
        });
    }

    public void logout() {
        currentUser.set(null);
    }

    public CompletableFuture<Boolean> changePassword(String oldPassword, String newPassword) throws Exception {
        User user = currentUser.get();
        if (user != null && user.passwordHashProperty().get().equals(hashPassword(oldPassword))) {
            user.passwordHashProperty().set(hashPassword(newPassword));
            user.updatedAtProperty().set(LocalDateTime.now());
            return userDao.save(user).thenApply(u -> true);
        }
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<Void> deleteAccount() throws Exception {
        User user = currentUser.get();
        if (user != null) {
            return userDao.delete(user).thenRun(() -> currentUser.set(null));
        }
        return CompletableFuture.completedFuture(null);
    }

    public User getCurrentUser() {
        return currentUser.get();
    }

    public ObjectProperty<User> currentUserProperty() {
        return currentUser;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("Error hashing password", e);
        }
    }
}