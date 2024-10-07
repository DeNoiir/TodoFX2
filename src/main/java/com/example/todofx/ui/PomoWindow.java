package com.example.todofx.ui;

import com.example.todofx.entity.PomoTimer;
import com.example.todofx.service.PomoService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.paint.Color;

public class PomoWindow extends Stage {
    private final PomoService pomoService;
    private Label timerLabel;
    private Label stateLabel;
    private Button startButton;
    private Button pauseButton;
    private Button closeButton;
    private Timeline timeline;

    public PomoWindow(PomoService pomoService) {
        this.pomoService = pomoService;
        initUI();
        initTimer();
        updateTimerDisplay();
        if (pomoService.getTimer().isRunning()) {
            timeline.play();
        }
    }

    private void initUI() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("pomo-window");

        stateLabel = new Label();
        stateLabel.getStyleClass().add("pomo-state-label");

        timerLabel = new Label();
        timerLabel.getStyleClass().add("pomo-timer-label");

        startButton = new Button("开始");
        startButton.getStyleClass().addAll("button", "pomo-button");
        startButton.setOnAction(e -> startTimer());

        pauseButton = new Button("暂停");
        pauseButton.getStyleClass().addAll("button", "pomo-button");
        pauseButton.setOnAction(e -> pauseTimer());

        closeButton = new Button("关闭");
        closeButton.getStyleClass().addAll("button", "pomo-button");
        closeButton.setOnAction(e -> close());

        HBox buttonBox = new HBox(10, startButton, pauseButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(stateLabel, timerLabel, buttonBox);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/com/example/todofx/styles.css").toExternalForm());

        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        setAlwaysOnTop(true);

        root.setOnMousePressed(pressEvent -> root.setOnMouseDragged(dragEvent -> {
            setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));
    }

    private void initTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            pomoService.decrementTimer();
            updateTimerDisplay();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateTimerDisplay() {
        PomoTimer timer = pomoService.getTimer();
        int totalSeconds = pomoService.getRemainingTime();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        stateLabel.setText("当前状态: " + getStateString(timer.getState()));

        if (timer.isRunning()) {
            startButton.setDisable(true);
            pauseButton.setDisable(false);
        } else {
            startButton.setDisable(false);
            pauseButton.setDisable(true);
        }
    }

    private String getStateString(PomoTimer.PomoState state) {
        return switch (state) {
            case WORK -> "专注工作";
            case SHORT_BREAK -> "短休息";
            case LONG_BREAK -> "长休息";
            default -> "未知状态";
        };
    }

    private void startTimer() {
        try {
            pomoService.startTimer();
            timeline.play();
            startButton.setDisable(true);
            pauseButton.setDisable(false);
        } catch (Exception e) {
            Platform.runLater(() -> CustomDialog.showException(e));
        }
    }

    private void pauseTimer() {
        try {
            pomoService.pauseTimer();
            timeline.stop();
            startButton.setDisable(false);
            pauseButton.setDisable(true);
        } catch (Exception e) {
            Platform.runLater(() -> CustomDialog.showException(e));
        }
    }

    @Override
    public void close() {
        try {
            pomoService.stopTimer();
            timeline.stop();
            super.close();
        } catch (Exception e) {
            Platform.runLater(() -> CustomDialog.showException(e));
        }
    }
}