package com.nhnacademy.event.domain;

public enum EventLevel {
    INFO(1),
    WARN(2),
    ERROR(3);

    private final int level;

    EventLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
