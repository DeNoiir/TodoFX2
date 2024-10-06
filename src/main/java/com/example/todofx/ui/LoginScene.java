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

public class LoginScene {
    private MainApp mainApp;
    private UserService userService;
    private Scene scene;
    private Label messageLabel;

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

        messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setAlignment(Pos.CENTER);

        loginBox.getChildren().addAll(
                titleLabel,
                usernameField,
                passwordField,
                buttonBox,
                messageLabel
        );

        scene = new Scene(loginBox, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());
    }

    private void login(String username, String password) {
        try {
            userService.login(username, password)
                    .thenAcceptAsync(success -> {
                        if (success) {
                            Platform.runLater(() -> {
                                mainApp.showMainScene();
                            });
                        } else {
                            Platform.runLater(() -> showMessage("登录失败：用户名或密码无效", true));
                        }
                    }, Platform::runLater)
                    .exceptionally(ex -> {
                        Platform.runLater(() -> showMessage("发生错误：" + ex.getMessage(), true));
                        return null;
                    });
        } catch (Exception e) {
            Platform.runLater(() -> showMessage("发生错误：" + e.getMessage(), true));
        }
    }

    private void register(String username, String password) {
        try {
            userService.register(username, password)
                    .thenAcceptAsync(success -> {
                        if (success) {
                            Platform.runLater(() -> showMessage("注册成功：您现在可以使用新的凭据登录", false));
                        } else {
                            Platform.runLater(() -> showMessage("注册失败：用户名已存在", true));
                        }
                    }, Platform::runLater)
                    .exceptionally(ex -> {
                        Platform.runLater(() -> showMessage("发生错误：" + ex.getMessage(), true));
                        return null;
                    });
        } catch (Exception e) {
            Platform.runLater(() -> showMessage("发生错误：" + e.getMessage(), true));
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        if (isError) {
            messageLabel.getStyleClass().add("error-message");
        } else {
            messageLabel.getStyleClass().remove("error-message");
        }
    }

    public Scene getScene() {
        return scene;
    }
}