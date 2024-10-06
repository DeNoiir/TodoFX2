package com.example.todofx;

import com.example.todofx.dao.TodoDao;
import com.example.todofx.dao.UserDao;
import com.example.todofx.service.PomoService;
import com.example.todofx.service.TodoService;
import com.example.todofx.service.UserService;
import com.example.todofx.ui.ExceptionDialog;
import com.example.todofx.ui.LoginScene;
import com.example.todofx.ui.MainScene;
import com.example.todofx.util.DatabaseConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.sql.Connection;

public class MainApp extends Application {
    private UserService userService;
    private TodoService todoService;
    private PomoService pomoService;
    private Stage primaryStage;
    private Stage loginStage;
    private double initialWidth = 1000;
    private double initialHeight = 600;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.loginStage = new Stage();
        initializeServices();
        setupLoginStage();
        primaryStage.setResizable(true);
        loginStage.show();

        loginStage.setOnCloseRequest(event -> Platform.exit());
        primaryStage.setOnCloseRequest(event -> Platform.exit());
    }

    private void initializeServices() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            UserDao userDao = new UserDao(connection);
            TodoDao todoDao = new TodoDao(connection);
            userService = new UserService(userDao);
            todoService = new TodoService(todoDao, userService);
            pomoService = new PomoService(); // 初始化 PomoService
        } catch (Exception e) {
            Platform.runLater(() -> {
                new ExceptionDialog(e).showAndWait();
                System.exit(1);
            });
        }
    }

    private void setupLoginStage() {
        LoginScene loginScene = new LoginScene(this, userService);
        loginStage.setScene(loginScene.getScene());
        loginStage.getScene().getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());
        loginStage.setTitle("Todo App - Login");
        loginStage.setResizable(false);
        loginStage.centerOnScreen();
    }

    public void showMainScene() {
        MainScene mainScene = new MainScene(this, userService, todoService, pomoService, initialWidth, initialHeight);
        primaryStage.setScene(mainScene.getScene());
        mainScene.getScene().getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());
        primaryStage.setTitle("Todo App - Main");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();
        loginStage.close();
        primaryStage.show();
    }

    public void logout() {
        todoService.logout();
        userService.logout();
        setupLoginStage();

        Platform.runLater(() -> {
            loginStage.setWidth(400);
            loginStage.setHeight(300);
            loginStage.centerOnScreen();
            primaryStage.hide();
            loginStage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        DatabaseConnection.closeConnection();
        super.stop();
    }
}