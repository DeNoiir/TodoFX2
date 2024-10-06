package com.example.todofx.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionDialog extends Alert {
    public ExceptionDialog(Throwable ex) {
        super(AlertType.ERROR);

        setTitle("错误");
        setHeaderText("发生错误");
        setContentText(ex.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("异常堆栈跟踪:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        getDialogPane().setExpandableContent(expContent);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        // 应用CSS样式
        String cssPath = getClass().getResource("/com/example/todofx/styles.css").toExternalForm();
        getDialogPane().getStylesheets().add(cssPath);

        // 加载自定义字体
        Font.loadFont(getClass().getResourceAsStream("/com/example/todofx/FZfont140.TTF"), 14);

        // 应用自定义样式类
        getDialogPane().getStyleClass().add("exception-dialog");

        // 设置对话框大小
        setResizable(true);
        getDialogPane().setPrefSize(600, 400);

        // 确保字体应用到对话框的所有文本
        getDialogPane().lookupAll(".label").forEach(node -> node.setStyle("-fx-font-family: 'FZfont140';"));
        textArea.setStyle("-fx-font-family: 'FZfont140';");

        // 设置对话框图标（如果有的话）
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        // stage.getIcons().add(new Image(getClass().getResourceAsStream("/path/to/icon.png")));
    }
}