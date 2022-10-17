package ru.example.group.main.entity.enumerated;

import lombok.Getter;

@Getter
public enum FriendshipStatusType {
    REQUEST(1),
    FRIEND(2),
    BLOCKED(3),
    DECLINED(4),
    SUBSCRIBED(5),
    WAS_BLOCKED_BY(6),
    DEADLOCK(7);

    private final int value;

    FriendshipStatusType(int value) {
        this.value = value;
    }
}
