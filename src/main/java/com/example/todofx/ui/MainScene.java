package com.example.todofx.ui;

import com.example.todofx.MainApp;
import com.example.todofx.entity.Todo;
import com.example.todofx.service.PomoService;
import com.example.todofx.service.TodoService;
import com.example.todofx.service.UserService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.stream.Collectors;

public class MainScene {
    private MainApp mainApp;
    private UserService userService;
    private TodoService todoService;
    private PomoService pomoService;
    private Scene scene;
    private PomoWindow pomoWindow;

    public MainScene(MainApp mainApp, UserService userService, TodoService todoService, PomoService pomoService, double width, double height) {
        this.mainApp = mainApp;
        this.userService = userService;
        this.todoService = todoService;
        this.pomoService = pomoService;
        createMainScene(width, height);
    }

    private void createMainScene(double width, double height) {
        BorderPane mainLayout = new BorderPane();

        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        HBox kanbanBoard = createKanbanBoard();

        StackPane centerContent = new StackPane();
        centerContent.getChildren().add(kanbanBoard);

        HBox buttonBox = createFloatingButtons();
        StackPane.setAlignment(buttonBox, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(buttonBox, new Insets(0, 20, 20, 0));
        centerContent.getChildren().add(buttonBox);

        mainLayout.setCenter(centerContent);

        scene = new Scene(mainLayout, width, height);
        scene.getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());

        todoService.getTodosObservableList().addListener((ListChangeListener<Todo>) c -> {
            updateKanbanBoard(todoService.getTodos());
        });
    }

    private HBox createFloatingButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(Region.USE_PREF_SIZE);
        buttonBox.setMaxHeight(Region.USE_PREF_SIZE);

        Button addButton = new Button("➕");
        addButton.getStyleClass().addAll("emoji-button", "add-button");
        addButton.setOnAction(e -> showAddTodoWindow());

        Button pomoButton = new Button("🍅");
        pomoButton.getStyleClass().addAll("emoji-button", "pomo-button");
        pomoButton.setOnAction(e -> showPomoWindow());

        buttonBox.getChildren().addAll(addButton, pomoButton);
        return buttonBox;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(210);

        Label userLabel = new Label(userService.getCurrentUser().usernameProperty().get());
        userLabel.getStyleClass().add("sidebar-username");

        Button allButton = createSidebarButton("全部");
        allButton.setOnAction(e -> updateKanbanBoard(todoService.getTodos()));

        Button inboxButton = createSidebarButton("收件箱");
        inboxButton.setOnAction(e -> updateKanbanBoard(todoService.getInbox()));

        Button todayButton = createSidebarButton("今天");
        todayButton.setOnAction(e -> updateKanbanBoard(todoService.getToday()));

        Button next7DaysButton = createSidebarButton("未来7天");
        next7DaysButton.setOnAction(e -> updateKanbanBoard(todoService.getNext7Days()));

        VBox categoryButtons = new VBox(5);
        for (Todo.Category category : Todo.Category.values()) {
            Button categoryButton = createSidebarButton(category.name());
            categoryButton.setOnAction(e -> updateKanbanBoard(todoService.getByCategory(category)));
            categoryButtons.getChildren().add(categoryButton);
        }

        HBox authButtons = new HBox(10);
        Button changePasswordButton = createSidebarButton("修改密码");
        changePasswordButton.setOnAction(e -> showChangePasswordWindow());
        Button logoutButton = createSidebarButton("登出");
        logoutButton.setOnAction(e -> mainApp.logout());
        authButtons.getChildren().addAll(changePasswordButton, logoutButton);

        sidebar.getChildren().addAll(userLabel, allButton, inboxButton, todayButton, next7DaysButton, categoryButtons, authButtons);
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private HBox createKanbanBoard() {
        HBox kanbanBoard = new HBox(10);
        kanbanBoard.setPadding(new Insets(10));
        HBox.setHgrow(kanbanBoard, Priority.ALWAYS);

        VBox todoColumn = createKanbanColumn("待办", Todo.Status.待办);
        VBox inProgressColumn = createKanbanColumn("进行中", Todo.Status.进行中);
        VBox doneColumn = createKanbanColumn("已完成", Todo.Status.已完成);

        kanbanBoard.getChildren().addAll(todoColumn, inProgressColumn, doneColumn);
        return kanbanBoard;
    }

    private VBox createKanbanColumn(String title, Todo.Status status) {
        VBox column = new VBox(10);
        column.getStyleClass().add("kanban-column");
        column.setPrefWidth(300);
        HBox.setHgrow(column, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("column-title");

        ListView<Todo> todoList = new ListView<>();
        todoList.getStyleClass().add("kanban-list-view");
        todoList.setItems(FXCollections.observableArrayList(todoService.getByStatus(status)));
        todoList.setCellFactory(param -> new TodoListCell());

        // 设置 ListView 的宽度和高度属性
        todoList.setPrefWidth(0); // 允许 ListView 收缩到其内容的大小
        todoList.setMaxWidth(Double.MAX_VALUE);
        todoList.setMinHeight(100); // 设置一个最小高度，以确保列表始终可见
        todoList.setMaxHeight(Double.MAX_VALUE);

        // 允许 ListView 在 VBox 中垂直增长
        VBox.setVgrow(todoList, Priority.ALWAYS);

        // 设置列表视图的固定单元格大小
        todoList.setFixedCellSize(70); // 根据您的 todo 卡片设计调整这个值

        // 绑定列表视图的预设高度到其内容
        todoList.prefHeightProperty().bind(Bindings.size(todoList.getItems()).multiply(todoList.getFixedCellSize()).add(2));

        setupDragAndDrop(todoList, status);

        column.getChildren().addAll(titleLabel, todoList);

        // 确保 VBox 也可以在水平方向上增长
        HBox.setHgrow(column, Priority.ALWAYS);

        return column;
    }

    private void setupDragAndDrop(ListView<Todo> todoList, Todo.Status status) {
        todoList.setOnDragDetected(event -> {
            Todo selected = todoList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Dragboard db = todoList.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(selected.getId());
                db.setContent(content);
                event.consume();
            }
        });

        todoList.setOnDragOver(event -> {
            if (event.getGestureSource() != todoList && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        todoList.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String todoId = db.getString();
                Todo todo = todoService.getTodos().stream()
                        .filter(t -> t.getId().equals(todoId))
                        .findFirst()
                        .orElse(null);
                if (todo != null) {
                    todo.statusProperty().set(status);
                    todoService.updateTodo(todo);
                    updateKanbanBoard(todoService.getTodos());
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        todoList.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                updateKanbanBoard(todoService.getTodos());
            }
            event.consume();
        });
    }

    private void updateKanbanBoard(java.util.List<Todo> todos) {
        HBox kanbanBoard = (HBox) ((StackPane) ((BorderPane) scene.getRoot()).getCenter()).getChildren().get(0);
        for (javafx.scene.Node node : kanbanBoard.getChildren()) {
            if (node instanceof VBox) {
                VBox column = (VBox) node;
                ListView<Todo> listView = (ListView<Todo>) column.getChildren().get(1);
                Label titleLabel = (Label) column.getChildren().get(0);
                Todo.Status status = getTodoStatusFromTitle(titleLabel.getText());
                listView.setItems(FXCollections.observableArrayList(
                        todos.stream().filter(todo -> todo.statusProperty().get() == status).collect(Collectors.toList())
                ));
            }
        }
    }

    private Todo.Status getTodoStatusFromTitle(String title) {
        switch (title) {
            case "待办":
                return Todo.Status.待办;
            case "进行中":
                return Todo.Status.进行中;
            case "已完成":
                return Todo.Status.已完成;
            default:
                throw new IllegalArgumentException("无效的列标题: " + title);
        }
    }

    private void showAddTodoWindow() {
        AddTodoWindow addTodoWindow = new AddTodoWindow(todoService, userService);
        addTodoWindow.showAndWait();
        updateKanbanBoard(todoService.getTodos());
    }

    private void showEditTodoWindow(Todo todo) {
        EditTodoWindow editTodoWindow = new EditTodoWindow(todoService, todo);
        editTodoWindow.showAndWait();
        updateKanbanBoard(todoService.getTodos());
    }

    private void showChangePasswordWindow() {
        ChangePasswordWindow changePasswordWindow = new ChangePasswordWindow(userService);
        changePasswordWindow.showAndWait();
    }

    private void showPomoWindow() {
        if (pomoWindow == null || !pomoWindow.isShowing()) {
            pomoWindow = new PomoWindow(pomoService);
            pomoWindow.show();
        } else {
            pomoWindow.requestFocus();
        }
    }

    public Scene getScene() {
        return scene;
    }

    private class TodoListCell extends ListCell<Todo> {
        private HBox hbox;
        private Label titleLabel;
        private Label dueDateLabel;
        private final Text helper;

        public TodoListCell() {
            hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getStyleClass().add("todo-cell");

            titleLabel = new Label();
            titleLabel.getStyleClass().add("todo-title");
            titleLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(titleLabel, Priority.ALWAYS);

            dueDateLabel = new Label();
            dueDateLabel.setAlignment(Pos.CENTER_RIGHT);
            dueDateLabel.setMinWidth(Region.USE_PREF_SIZE);

            hbox.getChildren().addAll(titleLabel, dueDateLabel);

            helper = new Text();
            helper.setWrappingWidth(0);
        }

        @Override
        protected void updateItem(Todo todo, boolean empty) {
            super.updateItem(todo, empty);
            if (empty || todo == null) {
                setText(null);
                setGraphic(null);
            } else {
                String fullTitle = todo.titleProperty().get();
                titleLabel.setText(fullTitle);
                dueDateLabel.setText(todo.dueDateProperty().get() != null ? todo.dueDateProperty().get().toString() : "无截止日期");
                setGraphic(hbox);

                hbox.prefWidthProperty().bind(getListView().widthProperty().subtract(20));

                hbox.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                    double availableWidth = newWidth.doubleValue() - dueDateLabel.getWidth() - 20;
                    if (availableWidth > 0) {
                        helper.setText(fullTitle);
                        while (helper.getLayoutBounds().getWidth() > availableWidth && helper.getText().length() > 3) {
                            helper.setText(helper.getText().substring(0, helper.getText().length() - 4) + "...");
                        }
                        titleLabel.setText(helper.getText());
                    }
                });

                setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1) {
                        showEditTodoWindow(todo);
                    }
                });
            }
        }
    }
}