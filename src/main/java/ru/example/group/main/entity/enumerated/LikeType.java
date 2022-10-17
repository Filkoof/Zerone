package ru.example.group.main.entity.enumerated;

import java.util.Locale;

public enum LikeType {
    POST,
    COMMENT;

    public static LikeType getLikeTypeFromString(String code) {
        return switch (code.toUpperCase(Locale.getDefault())) {
            case "POST" -> POST;
            case "COMMENT" -> COMMENT;
            default -> null;
        };
    }
}
