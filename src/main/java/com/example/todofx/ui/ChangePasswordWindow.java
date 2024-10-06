package com.example.todofx.ui;

import com.example.todofx.service.UserService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChangePasswordWindow extends Stage {
    private final UserService userService;

    public ChangePasswordWindow(UserService userService) {
        this.userService = userService;
        initUI();
    }

    private void initUI() {
        setTitle("修改密码");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField currentPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        grid.add(new Label("当前密码:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("新密码:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("确认新密码:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        Button changeButton = new Button("修改");
        Button cancelButton = new Button("取消");

        changeButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            if (newPassword.equals(confirmPasswordField.getText())) {
                try {
                    userService.changePassword(currentPasswordField.getText(), newPassword)
                            .thenAccept(success -> {
                                if (success) {
                                    Platform.runLater(() -> {
                                        showAlert("成功", "密码修改成功");
                                        close();
                                    });
                                } else {
                                    Platform.runLater(() -> showAlert("错误", "密码修改失败。请检查您的当前密码。"));
                                }
                            });
                } catch (Exception ex) {
                    Platform.runLater(() -> new ExceptionDialog(ex).showAndWait());
                }
            } else {
                showAlert("错误", "新密码不匹配");
            }
        });

        cancelButton.setOnAction(e -> close());

        grid.add(changeButton, 0, 3);
        grid.add(cancelButton, 1, 3);

        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());

        // 加载字体
        Font.loadFont(getClass().getResourceAsStream("/com/example/todofx/FZfont140.TTF"), 14);

        setScene(scene);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());
        alert.showAndWait();
    }
}