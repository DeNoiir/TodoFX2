package com.example.todofx.entity;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Todo implements Entity<String> {
    private SimpleStringProperty id = new SimpleStringProperty();
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleStringProperty description = new SimpleStringProperty();
    private SimpleObjectProperty<Category> category = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Status> status = new SimpleObjectProperty<>(Status.待办);
    private SimpleObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>();
    private SimpleObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private SimpleObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private SimpleStringProperty userId = new SimpleStringProperty();

    public enum Category {
        学业, 个人发展, 生活管理, 人际关系, 健康与休闲
    }

    public enum Status {
        待办, 进行中, 已完成
    }

    public Todo() {
        this.id = new SimpleStringProperty();
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.category = new SimpleObjectProperty<>();
        this.status = new SimpleObjectProperty<>(Status.待办);
        this.dueDate = new SimpleObjectProperty<>();
        this.createdAt = new SimpleObjectProperty<>(LocalDateTime.now());
        this.updatedAt = new SimpleObjectProperty<>(LocalDateTime.now());
        this.userId = new SimpleStringProperty();
    }

    public Todo(String id, String title, String description, Category category, Status status, LocalDate dueDate, LocalDateTime createdAt, LocalDateTime updatedAt, String userId) {
        this.id.set(id);
        this.title.set(title);
        this.description.set(description);
        this.category.set(category);
        this.status.set(status);
        this.dueDate.set(dueDate);
        this.createdAt.set(createdAt);
        this.updatedAt.set(updatedAt);
        this.userId.set(userId);
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

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Category getCategory() {
        return category.get();
    }

    public void setCategory(Category category) {
        this.category.set(category);
    }

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public LocalDate getDueDate() {
        return dueDate.get();
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate.set(dueDate);
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

    public String getUserId() {
        return userId.get();
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleObjectProperty<Category> categoryProperty() {
        return category;
    }

    public SimpleObjectProperty<Status> statusProperty() {
        return status;
    }

    public SimpleObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    public SimpleObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public SimpleObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public SimpleStringProperty userIdProperty() {
        return userId;
    }
}