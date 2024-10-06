package com.example.todofx.service;

import com.example.todofx.dao.UserDao;
import com.example.todofx.entity.User;
import com.example.todofx.ui.CustomDialog;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UserService {
    private final UserDao userDao;
    private final ObjectProperty<User> currentUser = new SimpleObjectProperty<>();
    private final ExecutorService executorService;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
        this.executorService = Executors.newCachedThreadPool();
    }

    public CompletableFuture<Boolean> register(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return userDao.findAll().get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch users", e);
            }
        }, executorService).thenCompose(users -> {
            if (users.stream().anyMatch(u -> u.usernameProperty().get().equals(username))) {
                return CompletableFuture.completedFuture(false);
            }
            User newUser = new User();
            newUser.usernameProperty().set(username);
            try {
                newUser.passwordHashProperty().set(hashPassword(password));
            } catch (NoSuchAlgorithmException e) {
                CustomDialog.showException(e);
                return CompletableFuture.completedFuture(false);
            }
            newUser.createdAtProperty().set(LocalDateTime.now());
            newUser.updatedAtProperty().set(LocalDateTime.now());
            return userDao.save(newUser).thenApply(u -> true);
        }).exceptionally(ex -> {
            CustomDialog.showException(ex);
            return false;
        });
    }

    public CompletableFuture<Boolean> login(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return userDao.findAll().get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch users", e);
            }
        }, executorService).thenCompose(users -> {
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
        }).exceptionally(ex -> {
            CustomDialog.showException(ex);
            return false;
        });
    }

    public void logout() {
        currentUser.set(null);
    }

    public CompletableFuture<Boolean> changePassword(String oldPassword, String newPassword) {
        return CompletableFuture.supplyAsync(() -> {
            User user = currentUser.get();
            if (user != null) {
                try {
                    if (user.passwordHashProperty().get().equals(hashPassword(oldPassword))) {
                        user.passwordHashProperty().set(hashPassword(newPassword));
                        user.updatedAtProperty().set(LocalDateTime.now());
                        return userDao.save(user).thenApply(u -> true).get();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to change password", e);
                }
            }
            return false;
        }, executorService).exceptionally(ex -> {
            CustomDialog.showException(ex);
            return false;
        });
    }

    public CompletableFuture<Void> deleteAccount() {
        return CompletableFuture.runAsync(() -> {
            User user = currentUser.get();
            if (user != null) {
                try {
                    userDao.delete(user).get();
                    currentUser.set(null);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to delete account", e);
                }
            }
        }, executorService).exceptionally(ex -> {
            CustomDialog.showException(ex);
            return null;
        });
    }

    public User getCurrentUser() {
        return currentUser.get();
    }

    public ObjectProperty<User> currentUserProperty() {
        return currentUser;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}