package com.example.todofx.entity;

import java.time.LocalDateTime;

public class PomoTimer {
    private int totalPomos;
    private int currentCycle;
    private LocalDateTime lastStartTime;
    private boolean isRunning;
    private PomoState state;

    public enum PomoState {
        WORK, SHORT_BREAK, LONG_BREAK
    }

    public PomoTimer() {
        this.totalPomos = 0;
        this.currentCycle = 0;
        this.isRunning = false;
        this.state = PomoState.WORK;
    }

    public int getTotalPomos() {
        return totalPomos;
    }

    public void setTotalPomos(int totalPomos) {
        this.totalPomos = totalPomos;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public void setCurrentCycle(int currentCycle) {
        this.currentCycle = currentCycle;
    }

    public LocalDateTime getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(LocalDateTime lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public PomoState getState() {
        return state;
    }

    public void setState(PomoState state) {
        this.state = state;
    }
}