package com.example.todofx.service;

import com.example.todofx.entity.PomoTimer;
import com.example.todofx.ui.CustomDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class PomoService {
    private static final String TIMER_FILE = "pomo_timer.json";
    private final ObjectMapper objectMapper;
    private PomoTimer timer;

    public PomoService() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        File file = new File(TIMER_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                CustomDialog.showException(e);
            }
        }

        loadTimer();
    }

    public void loadTimer() {
        File file = new File(TIMER_FILE);
        if (file.exists() && file.length() > 0) {
            try {
                timer = objectMapper.readValue(file, PomoTimer.class);
            } catch (IOException e) {
                CustomDialog.showException(e);
                timer = new PomoTimer();
            }
        } else {
            timer = new PomoTimer();
        }
        timer.setCurrentCycle(getInitialTime(timer.getState()));
    }

    public void saveTimer() {
        try {
            objectMapper.writeValue(new File(TIMER_FILE), timer);
        } catch (IOException e) {
            CustomDialog.showException(e);
        }
    }

    public void startTimer() {
        timer.setRunning(true);
        timer.setLastStartTime(LocalDateTime.now());
        saveTimer();
    }

    public void stopTimer() {
        if (timer.isRunning()) {
            timer.setRunning(false);
            saveTimer();
        }
    }

    public void pauseTimer() {
        if (timer.isRunning()) {
            timer.setRunning(false);
            saveTimer();
        }
    }

    public void decrementTimer() {
        if (timer.isRunning() && timer.getCurrentCycle() > 0) {
            timer.setCurrentCycle(timer.getCurrentCycle() - 1);
            if (timer.getCurrentCycle() == 0) {
                completeCurrentCycle();
            }
            saveTimer();
        }
    }

    private int getInitialTime(PomoTimer.PomoState state) {
        return switch (state) {
            case WORK -> 25 * 60; // 25 minutes
            case SHORT_BREAK -> 5 * 60; // 5 minutes
            case LONG_BREAK -> 15 * 60; // 15 minutes
            default -> throw new IllegalStateException("Unexpected value: " + state);
        };
    }

    private void completeCurrentCycle() {
        switch (timer.getState()) {
            case WORK:
                timer.setTotalPomos(timer.getTotalPomos() + 1);
                if (timer.getTotalPomos() % 4 == 0) {
                    timer.setState(PomoTimer.PomoState.LONG_BREAK);
                } else {
                    timer.setState(PomoTimer.PomoState.SHORT_BREAK);
                }
                break;
            case SHORT_BREAK:
            case LONG_BREAK:
                timer.setState(PomoTimer.PomoState.WORK);
                break;
        }
        timer.setCurrentCycle(getInitialTime(timer.getState()));
    }

    public int getRemainingTime() {
        return timer.getCurrentCycle();
    }

    public PomoTimer getTimer() {
        return timer;
    }

    public void shutdown() {
        stopTimer();
    }
}