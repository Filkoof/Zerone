package ru.example.group.main.entity.enumerated;

public enum FriendshipStatusType {
    REQUEST,
    FRIEND,
    BLOCKED,
    DECLINED,
    SUBSCRIBED,
    WASBLOCKEDBY,
    DEADLOCK;

    public static Long getLongFromEnum(FriendshipStatusType friendshipStatusType) {
        return switch (friendshipStatusType) {
            case REQUEST -> 1L;
            case FRIEND -> 2L;
            case BLOCKED -> 3L;
            case DECLINED -> 4L;
            case SUBSCRIBED -> 5L;
            case WASBLOCKEDBY -> 6L;
            case DEADLOCK -> 7L;
        };
    }

    public static FriendshipStatusType getFriendshipFromString(String code) {
        return switch (code) {
            case "REQUEST" -> REQUEST;
            case "FRIEND" -> FRIEND;
            case "BLOCKED" -> BLOCKED;
            case "DECLINED" -> DECLINED;
            case "SUBSCRIBED" -> SUBSCRIBED;
            case "WASBLOCKEDBY" -> WASBLOCKEDBY;
            case "DEADLOCK" -> DEADLOCK;
            default -> null;
        };
    }
}
