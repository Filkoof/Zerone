package ru.example.group.main.entity.enumerated;

import lombok.Getter;

@Getter
public enum NotificationType {
    POST(0),
    POST_COMMENT(1),
    COMMENT_COMMENT(2),
    FRIEND_REQUEST(3),
    MESSAGE(4),
    FRIEND_BIRTHDAY(5);

    private final long value;

    NotificationType(long value) {
        this.value = value;
    }

    public static NotificationType getTypeFromValue(Long value) {
        return switch (value.toString()) {
            case "0" -> POST;
            case "1" -> POST_COMMENT;
            case "2" -> COMMENT_COMMENT;
            case "3" -> FRIEND_REQUEST;
            case "4" -> MESSAGE;
            case "5" -> FRIEND_BIRTHDAY;
            default -> null;
        };
    }
}
