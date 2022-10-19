package ru.example.group.main.entity.enumerated;

import java.util.Locale;

public enum SupportRequestStatus {
    NEW,
    IN_WORK,
    CLOSED;

    public static String getStringFromSupportRequestStatus(SupportRequestStatus code) {
        return switch (code) {
            case NEW -> NEW.toString();
            case IN_WORK -> IN_WORK.toString();
            case CLOSED -> CLOSED.toString();
            default -> null;
        };
    }

    public static SupportRequestStatus getEnumFromString(String status) {
        return switch (status) {
            case "NEW" -> NEW;
            case "IN_WORK" -> IN_WORK;
            case "CLOSED" -> CLOSED;
            default -> null;
        };
    }
}
