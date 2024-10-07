package com.example.todofx.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.scene.layout.Region;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class CustomDialog extends Dialog<ButtonType> {

    private final DialogType dialogType;
    private TextArea detailsTextArea;

    public enum DialogType {
        INFO, WARNING, ERROR, EXCEPTION
    }

    public CustomDialog(DialogType type, String title, String headerText, String contentText) {
        this.dialogType = type;
        initDialog(title, headerText, contentText);
    }

    public CustomDialog(Throwable ex) {
        this.dialogType = DialogType.EXCEPTION;
        initDialog(getHeaderText(ex), getHeaderText(ex), getContentText(ex));
        this.detailsTextArea = createDetailsTextArea(getStackTraceText(ex));
        getDialogPane().setExpandableContent(createExpandableContent(detailsTextArea));
    }

    private void initDialog(String title, String headerText, String contentText) {
        setHeaderText(headerText);
        setContentText(contentText);

        getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());
        getDialogPane().getStyleClass().add("custom-dialog");
        getDialogPane().getStyleClass().add(getStyleClass());

        // 移除窗口装饰
        initStyle(StageStyle.TRANSPARENT);

        // 设置窗口大小根据内容自动调整
        getDialogPane().setPrefWidth(Region.USE_COMPUTED_SIZE);
        getDialogPane().setPrefHeight(Region.USE_COMPUTED_SIZE);

        // 确保对话框面板的背景是透明的
        getDialogPane().setStyle("-fx-background-color: transparent;");

        // 让对话框可以移动
        makeDraggable();
    }

    private void makeDraggable() {
        final Delta dragDelta = new Delta();
        getDialogPane().setOnMousePressed(mouseEvent -> {
            dragDelta.x = getX() - mouseEvent.getScreenX();
            dragDelta.y = getY() - mouseEvent.getScreenY();
        });
        getDialogPane().setOnMouseDragged(mouseEvent -> {
            setX(mouseEvent.getScreenX() + dragDelta.x);
            setY(mouseEvent.getScreenY() + dragDelta.y);
        });
    }

    private String getStyleClass() {
        switch (dialogType) {
            case INFO:
                return "info-dialog";
            case WARNING:
                return "warning-dialog";
            case ERROR:
            case EXCEPTION:
                return "error-dialog";
            default:
                return "";
        }
    }

    private String getHeaderText(Throwable ex) {
        return "发生了一个" + getExceptionName(ex);
    }

    private String getContentText(Throwable ex) {
        String message = ex.getMessage();
        return message != null && !message.isEmpty() ? message : "没有提供详细信息。";
    }

    private String getExceptionName(Throwable ex) {
        String simpleName = ex.getClass().getSimpleName();
        return simpleName.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
    }

    private TextArea createDetailsTextArea(String text) {
        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        return textArea;
    }

    private String getStackTraceText(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

    private GridPane createExpandableContent(TextArea textArea) {
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label("详细信息:"), 0, 0);
        expContent.add(textArea, 0, 1);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        expContent.setPrefWidth(600);
        expContent.setPrefHeight(400);
        expContent.setPadding(new Insets(10));

        return expContent;
    }

    public void setValidationErrors(List<String> errors) {
        VBox content = new VBox(10);
        content.getChildren().add(new Label("请修正以下错误:"));
        for (String error : errors) {
            content.getChildren().add(new Label("• " + error));
        }
        getDialogPane().setContent(content);
    }

    public static void showAndWait(DialogType type, String title, String headerText, String contentText) {
        Platform.runLater(() -> {
            CustomDialog dialog = new CustomDialog(type, title, headerText, contentText);
            dialog.showAndWait();
        });
    }

    public static void showException(Throwable ex) {
        Platform.runLater(() -> {
            CustomDialog dialog = new CustomDialog(ex);
            dialog.showAndWait();
        });
    }

    public static void showValidationErrors(List<String> errors) {
        Platform.runLater(() -> {
            CustomDialog dialog = new CustomDialog(DialogType.ERROR, "验证错误", "表单验证失败", null);
            dialog.setValidationErrors(errors);
            dialog.showAndWait();
        });
    }

    // 用于跟踪鼠标拖动的内部类
    private static class Delta {
        double x, y;
    }
}