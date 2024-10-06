package com.example.todofx.ui;

import com.example.todofx.entity.Todo;
import com.example.todofx.service.TodoService;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDateTime;

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

        grid.add(new Label("标题:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("分类:"), 0, 1);
        grid.add(categoryComboBox, 1, 1);
        grid.add(new Label("截止日期:"), 0, 2);
        grid.add(dueDatePicker, 1, 2);
        grid.add(new Label("描述:"), 0, 3);
        grid.add(descriptionArea, 1, 3);

        Button saveButton = new Button("保存");
        Button deleteButton = new Button("删除");
        Button cancelButton = new Button("取消");

        saveButton.setOnAction(e -> {
            try {
                todo.titleProperty().set(titleField.getText());
                todo.descriptionProperty().set(descriptionArea.getText());
                todo.categoryProperty().set(categoryComboBox.getValue());
                todo.dueDateProperty().set(dueDatePicker.getValue());
                todo.updatedAtProperty().set(LocalDateTime.now());

                todoService.updateTodo(todo);
                close();
            } catch (Exception ex) {
                Platform.runLater(() -> new ExceptionDialog(ex).showAndWait());
            }
        });

        deleteButton.setOnAction(e -> {
            try {
                todoService.deleteTodo(todo);
                close();
            } catch (Exception ex) {
                Platform.runLater(() -> new ExceptionDialog(ex).showAndWait());
            }
        });
        cancelButton.setOnAction(e -> close());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(saveButton, deleteButton, cancelButton);

        mainLayout.getChildren().addAll(grid, buttonBox);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());

        // 加载字体
        Font.loadFont(getClass().getResourceAsStream("/com/example/todofx/FZfont140.TTF"), 14);

        setScene(scene);
    }
}