package ru.example.group.main.entity.enumerated;

import lombok.Getter;

@Getter
public enum ReadStatusType {
    SENT(0),
    READ(1);

    private final int value;

    ReadStatusType(int value) {
        this.value = value;
    }
}
