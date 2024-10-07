package com.example.todofx.ui;

import com.example.todofx.MainApp;
import com.example.todofx.service.UserService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginScene {
    private MainApp mainApp;
    private UserService userService;
    private Scene scene;

    public LoginScene(MainApp mainApp, UserService userService) {
        this.mainApp = mainApp;
        this.userService = userService;
        createLoginScene();
    }

    private void createLoginScene() {
        VBox loginBox = new VBox(20);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(50, 100, 50, 100));

        TextField usernameField = new TextField();
        usernameField.setPromptText("用户名");
        usernameField.setMaxWidth(200);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码");
        passwordField.setMaxWidth(200);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("登录");
        loginButton.setOnAction(e -> login(usernameField.getText(), passwordField.getText()));

        Button registerButton = new Button("注册");
        registerButton.setOnAction(e -> register(usernameField.getText(), passwordField.getText()));

        buttonBox.getChildren().addAll(loginButton, registerButton);

        Label titleLabel = new Label("待办事项应用登录");
        titleLabel.getStyleClass().add("login-title");

        loginBox.getChildren().addAll(
                titleLabel,
                usernameField,
                passwordField,
                buttonBox
        );

        scene = new Scene(loginBox, 400, 300);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/todofx/styles.css")).toExternalForm());
    }

    private void login(String username, String password) {
        if (validateLoginInput(username, password)) {
            userService.login(username, password)
                    .thenAcceptAsync(success -> {
                        if (success) {
                            Platform.runLater(() -> mainApp.showMainScene());
                        } else {
                            Platform.runLater(() -> CustomDialog.showAndWait(CustomDialog.DialogType.ERROR, "登录失败", "用户名或密码无效", "请检查您的用户名和密码，然后重试。"));
                        }
                    }, Platform::runLater)
                    .exceptionally(ex -> {
                        Platform.runLater(() -> CustomDialog.showException(ex));
                        return null;
                    });
        }
    }

    private void register(String username, String password) {
        if (validateRegisterInput(username, password)) {
            userService.register(username, password)
                    .thenAcceptAsync(success -> {
                        if (success) {
                            Platform.runLater(() -> CustomDialog.showAndWait(CustomDialog.DialogType.INFO, "注册成功", "账户已创建", "您现在可以使用新的凭据登录。"));
                        } else {
                            Platform.runLater(() -> CustomDialog.showAndWait(CustomDialog.DialogType.ERROR, "注册失败", "用户名已存在", "请选择一个不同的用户名。"));
                        }
                    }, Platform::runLater)
                    .exceptionally(ex -> {
                        Platform.runLater(() -> CustomDialog.showException(ex));
                        return null;
                    });
        }
    }

    private boolean validateLoginInput(String username, String password) {
        List<String> errors = new ArrayList<>();

        if (username.trim().isEmpty()) {
            errors.add("用户名不能为空");
        }
        if (password.trim().isEmpty()) {
            errors.add("密码不能为空");
        }

        if (!errors.isEmpty()) {
            CustomDialog.showValidationErrors(errors);
            return false;
        }

        return true;
    }

    private boolean validateRegisterInput(String username, String password) {
        List<String> errors = new ArrayList<>();

        if (username.trim().isEmpty()) {
            errors.add("用户名不能为空");
        }
        if (password.trim().isEmpty()) {
            errors.add("密码不能为空");
        }
        if (password.length() < 6) {
            errors.add("密码长度必须至少为6个字符");
        }

        if (!errors.isEmpty()) {
            CustomDialog.showValidationErrors(errors);
            return false;
        }

        return true;
    }

    public Scene getScene() {
        return scene;
    }
}