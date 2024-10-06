package com.example.todofx.ui;

import com.example.todofx.service.UserService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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
            if (validateInput(currentPasswordField, newPasswordField, confirmPasswordField)) {
                userService.changePassword(currentPasswordField.getText(), newPasswordField.getText())
                        .thenAccept(success -> {
                            if (success) {
                                CustomDialog.showAndWait(CustomDialog.DialogType.INFO, "成功", "密码修改成功", "您的密码已成功更新。");
                                close();
                            } else {
                                CustomDialog.showAndWait(CustomDialog.DialogType.ERROR, "错误", "密码修改失败", "请检查您的当前密码是否正确。");
                            }
                        });
            }
        });

        cancelButton.setOnAction(e -> close());

        grid.add(changeButton, 0, 3);
        grid.add(cancelButton, 1, 3);

        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());

        setScene(scene);
    }

    private boolean validateInput(PasswordField currentPasswordField, PasswordField newPasswordField, PasswordField confirmPasswordField) {
        List<String> errors = new ArrayList<>();

        if (currentPasswordField.getText().trim().isEmpty()) {
            errors.add("请输入当前密码");
        }
        if (newPasswordField.getText().trim().isEmpty()) {
            errors.add("请输入新密码");
        }
        if (confirmPasswordField.getText().trim().isEmpty()) {
            errors.add("请确认新密码");
        }
        if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
            errors.add("新密码和确认密码不匹配");
        }
        if (newPasswordField.getText().length() < 6) {
            errors.add("新密码长度必须至少为6个字符");
        }

        if (!errors.isEmpty()) {
            CustomDialog.showValidationErrors(errors);
            return false;
        }

        return true;
    }
}