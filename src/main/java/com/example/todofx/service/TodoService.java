package com.example.todofx.service;

import com.example.todofx.dao.TodoDao;
import com.example.todofx.entity.Todo;
import com.example.todofx.entity.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TodoService {
    private static final Logger logger = Logger.getLogger(TodoService.class.getName());
    private final TodoDao todoDao;
    private final UserService userService;
    private final ObservableList<Todo> todos = FXCollections.observableArrayList();
    private boolean listenersEnabled = true;
    private final ExecutorService executorService;

    public TodoService(TodoDao todoDao, UserService userService) {
        this.todoDao = todoDao;
        this.userService = userService;
        this.executorService = Executors.newCachedThreadPool();
        setupListeners();
        setupUserListener();
    }

    public void loadTodos() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            logger.warning("No current user logged in, cannot load todos.");
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            try {
                return todoDao.findAll().get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load todos", e);
            }
        }, executorService).thenAccept(todoList -> {
            List<Todo> userTodos = todoList.stream()
                    .filter(todo -> todo.getUserId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
            Platform.runLater(() -> {
                disableListeners();
                todos.setAll(userTodos);
                enableListeners();
            });
        }).exceptionally(ex -> {
            logger.severe("Failed to load todos: " + ex.getMessage());
            return null;
        });
    }

    private void setupListeners() {
        todos.addListener((javafx.collections.ListChangeListener.Change<? extends Todo> c) -> {
            if (!listenersEnabled) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(this::saveTodoAsync);
                }
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(this::deleteTodoAsync);
                }
                if (c.wasUpdated()) {
                    int index = c.getFrom();
                    while (index < c.getTo()) {
                        saveTodoAsync(todos.get(index));
                        index++;
                    }
                }
            }
        });
    }

    private void setupUserListener() {
        userService.currentUserProperty().addListener((observable, oldUser, newUser) -> {
            if (newUser != null) {
                loadTodos();
            } else {
                Platform.runLater(() -> {
                    disableListeners();
                    todos.clear();
                    enableListeners();
                });
            }
        });
    }

    private void disableListeners() {
        listenersEnabled = false;
    }

    private void enableListeners() {
        listenersEnabled = true;
    }

    private void saveTodoAsync(Todo todo) {
        executorService.submit(() -> {
            try {
                todoDao.save(todo).get();
            } catch (Exception e) {
                logger.severe("Failed to save todo: " + e.getMessage());
                throw new RuntimeException("Failed to save todo", e);
            }
        });
    }

    private void deleteTodoAsync(Todo todo) {
        executorService.submit(() -> {
            try {
                todoDao.delete(todo).get();
            } catch (Exception e) {
                logger.severe("Failed to delete todo: " + e.getMessage());
                throw new RuntimeException("Failed to delete todo", e);
            }
        });
    }

    public void addTodo(Todo todo) {
        executorService.submit(() -> {
            try {
                todoDao.save(todo).get();
                Platform.runLater(() -> todos.add(todo));
            } catch (Exception e) {
                logger.severe("Failed to add todo: " + e.getMessage());
                throw new RuntimeException("Failed to add todo", e);
            }
        });
    }

    public void updateTodo(Todo todo) {
        executorService.submit(() -> {
            try {
                todoDao.save(todo).get();
                Platform.runLater(() -> {
                    int index = todos.indexOf(todo);
                    if (index != -1) {
                        todos.remove(index);
                        todos.add(index, todo);
                    }
                });
            } catch (Exception e) {
                logger.severe("Failed to update todo: " + e.getMessage());
                throw new RuntimeException("Failed to update todo", e);
            }
        });
    }

    public void deleteTodo(Todo todo) {
        executorService.submit(() -> {
            try {
                todoDao.delete(todo).get();
                Platform.runLater(() -> todos.remove(todo));
            } catch (Exception e) {
                logger.severe("Failed to delete todo: " + e.getMessage());
                throw new RuntimeException("Failed to delete todo", e);
            }
        });
    }

    public List<Todo> getTodos() {
        return new ArrayList<>(todos);
    }

    public List<Todo> getInbox() {
        return todos.stream()
                .filter(todo -> todo.dueDateProperty().get() == null || todo.categoryProperty().get() == null)
                .collect(Collectors.toList());
    }

    public List<Todo> getToday() {
        LocalDate today = LocalDate.now();
        return todos.stream()
                .filter(todo -> todo.dueDateProperty().get() != null && todo.dueDateProperty().get().equals(today))
                .collect(Collectors.toList());
    }

    public List<Todo> getNext7Days() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysLater = today.plusDays(7);
        return todos.stream()
                .filter(todo -> todo.dueDateProperty().get() != null &&
                        !todo.dueDateProperty().get().isBefore(today) &&
                        !todo.dueDateProperty().get().isAfter(sevenDaysLater))
                .collect(Collectors.toList());
    }

    public List<Todo> getByCategory(Todo.Category category) {
        return todos.stream()
                .filter(todo -> (category == null && todo.categoryProperty().get() == null) ||
                        (category != null && category.equals(todo.categoryProperty().get())))
                .collect(Collectors.toList());
    }

    public List<Todo> getByStatus(Todo.Status status) {
        return todos.stream()
                .filter(todo -> todo.statusProperty().get() == status)
                .collect(Collectors.toList());
    }

    public void logout() {
        disableListeners();
        todos.clear();
        enableListeners();
        userService.logout();
    }

    public ObservableList<Todo> getTodosObservableList() {
        return todos;
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