package com.example.todofx.ui;

import com.example.todofx.entity.Todo;
import com.example.todofx.service.TodoService;
import com.example.todofx.service.UserService;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddTodoWindow extends Stage {
    private final TodoService todoService;
    private final UserService userService;

    public AddTodoWindow(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
        initUI();
    }

    private void initUI() {
        setTitle("添加新待办事项");

        VBox mainLayout = new VBox(10);
        mainLayout.getStyleClass().add("todo-window");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getStyleClass().add("grid");

        TextField titleField = new TextField();
        titleField.setPromptText("标题");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("描述");

        ComboBox<Todo.Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(Todo.Category.values());
        categoryComboBox.setPromptText("选择分类");
        categoryComboBox.getStyleClass().add("category-combo-box");

        DatePicker dueDatePicker = new DatePicker();

        grid.add(new Label("标题:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("分类:"), 0, 1);
        grid.add(categoryComboBox, 1, 1);
        grid.add(new Label("截止日期:"), 0, 2);
        grid.add(dueDatePicker, 1, 2);
        grid.add(new Label("描述:"), 0, 3);
        grid.add(descriptionArea, 1, 3);

        Button saveButton = new Button("保存");
        Button cancelButton = new Button("取消");

        saveButton.setOnAction(e -> {
            if (validateInput(titleField, categoryComboBox, dueDatePicker)) {
                Todo newTodo = new Todo();
                newTodo.titleProperty().set(titleField.getText());
                newTodo.descriptionProperty().set(descriptionArea.getText());
                newTodo.categoryProperty().set(categoryComboBox.getValue());
                newTodo.dueDateProperty().set(dueDatePicker.getValue());
                newTodo.statusProperty().set(Todo.Status.待办);
                newTodo.createdAtProperty().set(LocalDateTime.now());
                newTodo.updatedAtProperty().set(LocalDateTime.now());
                newTodo.userIdProperty().set(userService.getCurrentUser().getId());

                todoService.addTodo(newTodo);
                close();
            }
        });

        cancelButton.setOnAction(e -> close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getStyleClass().add("button-box");
        buttonBox.getChildren().addAll(saveButton, cancelButton);

        mainLayout.getChildren().addAll(grid, buttonBox);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/todofx/styles.css")).toExternalForm());

        setScene(scene);
    }

    private boolean validateInput(TextField titleField, ComboBox<Todo.Category> categoryComboBox, DatePicker dueDatePicker) {
        List<String> errors = new ArrayList<>();

        if (titleField.getText().trim().isEmpty()) {
            errors.add("标题不能为空");
        }
        if (categoryComboBox.getValue() == null) {
            errors.add("请选择一个分类");
        }
        if (dueDatePicker.getValue() == null) {
            errors.add("请选择截止日期");
        }

        if (!errors.isEmpty()) {
            CustomDialog.showValidationErrors(errors);
            return false;
        }

        return true;
    }
}