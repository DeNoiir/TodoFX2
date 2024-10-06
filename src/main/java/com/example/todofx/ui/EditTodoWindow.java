package com.example.todofx.ui;

import com.example.todofx.entity.Todo;
import com.example.todofx.service.TodoService;
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

public class EditTodoWindow extends Stage {
    private final TodoService todoService;
    private final Todo todo;

    public EditTodoWindow(TodoService todoService, Todo todo) {
        this.todoService = todoService;
        this.todo = todo;
        initUI();
    }

    private void initUI() {
        setTitle("编辑待办事项");

        VBox mainLayout = new VBox(10);
        mainLayout.getStyleClass().add("todo-window");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField titleField = new TextField(todo.titleProperty().get());
        TextArea descriptionArea = new TextArea(todo.descriptionProperty().get());

        ComboBox<Todo.Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(Todo.Category.values());
        categoryComboBox.setValue(todo.categoryProperty().get());

        DatePicker dueDatePicker = new DatePicker(todo.dueDateProperty().get());

        ComboBox<Todo.Status> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(Todo.Status.values());
        statusComboBox.setValue(todo.statusProperty().get());

        grid.add(new Label("标题:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("分类:"), 0, 1);
        grid.add(categoryComboBox, 1, 1);
        grid.add(new Label("截止日期:"), 0, 2);
        grid.add(dueDatePicker, 1, 2);
        grid.add(new Label("状态:"), 0, 3);
        grid.add(statusComboBox, 1, 3);
        grid.add(new Label("描述:"), 0, 4);
        grid.add(descriptionArea, 1, 4);

        Button saveButton = new Button("保存");
        Button deleteButton = new Button("删除");
        Button cancelButton = new Button("取消");

        saveButton.setOnAction(e -> {
            if (validateInput(titleField, categoryComboBox, dueDatePicker, statusComboBox)) {
                todo.titleProperty().set(titleField.getText());
                todo.descriptionProperty().set(descriptionArea.getText());
                todo.categoryProperty().set(categoryComboBox.getValue());
                todo.dueDateProperty().set(dueDatePicker.getValue());
                todo.statusProperty().set(statusComboBox.getValue());
                todo.updatedAtProperty().set(LocalDateTime.now());

                todoService.updateTodo(todo);
                close();
            }
        });

        deleteButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("确认删除");
            alert.setHeaderText("您确定要删除这个待办事项吗？");
            alert.setContentText("此操作不可撤销。");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    todoService.deleteTodo(todo);
                    close();
                }
            });
        });

        cancelButton.setOnAction(e -> close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(saveButton, deleteButton, cancelButton);

        mainLayout.getChildren().addAll(grid, buttonBox);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());

        setScene(scene);
    }

    private boolean validateInput(TextField titleField, ComboBox<Todo.Category> categoryComboBox,
                                  DatePicker dueDatePicker, ComboBox<Todo.Status> statusComboBox) {
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
        if (statusComboBox.getValue() == null) {
            errors.add("请选择一个状态");
        }

        if (!errors.isEmpty()) {
            CustomDialog.showValidationErrors(errors);
            return false;
        }

        return true;
    }
}